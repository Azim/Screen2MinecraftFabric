package icu.azim.screen2mc.mixin;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;

import icu.azim.screen2mc.ColorUtils;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelStorage;

@Mixin(MinecraftServer.class)
public class ScreenMixin {
	@Shadow
	private Map<RegistryKey<World>, ServerWorld> worlds;
	private ServerWorld world;
	private Robot screen;
	private ColorUtils colors;
	private Rectangle screenRect = new Rectangle(0, 0, 1920, 1080);
	private BufferedImage old;

	@Inject(at = @At("RETURN"), method = "<init>(Ljava/lang/Thread;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/SaveProperties;Lnet/minecraft/resource/ResourcePackManager;Ljava/net/Proxy;Lcom/mojang/datafixers/DataFixer;"
			+ "Lnet/minecraft/resource/ServerResourceManager;Lcom/mojang/authlib/minecraft/MinecraftSessionService;Lcom/mojang/authlib/GameProfileRepository;Lnet/minecraft/util/UserCache;Lnet/minecraft/server/WorldGenerationProgressListenerFactory;)V")
	public void afterConstruct(Thread thread, DynamicRegistryManager.Impl impl, LevelStorage.Session session,
			SaveProperties saveProperties, ResourcePackManager resourcePackManager, Proxy proxy, DataFixer dataFixer,
			ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService,
			GameProfileRepository gameProfileRepository, UserCache userCache,
			WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo info) { //inject to the end of MinecraftServer constructor
		try {
			screen = new Robot();
			colors = new ColorUtils();
			System.out.println("Started screen capture");
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	//inject before each  tick
	@Inject(at = @At("HEAD"), method = "tick(Ljava/util/function/BooleanSupplier;)V")
	protected void tick(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
		if (screen == null)
			return;
		if (world == null)
			world = worlds.get(World.OVERWORLD);
		if (world == null)
			return;
		if (world.getPlayers().isEmpty())
			return;
		BufferedImage img = screen.createScreenCapture(screenRect);
		if (old == null) {
			old = img;
			return;
		}
		// block canvas  
		/*
			int y = 4; // 256 x 144 <- 1920 x 1080
			for (int zchunk = 0; zchunk < 9; zchunk++) {
				for (int xchunk = 0; xchunk < 16; xchunk++) {
					//WorldChunk chunk = world.getChunk(xchunk, zchunk); //changed to use  world instead, since clients didnt see changes  
					int zchunk16 = zchunk * 16;
					int xchunk16 = xchunk * 16;
					for (int z = 0; z < 16; z++) {
						for (int x = 0; x < 16; x++) {
							int fx = xchunk16 + x;
							int fz = zchunk16 + z;

							int canvasx = (int) (fx * 7.5);
							int canvasy = (int) (fz * 7.5);
							int color = img.getRGB(canvasx, canvasy);
							if (color == old.getRGB(canvasx, canvasy))
								continue;
							BlockState state = colors.findClosest(new Color(color));
							world.setBlockState(new BlockPos(fx, y, fz), state);
						}
					}
				}
			}
		}
		*/
		//map canvas
		//requires 15x9 map display filled from left to right from bottom to top
		List<ServerPlayerEntity> players = world.getPlayers();
		for (int x = 0; x < 15; x++) {
			for (int y = 0; y < 9; y++) {
				byte[] colors = new byte[128 * 128];
				int segmentx = x * 128;
				int segmenty = y * 128;
				for (int lx = 0; lx < 128; lx++) { // width
					for (int ly = 0; ly < 128; ly++) { // height
						int index = lx + ly * 128;
						if (ly + segmenty < 1080 && lx + segmentx < 1920) {
							colors[index] = this.colors.findClosestByte(new Color(img.getRGB(lx + segmentx, ly + segmenty)));
						} else { //outside of the screenshot but still inside of the canvas
							colors[index] = 119;
						}
					}
				}
				MapUpdateS2CPacket packet = new MapUpdateS2CPacket(x + (8 - y) * 15, (byte) 3, false, false,
						Collections.emptyList(), colors, 0, 0, 128, 128);
				players.forEach(p -> {
					ServerSidePacketRegistry.INSTANCE.sendToPlayer(p, packet);
				});
			}
		}
		old = img;
	}
}
