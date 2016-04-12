package com.gmail.Jacob6816.SCBReborn.utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.Jacob6816.SCBReborn.arenas.Arena;

public class Lobbyboard {

	private String		currentlyWaiting	= ChatColor.AQUA + "Players: ";
	private String		playersNeeded		= ChatColor.RED + "Needed: ";
	private Scoreboard	ingameScoreboard	= null;
	private Arena		arenaObject			= null;
	private String		scoreboardTitle		= null;
	private int			requiredToStart		= 8;
	private int			players				= 0;
	private int			countdown			= 30;
	private boolean		countingDown		= false;

	public Lobbyboard(Arena arena) {
		this.arenaObject = arena;
		ingameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		scoreboardTitle = ChatColor.AQUA + arena.getGameName() + " Lobby";
	}

	public void preformInitialSetup() {
		// Create Started Scoreboard
		ingameScoreboard.registerNewObjective(scoreboardTitle, "dummy");
		Objective waitingObjective = ingameScoreboard.getObjective(currentlyWaiting);
		waitingObjective.setDisplayName(currentlyWaiting);
		ingameScoreboard.clearSlot(DisplaySlot.SIDEBAR);
		waitingObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public void setMinimumPlayers(int newMinimum) {
		this.requiredToStart = newMinimum;
	}

	public void updateLobby(int playerCount) {
		Objective objective = ingameScoreboard.getObjective(DisplaySlot.SIDEBAR);
		objective.getScore(currentlyWaiting).setScore(playerCount);
		objective.getScore(playersNeeded).setScore(requiredToStart - playerCount);
		this.players = playerCount;
		if (countingDown) {
			if (objective.getScore(playersNeeded).getScore() > 0) {
				countingDown = false;
				currentCount = 0;
			}
		}
	}

	int	currentCount	= 0;

	public void startGame(boolean checkPlayers, boolean runCountdown) {
		if (checkPlayers)
			if (players - requiredToStart < 0) {
				currentCount = 0;
				return;
			}
		if (runCountdown) {
			if (!countingDown) {
				countingDown = true;
				return;
			}
			if (countdown - currentCount <= 0) {
				forceStart();
				return;
			}
			currentCount++;
			if ((countdown - currentCount) % 5 == 0 || (countdown - currentCount) < 5) {
				MessageManager.messageGame(arenaObject, "Game Starting in: " + (countdown - currentCount));
			}
		}
		else forceStart();
	}

	public void forceStart() {
		// TODO start the game
	}

	public void dispose() {
		currentlyWaiting = null;
		playersNeeded = null;
		ingameScoreboard = null;
		arenaObject = null;
		scoreboardTitle = null;
		requiredToStart = 0;
		players = 0;
		countdown = 0;
		countingDown = false;
		currentCount = 0;
	}
}
