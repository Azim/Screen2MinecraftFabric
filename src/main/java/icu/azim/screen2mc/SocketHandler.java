package icu.azim.screen2mc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class SocketHandler implements Runnable {

	private Socket client;
	private MinecraftServer server;
	private World world;
    PrintWriter out;
    BufferedReader in;
    private String password;
    private int screenHeight;
    private int screenWidth;
    private String worldName;
    private int xoffset;
    private int yoffset;
    private int zoffset;
    private int bufferSize;
	
	public SocketHandler(Socket client, MinecraftServer server) {
		this.client = client;
		this.server = server;
		this.world = this.server.getOverworld();
	}

	@Override
	public void run() {
		try {
			out = new PrintWriter(client.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(client.getInputStream()));

	        // reading configs from client
	        password = in.readLine();
	        screenHeight = Integer.parseInt(in.readLine());
	        screenWidth = Integer.parseInt(in.readLine());
	        worldName = in.readLine();
	        xoffset = Integer.parseInt(in.readLine());
	        yoffset = Integer.parseInt(in.readLine());
	        zoffset = Integer.parseInt(in.readLine());
	        bufferSize = screenHeight * screenWidth * 3;
	        
	        if(!password.equals("astream13")) {
                System.out.println("Client tried to connect with wrong password!");
                client.close();
                return;
            }
	        
	        int index = 0;
	        while(!client.isClosed()) {
	        	
	        	
	        	
	        	
	        	
	        }
	        
	        
	        
	        
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
