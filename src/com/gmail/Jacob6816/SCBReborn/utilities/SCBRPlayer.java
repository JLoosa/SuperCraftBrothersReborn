package com.gmail.Jacob6816.SCBReborn.utilities;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.classes.ConfigurablePlayerClass;
import com.gmail.Jacob6816.SCBReborn.classes.PlayerClassManager;

public class SCBRPlayer {

	private Player player;
	private ConfigurablePlayerClass playerClass;
	private int livesRemaining;
	private PlayerBackup playerBackup;

	public SCBRPlayer(Player player, Arena game) {
		this.player = player;
		playerBackup = new PlayerBackup();
		playerBackup.saveState(this.player);
		this.playerClass = PlayerClassManager.getInstance().getPlayerClasses()[0];
		livesRemaining = game.getLivesForArena();
		teleport(game.getLobbyLocation());
	}

	public void setPlayerClass(ConfigurablePlayerClass configurablePlayerClass) {
		this.playerClass = configurablePlayerClass;
	}

	public void setLevel(int level) {
		player.setLevel(level);
	}

	public void applyClass() {
		player.getInventory().setArmorContents(playerClass.getClassArmor());
		player.getInventory().setContents(playerClass.getClassInventory());
	}

	public void teleport(Location location) {
		player.teleport(location);
	}

	public boolean isThreat() {
		return player.getHealth() > 0;
	}

	public double distanceTo(Location target) {
		return player.getLocation().distance(target);
	}

	public int reduceLives() {
		return reduceLives(1);
	}

	public int reduceLives(int amount) {
		this.livesRemaining -= amount;
		return livesRemaining;
	}

	public void sendMessage(String string) {
		player.sendMessage(string);
	}

	public ConfigurablePlayerClass getPlayerClass() {
		return playerClass;
	}

	public String getName() {
		return player.getName();
	}

	public void setScoreboard(Scoreboard scoreboard) {
		player.setScoreboard(scoreboard);
	}

	public boolean stillHasLives() {
		return livesRemaining >= 1;
	}

	public boolean fastCheck(Player check) {
		return player.hashCode() == check.hashCode();
	}

	public void dispose() {
		player = null;
		playerClass = null;
		livesRemaining = 0;
		playerBackup.dispose();
		playerBackup = null;
	}

	public void restorePlayer() {
		playerBackup.loadState(player);
	}

	private class PlayerBackup {

		private double health;
		private ItemStack[] armor, inventory;
		private int foodLevel, totalExperience;
		private Collection<PotionEffect> potionEffects;
		private Location lastLocation;

		public void saveState(Player player) {
			health = player.getHealth();
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			armor = player.getInventory().getArmorContents();
			inventory = player.getInventory().getContents();
			player.getInventory().setArmorContents(new ItemStack[0]);
			player.getInventory().clear();
			foodLevel = player.getFoodLevel();
			player.setFoodLevel(20);
			totalExperience = player.getTotalExperience();
			player.setTotalExperience(0);
			potionEffects = player.getActivePotionEffects();
			if (!potionEffects.isEmpty()) {
				Iterator<PotionEffect> iterator = player.getActivePotionEffects().iterator();
				while (iterator.hasNext())
					player.removePotionEffect(iterator.next().getType());
			}
			lastLocation = player.getLocation();
		}

		public void dispose() {
			health = 0;
			foodLevel = 0;
			totalExperience = 0;
			armor = null;
			inventory = null;
			potionEffects = null;
			lastLocation = null;

		}

		public void loadState(Player player) {
			player.getInventory().setArmorContents(armor);
			player.getInventory().setContents(inventory);
			player.setHealth(health);
			player.setTotalExperience(totalExperience);
			player.setFoodLevel(foodLevel);
			player.addPotionEffects(potionEffects);
			player.teleport(lastLocation);
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}

	}
}
