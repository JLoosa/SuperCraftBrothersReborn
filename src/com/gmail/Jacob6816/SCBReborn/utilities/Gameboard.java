package com.gmail.Jacob6816.SCBReborn.utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.classes.ConfigurablePlayerClass;
import com.gmail.Jacob6816.SCBReborn.classes.PlayerClassManager;

public class Gameboard {

	private String		livesDisplay		= ChatColor.AQUA + "Lives";
	private String		healthDisplay		= ChatColor.RED + "HP";
	private Scoreboard	ingameScoreboard	= null;
	private Arena		arenaObject			= null;

	public Gameboard(Arena arena) {
		this.arenaObject = arena;
		ingameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
	}

	public void preformInitialSetup() {
		// Lives Objective
		ingameScoreboard.registerNewObjective(livesDisplay, "dummy");
		Objective livesObjective = ingameScoreboard.getObjective(livesDisplay);
		livesObjective.setDisplayName(livesDisplay);
		ingameScoreboard.clearSlot(DisplaySlot.SIDEBAR);
		livesObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

		// Player Health
		ingameScoreboard.registerNewObjective(healthDisplay, "health");
		Objective healthObjective = ingameScoreboard.getObjective(healthDisplay);
		healthObjective.setDisplayName(healthDisplay);
		ingameScoreboard.clearSlot(DisplaySlot.BELOW_NAME);
		healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

		// TODO Register Class Teams
		ConfigurablePlayerClass[] classes = PlayerClassManager.getInstance().getPlayerClasses();
		if (classes.length == 0) return;
		for (ConfigurablePlayerClass playerClass : classes) {
			Team team = ingameScoreboard.registerNewTeam(playerClass.getClassName());
			team.setPrefix(playerClass.getClassColor().toString() + "[");
			team.setSuffix("]" + ChatColor.RESET.toString());
			team.setAllowFriendlyFire(true);
			team.setCanSeeFriendlyInvisibles(false);
		}
	}

	public void reallocateTeams() {
		for (SCBRPlayer player : arenaObject.getPlayers()) {
			ConfigurablePlayerClass playerClass = player.getPlayerClass();
			ingameScoreboard.getTeam(playerClass.getClassName()).addEntry(player.getName());
		}
	}

	public void applyToPlayers() {
		for (SCBRPlayer player : arenaObject.getPlayers()) {
			player.setScoreboard(ingameScoreboard);
		}
	}

}
