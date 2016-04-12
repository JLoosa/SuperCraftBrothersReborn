package com.gmail.Jacob6816.SCBReborn.arenas;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;

public class ArenaManager {

	private static ArenaManager	instance;
	private BukkitTask			tickRunnable;

	private ArenaManager() {
		arenas = new ArrayList<Arena>();
	}

	public void beginRunnable() {
		SCBReborn scbr = SCBReborn.getSCBR();
		if (scbr == null) return;
		tickRunnable = new BukkitRunnable() {
			int	curr	= 0;

			public void run() {
				List<Arena> arenas = getAllArenas();
				if (arenas.isEmpty()) return;
				if (curr % 20 != 0)
				for (Arena a : arenas)
					a.onTick();
				else
				for (Arena a : arenas) {
					a.onTick();
					a.onSecond();
				}

			}
		}.runTaskTimer(scbr, 5, 1);
	}

	private ArrayList<Arena>	arenas;

	public static ArenaManager getAM() {
		if (instance == null) instance = new ArenaManager();
		return instance;
	}

	public void dispose() {
		disableAllArenas();
		if (tickRunnable != null)
			tickRunnable.cancel();
		tickRunnable = null;
		instance = null;
	}

	public void addArenaToList(Arena arena) {
		if (!arenas.contains(arena)) arenas.add(arena);
	}

	public List<Arena> getAllArenas() {
		return Collections.unmodifiableList(arenas);
	}

	public void runGameTicks() {
		tickRunnable = new BukkitRunnable() {
			int	tick	= 1;

			public void run() {
				if (arenas.isEmpty()) return;
				for (Arena a : arenas)
					a.onTick();
				if (tick == 0)
					for (Arena a : arenas)
						a.onSecond();
				tick++;
				tick %= 20;
			}
		}.runTaskTimer(SCBReborn.getSCBR(), 0, 1);
	}

	public void loadGames() {
		File arenaDirectory = new File(SCBReborn.getSCBR().getDataFolder(), "Arenas");
		if (!arenaDirectory.exists() && !arenaDirectory.mkdirs()) {
			SCBReborn.getSCBR().getLogger().severe("Could Not Create SCBReborn Directory");
		}
		if (!arenaDirectory.exists()) arenaDirectory.mkdirs();
		File[] arenaFiles = arenaDirectory.listFiles();
		if (arenaFiles.length == 0) return;
		for (File file : arenaFiles) {
			Arena arena = new Arena(file);
			arenas.add(arena);
			SCBReborn.getSCBR().getLogger().info("Loaded arena: " + arena.getGameName());
			arena.tryEnable();
		}
	}

	public Arena getPlayerArena(Player player) {
		if (arenas.isEmpty()) return null;
		for (Arena arena : arenas)
			if (arena.hasPlayer(player)) return arena;
		return null;
	}

	public int getArenaCount() {
		return arenas.size();
	}

	public Arena getArena(String name) {
		if (arenas.isEmpty()) return null;
		for (Arena arena : arenas)
			if (arena.getGameName().equalsIgnoreCase(name)) return arena;
		return null;
	}

	public void disableAllArenas() {
		if (arenas.isEmpty()) return;
		for (Arena arena : arenas)
			arena.disableArena();
	}

}
