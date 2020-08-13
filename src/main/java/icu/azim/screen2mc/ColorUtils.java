package icu.azim.screen2mc;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.block.BlockState;
import net.minecraft.block.MaterialColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


//class originally by Chicken but at this point it is a collective work anyway
public class ColorUtils {
	HashMap<BlockState, Color> blocks;
	HashMap<Color, BlockState> cache;
	HashMap<Integer, Color> colors;
	HashMap<Color, Integer> bytecache;
	
	public ColorUtils() {
		// loading colors
		blocks = new HashMap<>();
		cache = new HashMap<>();
		bytecache = new HashMap<>();
		colors = new HashMap<>();
		try {
			InputStream blockInputStream = Screen2Minecraft.class.getClassLoader().getResourceAsStream("assets/screen2mc/baked_blocks.json");
			InputStreamReader blockIsReader = new InputStreamReader(blockInputStream);
			BufferedReader blockReader = new BufferedReader(blockIsReader);
			StringBuffer blockStringBuilder = new StringBuffer();
			String curStr;

			while ((curStr = blockReader.readLine()) != null) {
				blockStringBuilder.append(curStr);
			}

			String bakedBlocks = blockStringBuilder.toString();
			JsonArray blockDatas = (JsonArray) (new Gson()).fromJson(bakedBlocks, JsonArray.class);
			for (int k = 0; k < blockDatas.size(); k++) {
				JsonObject blockData = blockDatas.get(k).getAsJsonObject();
				blocks.put(Registry.BLOCK.get(new Identifier(blockData.get("game_id_13").getAsString())).getDefaultState(),
						new Color(blockData.get("red").getAsInt(), blockData.get("green").getAsInt(),
								blockData.get("blue").getAsInt()));
			}

			System.out.println("Loaded " + String.valueOf(blocks.size()) + " blocks / colors.");
		} catch (IOException e) {
			System.out.println("Error while trying to load colors.");
			e.printStackTrace();
		}
		for(int i = 1; i < 64; i++) {
			MaterialColor c = MaterialColor.COLORS[i];
			if(c==null)continue;
			for(int j = 0; j < 4; j++) {
				colors.put(i*4+j, new Color(getRenderColor(c, j)));
			}
		}
		
	}
	
	public byte findClosestByte(Color c) {
		double closestDist = Double.MAX_VALUE;
		if(bytecache.containsKey(c)) {
			return bytecache.get(c).byteValue();
		}
		
		int closestBlock = 0;
		for (Entry<Integer, Color> entry : colors.entrySet()) {
			int state = entry.getKey();
			Color value = entry.getValue();
			double distance = dist(c, value);
			if (distance < closestDist) {
				closestDist = distance;
				closestBlock = state;
			}
		}
		bytecache.put(c, closestBlock);
		return (byte) closestBlock;
	}
	
	public int getRenderColor(MaterialColor material, int shade) {
	      int i = 220;
	      if (shade == 3) {
	         i = 135;
	      }

	      if (shade == 2) {
	         i = 255;
	      }

	      if (shade == 1) {
	         i = 220;
	      }

	      if (shade == 0) {
	         i = 180;
	      }

	      int j = (material.color & 255) * i / 255;
	      int k = (material.color >> 8 & 255) * i / 255;
	      int l = (material.color >> 16 & 255) * i / 255;
	      return -16777216 | l << 16 | k << 8 | j;
	   }
	
	public BlockState findClosest(Color c) {
		double closestDist = Double.MAX_VALUE;
		BlockState closestBlock;
		closestBlock = cache.get(c);
		if(closestBlock!=null) {
			return closestBlock;
		}
		
		for (Map.Entry<BlockState, Color> entry : blocks.entrySet()) {
			BlockState state = entry.getKey();
			Color value = entry.getValue();
			double distance = dist(c, value);
			if (distance < closestDist) {
				closestDist = distance;
				closestBlock = state;
			}
		}
		cache.put(c, closestBlock);
		return closestBlock;
	}

	public double dist(Color a, Color b) {
		int r1 = a.getRed();
		int g1 = a.getGreen();
		int b1 = a.getBlue();
		int r2 = b.getRed();
		int g2 = b.getGreen();
		int b2 = b.getBlue();
		double drp2 = Math.pow(r1 - r2, 2);
		double dgp2 = Math.pow(g1 - g2, 2);
		double dbp2 = Math.pow(b1 - b2, 2);
		int t = (r1 + r2) / 2;
		return Math.sqrt(2 * drp2 + 4 * dgp2 + 3 * dbp2 + t * (drp2 - dbp2) / 256);
	}
}
