package com.gmail.Jacob6816.SCBReborn.arenas;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;

public class ArenaConfigurationReader {

	private File				arenaFile;
	private FileConfiguration	config;
	private String				keywordRegion	= "Region.";
	private String				keywordSign		= "Signs.";
	private String				keywordLocation	= "Location.";

	public ArenaConfigurationReader(File arenaFile) {
		this.arenaFile = arenaFile;
		reloadConfigurationFile();
	}

	public ArenaConfigurationReader(String newFile) {
		File arenaDirectory = new File(SCBReborn.getSCBR().getDataFolder(), "Arenas");
		if (!arenaDirectory.exists()) arenaDirectory.mkdirs();
		arenaFile = new File(arenaDirectory, newFile.toUpperCase());
		if (!arenaFile.exists())
			try {
				arenaFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		reloadConfigurationFile();
		config.set("NAME", newFile);
	}

	/* Getters */

	public String getGameName() {
		return config.getString("NAME");
	}

	public ArrayList<Location> getGameSpawnPoints() {
		ArrayList<Location> locations = new ArrayList<Location>();
		ConfigurationSection spawnSection = config.getConfigurationSection(keywordLocation + ".Spawns");
		if (spawnSection == null) return locations;
		Map<String, Object> points = spawnSection.getValues(false);
		for (Object o : points.values())
			try {
				String s = (String) o;
				Location parsed = str2loc(s);
				locations.add(parsed);
			} catch (Exception exc) {
			}
		return locations;
	}

	public CuboidSelection getCuboidSelection() {
		String worldName = config.getString(keywordRegion + "world");
		World world = Bukkit.getWorld(worldName);
		int minX = config.getInt(keywordRegion + "minX");
		int minY = config.getInt(keywordRegion + "minY");
		int minZ = config.getInt(keywordRegion + "minZ");
		int maxX = config.getInt(keywordRegion + "maxX");
		int maxY = config.getInt(keywordRegion + "maxY");
		int maxZ = config.getInt(keywordRegion + "maxZ");
		return new CuboidSelection(world, new Vector(minX, minY, minZ), new Vector(maxX, maxY, maxZ));
	}

	public Map<String, Object> getSignMap() {
		ConfigurationSection mapSection = config.getConfigurationSection(keywordSign);
		if (mapSection == null) return new HashMap<String, Object>();
		return mapSection.getValues(false);
	}

	public Location getGameLobby() {
		String location = config.getString(keywordLocation + ".Lobby", SCBReborn.getSCBR().getConfig().getString("GLOBAL.Lobby", null));
		Location ans = (location == null ? null : str2loc(location));
		if (ans == null) ans = Bukkit.getWorlds().get(0).getSpawnLocation();
		return ans;
	}

	/* Setters */

	public void setCuboidSelection(CuboidSelection gameRegion) {
		Vector lowest = gameRegion.getNativeMinimumPoint();
		Vector highest = gameRegion.getNativeMaximumPoint();
		config.set(keywordRegion + "world", gameRegion.getWorld().getName());
		config.set(keywordRegion + "minX", lowest.getBlockX());
		config.set(keywordRegion + "minY", lowest.getBlockY());
		config.set(keywordRegion + "minZ", lowest.getBlockZ());
		config.set(keywordRegion + "maxX", highest.getBlockX());
		config.set(keywordRegion + "maxY", highest.getBlockY());
		config.set(keywordRegion + "maxZ", highest.getBlockZ());
		saveConfigurationFile();
	}

	public void setSignMap(Map<String, Object> map) {
		config.set(keywordSign.substring(0, keywordSign.length() - 1), null);
		if (!map.isEmpty()) {
			int i = 0;
			for (Object value : map.values()) {
				config.set(keywordSign + i, value);
				i++;
			}
		}
		saveConfigurationFile();
	}

	public void setGameLobby(Location lobbyLocation) {
		config.set(keywordLocation + ".Lobby", loc2str(lobbyLocation));
		saveConfigurationFile();
	}

	public void setSpawnPoints(ArrayList<Location> spawnPoints) {
		String key = keywordLocation + ".Spawns.";
		config.set(key.substring(0, key.length() - 1), null);
		int i = 0;
		for (Location location : spawnPoints) {
			config.set(key + i, loc2str(location));
			i++;
		}
		saveConfigurationFile();
	}

	/* Private Methods */

	public static Location str2loc(String input) {
		if (input.length() <= 0 || !input.contains(":")) return null;
		String[] splitString = input.split(":");
		if (splitString.length < 4) return null;
		World world = Bukkit.getWorld(splitString[0]);
		if (world == null) return null;
		Location location = new Location(world, Double.parseDouble(splitString[1]), Double.parseDouble(splitString[2]), Double.parseDouble(splitString[3]));
		if (splitString.length == 6) {
			location.setYaw(Float.parseFloat(splitString[4]));
			location.setPitch(Float.parseFloat(splitString[5]));
		}
		return location;
	}

	public static String loc2str(Location input) {
		if (input == null) return null;
		String world = input.getWorld().getName();
		StringBuilder builder = new StringBuilder(world);
		String x = num2str(input.getX(), 2);
		builder.append(":" + x);
		String y = num2str(input.getY(), 2);
		builder.append(":" + y);
		String z = num2str(input.getZ(), 2);
		builder.append(":" + z);
		if (Math.abs(input.getPitch()) > 15 || Math.abs(input.getY()) > 15) {
			String yaw = num2str(input.getYaw(), 2);
			builder.append(":" + yaw);
			String pitch = num2str(input.getPitch(), 2);
			builder.append(":" + pitch);
		}
		return builder.toString().trim();
	}

	public static String num2str(Number n, int length) {
		String s = "" + n.doubleValue();
		int deci = s.indexOf(".");
		return s.substring(0, Math.min(s.length(), deci + length));
	}

	public void saveConfigurationFile() {
		try {
			config.save(arenaFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reloadConfigurationFile() {
		this.config = YamlConfiguration.loadConfiguration(arenaFile);
	}

}
