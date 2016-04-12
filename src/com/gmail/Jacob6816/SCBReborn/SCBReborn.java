package com.gmail.Jacob6816.SCBReborn;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;
import com.gmail.Jacob6816.SCBReborn.classes.PlayerClassManager;
import com.gmail.Jacob6816.SCBReborn.events.SCBREventHandler;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class SCBReborn extends JavaPlugin {

	private static SCBReborn	instance;

	@Override
	public void onDisable() {
		ArenaManager.getAM().disableAllArenas();
		ArenaManager.getAM().dispose();
		PlayerClassManager.getInstance().saveAllClassesToFile();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		saveConfig();
		instance = null;
		super.onDisable();
	}

	@Override
	public void onEnable() {
		if (getWorldEdit() == null) {
			getLogger().severe("WorldEdit could not be found. Please make sure that it is correctly installed.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		instance = this;
		if (!getDataFolder().exists()) getDataFolder().mkdirs();
		if (getConfig() == null) {
			getConfig().addDefault("Game.MinPlayers", 2);
			getConfig().addDefault("Game.MaxPlayers", 8);
			getConfig().addDefault("Game.PlayerLives", 8);
			saveDefaultConfig();
		}
		PlayerClassManager.getInstance().setClassInv();
		Bukkit.getPluginManager().registerEvents(new SCBREventHandler(), this);
		getCommand("SCBReborn").setExecutor(new SCBRCommandExecutor());
		super.onEnable();
		ArenaManager.getAM().loadGames();
		ArenaManager.getAM().beginRunnable();
	}

	public static SCBReborn getSCBR() {
		return instance;
	}

	public static WorldEditPlugin getWorldEdit() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
		if (plugin == null || !(plugin instanceof WorldEditPlugin)) return null;
		return (WorldEditPlugin) plugin;
	}

}