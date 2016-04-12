package com.gmail.Jacob6816.SCBReborn.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;
import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaConfigurationReader;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public class CommandSetLobby extends SCBRCommand {

	public CommandSetLobby() {
		super("SetLobby", "/SCBReborn SetLobby (Game [blank = global])", "sl");
	}

	@Override
	public void onCommand(Player player, String... args) {
		Location newLobby = player.getLocation();
		if (args.length == 0) {
			setGlobalLobby(newLobby);
			MessageManager.messageRecipient(player, "Global lobby set to your location");
			return;
		}
		String name = args[0];
		Arena arena = ArenaManager.getAM().getArena(name);
		if (arena == null) {
			MessageManager.messageRecipient(player, ChatColor.RED + "That arena could not be found");
			return;
		}
		arena.setLobbyLocation(newLobby);
		MessageManager.messageRecipient(player, "Game lobby set to your location");
	}

	private void setGlobalLobby(Location newLobby) {
		SCBReborn.getSCBR().getConfig().set("GLOBAL.Lobby", ArenaConfigurationReader.loc2str(newLobby));
		SCBReborn.getSCBR().saveConfig();
	}

}
