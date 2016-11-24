package com.gmail.Jacob6816.SCBReborn.arenas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;

public class SimpleArenaSignManager implements ConfigurationSerializable {

	/* Sign Layout
	 * 
	 * [SCBReborn] [Game ID] [Game State] [# of Players] */

	private List<Sign>		signList;
	private final ChatColor	tagColor	= ChatColor.AQUA;
	private Arena			arena;

	public SimpleArenaSignManager(Sign... signs) {
		signList = new ArrayList<Sign>(Arrays.asList(signs));
		new BukkitRunnable() {
			public void run() {
				updateAllSigns();
			}
		}.runTaskTimer(SCBReborn.getSCBR(), 20, 20);
	}

	public SimpleArenaSignManager(Map<String, Object> constructorMap, Arena arena) {
		signList = new ArrayList<Sign>();
		this.arena = arena;
		if (constructorMap == null || constructorMap.isEmpty())
			return;
		for (Object obj : constructorMap.values()) {
			String location = (String) obj;
			Location loc = stringtoLocation(location);
			if (loc == null || !(loc.getBlock().getState() instanceof Sign)) continue;
			signList.add((Sign) loc.getBlock().getState());
		}
	}

	public void addSigns(Sign... newSigns) {
		for (Sign s : newSigns) {
			signList.add(s);
			updateSign(s);
		}
	}

	public void updateSign(Sign s) {
		s.setLine(0, tagColor + "[SCBReborn]");
		s.setLine(1, arena.getGameName());
		s.setLine(2, arena.getGameState().displayText());
		s.setLine(3, arena.getPlayers().size() + " / " + arena.getMaxPlayers());
		s.update(true, true);
		s.getBlock().getState().update(true);
	}

	public void updateAllSigns() {
		if (signList.isEmpty()) return;
		for (Sign sign : signList)
			if (sign.getChunk().isLoaded())
				try {
					updateSign(sign);
				} catch (Exception exc) {
					signList.remove(sign);
				}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> signMap = new HashMap<String, Object>();
		if (!signList.isEmpty()) {
			int pos = 0;
			for (Sign sign : signList) {
				signMap.put("sign." + pos, locationToString(sign.getLocation()));
				pos++;
			}
		}
		return signMap;
	}

	private String locationToString(Location blockLocation) {
		StringBuilder sb = new StringBuilder(blockLocation.getWorld().getName());
		sb.append(":" + blockLocation.getBlockX());
		sb.append(":" + blockLocation.getBlockY());
		sb.append(":" + blockLocation.getBlockZ());
		return sb.toString().trim();
	}

	private Location stringtoLocation(String locationString) {
		if (!locationString.contains(":")) return null;
		String[] components = locationString.split(":");
		if (components.length < 4) return null;
		World world = Bukkit.getWorld(components[0]);
		if (world == null) return null;
		int[] worldCoords = new int[3];
		for (int i = 0; i < 3; i++)
			worldCoords[i] = Integer.parseInt(components[i + 1]);
		return new Location(world, worldCoords[0], worldCoords[1], worldCoords[2]);
	}
}
