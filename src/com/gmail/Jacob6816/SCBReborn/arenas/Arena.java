package com.gmail.Jacob6816.SCBReborn.arenas;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;
import com.gmail.Jacob6816.SCBReborn.classes.PlayerClassManager;
import com.gmail.Jacob6816.SCBReborn.utilities.Gameboard;
import com.gmail.Jacob6816.SCBReborn.utilities.Lobbyboard;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;
import com.gmail.Jacob6816.SCBReborn.utilities.SCBRPlayer;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;

public class Arena {

	private String						gameName;
	private ArrayList<Location>			spawnPoints;
	private CuboidSelection				gameRegion;
	private ArenaState					gameState;
	private Gameboard					gameboard;
	private Lobbyboard					lobbyboard;
	private SimpleArenaSignManager		signManager;
	private int							minPlayers;
	private int							maxPlayers;
	private HashMap<String, SCBRPlayer>	players;
	private int							numberOfLives;
	private Location					gameLobby;
	private ArenaConfigurationReader	arenaConfigurationReader;

	public Arena(File gameFile) {
		ArenaConfigurationReader acr = new ArenaConfigurationReader(gameFile);
		gameRegion = acr.getCuboidSelection();
		finishSetup(acr);
	}

	public Arena(String name, CuboidSelection gameRegion) {
		ArenaConfigurationReader acr = new ArenaConfigurationReader(name);
		this.gameRegion = gameRegion;
		acr.setCuboidSelection(gameRegion);
		finishSetup(acr);
	}

	private void finishSetup(ArenaConfigurationReader acr) {
		this.gameName = acr.getGameName();
		gameState = ArenaState.DISABLED;
		gameLobby = acr.getGameLobby();
		players = new HashMap<String, SCBRPlayer>();
		lobbyboard = new Lobbyboard(this);
		signManager = new SimpleArenaSignManager(acr.getSignMap(), this);
		this.minPlayers = SCBReborn.getSCBR().getConfig().getInt("I:MinPlayers", 2);
		this.maxPlayers = SCBReborn.getSCBR().getConfig().getInt("I:MaxPlayers", 8);
		this.numberOfLives = SCBReborn.getSCBR().getConfig().getInt("I:PlayerLives", 3);
		lobbyboard.setMinimumPlayers(minPlayers);
		spawnPoints = acr.getGameSpawnPoints();
		signManager.updateAllSigns();
		this.arenaConfigurationReader = acr;
	}

	public boolean tryEnable() {
		if (gameName == null) return false;
		if (spawnPoints.size() == 0) return false;
		if (PlayerClassManager.getInstance().getPlayerClasses().length == 0) return false;
		gameState = ArenaState.LOBBY;
		signManager.updateAllSigns();
		return true;
	}

	public Collection<SCBRPlayer> getPlayers() {
		return Collections.unmodifiableCollection(players.values());
	}

	public SCBRPlayer getSCBRPlayerFor(Player player) {
		if (players.isEmpty()) return null;
		for (String scbrp : players.keySet())
			if (scbrp.equals(player.getName()))
				return players.get(scbrp);
		return null;
	}

	public boolean hasPlayer(Player player) {
		return getSCBRPlayerFor(player) != null;
	}

	private Location locateSafeSpawn() {
		double safetyScore = Integer.MIN_VALUE;
		int spawnIndex = -1;
		for (int i = 0; i < spawnPoints.size(); i++) {
			double totalDistance = 0;
			for (SCBRPlayer player : players.values())
				if (player.isThreat())
					totalDistance += player.distanceTo(spawnPoints.get(i));
			double score = totalDistance * totalDistance;
			if (score > safetyScore) {
				safetyScore = score;
				spawnIndex = i;
			}
		}
		return spawnPoints.get(spawnIndex);
	}

	public CuboidSelection getGameRegion() {
		return gameRegion;
	}

	public void respawnPlayer(PlayerRespawnEvent event) {
		if (players.isEmpty()) return;
		for (SCBRPlayer ingame : players.values()) {
			if (!ingame.getName().equals(event.getPlayer().getName())) continue;
			if (ingame.stillHasLives())
				event.setRespawnLocation(locateSafeSpawn());
		}
		return;
	}

	public String getGameName() {
		return this.gameName;
	}

	public boolean isBlockInArena(Location block) {
		return gameRegion.contains(block);
	}

	public ArenaState getGameState() {
		return gameState;
	}

	public void startGame() {
		lobbyboard.dispose();
		gameboard = new Gameboard(this);
		gameboard.preformInitialSetup();
		gameboard.reallocateTeams();
		gameboard.applyToPlayers();
		this.gameState = ArenaState.INGAME;
		signManager.updateAllSigns();
	}

	public void onTick() {
		//TODO
	}

	public boolean isJoinable() {
		if (gameState != ArenaState.LOBBY) return false;
		return players.size() < maxPlayers;
	}

	public void onSecond() {
		if (lobbyboard != null) lobbyboard.startGame(true, true);

	}

	public int getLivesForArena() {
		return numberOfLives;
	}

	public void disableArena() {
		gameState = ArenaState.DISABLED;
		lobbyboard = null;
		gameboard = null;
		if (players.isEmpty()) return;
		for (SCBRPlayer player : players.values()) {
			player.restorePlayer();
			player.dispose();
		}
		players.clear();
		arenaConfigurationReader.setSpawnPoints(spawnPoints);
		arenaConfigurationReader.setSignMap(signManager.serialize());
	}

	public Location getLobbyLocation() {
		return gameLobby;
	}

	public void updateArenaSigns() {
		signManager.updateAllSigns();
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void addAndUpdateNewSign(Sign sign) {
		signManager.addSigns(sign);
	}

	public void addPlayer(Player player) {
		SCBRPlayer scbrPlayer = new SCBRPlayer(player, this);
		players.put(player.getName(), scbrPlayer);
		signManager.updateAllSigns();
	}

	public void onDeath(Player p, String deathMessage) {
		MessageManager.messageGame(this, deathMessage);
		getSCBRPlayerFor(p).reduceLives();
	}

	public void removePlayer(Player player) {
		if (players.isEmpty()) return;
		SCBRPlayer scbr = players.get(player.getName());
		scbr.restorePlayer();
		scbr.dispose();
		players.remove(player.getName());
		signManager.updateAllSigns();
	}

	public void setLobbyLocation(Location newLobby) {
		this.gameLobby = newLobby;
		arenaConfigurationReader.setGameLobby(newLobby);
	}

	public void addSpawnPoint(Location location) {
		spawnPoints.add(location);
	}

	public void setSpawnPoint(int pos, Location location) {
		if (pos >= spawnPoints.size()) addSpawnPoint(location);
		else spawnPoints.set(pos, location);
	}

	public int getSpawnPointCount() {
		return spawnPoints.size();
	}

	public boolean tryDisable() {
		disableArena();
		return true;
	}
}
