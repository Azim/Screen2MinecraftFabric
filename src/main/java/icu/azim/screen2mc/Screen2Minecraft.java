package icu.azim.screen2mc;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.util.Pair;

public class Screen2Minecraft implements DedicatedServerModInitializer {
	
	public static boolean mapmode = false;
	public static Vector3d blockCanvasOffset;
	public static Pair<Integer, Integer> blockCanvasSize;
	public static int mapIdOffset = 0;
	
	@Override
	public void onInitializeServer() {
		blockCanvasOffset = new Vector3d(0, 4, 0);
		blockCanvasSize = new Pair<Integer, Integer>(256, 144);
		System.out.println("Init");
	}
}
