package icu.azim.screen2mc;

import net.fabricmc.api.DedicatedServerModInitializer;

public class Screen2Minecraft implements DedicatedServerModInitializer {
	public static boolean started = false;

	@Override
	public void onInitializeServer() {
		// TODO Auto-generated method stub
		/*
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            if(dedicated) {
                dispatcher.register(CommandManager.literal("stream").executes(context -> {
                	if(started) {
                		System.out.println("already started");
                		return 0;
                	}
                    MinecraftServer server = context.getSource().getMinecraftServer();
                    try {
            			serversocket = new ServerSocket(25566);
            			new Thread() {
            				public void run() {
            					try {
            						Socket client = serversocket.accept();
            						Runnable socketHandler = new SocketHandler(client, server);
            						new Thread(socketHandler).start();
            						System.out.println("client connected");
            					} catch (IOException e) {
            						e.printStackTrace();
            						started = false;
            					}
            				}
            			}.start();
            		} catch (IOException e) {
            			e.printStackTrace();
            			started = false;
            		}
                    return 0;
                }));
            }
        });
        */
		System.out.println("loaded stuff, mixin should be ready too tbh");
	}
}
