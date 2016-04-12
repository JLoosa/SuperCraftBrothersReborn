package com.gmail.Jacob6816.SCBReborn.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public class CommandLeaveArena extends SCBRCommand {

	public CommandLeaveArena() {
		super("Leave", "/SCBReborn Leave", "l");
	}

	@Override
	public void onCommand(Player player, String... args) {
		Arena arena = ArenaManager.getAM().getPlayerArena(player);
		if (arena == null) {
			MessageManager.messageRecipient(player, ChatColor.RED + "You are not currently in a game");
			return;
		}
		arena.removePlayer(player);
		MessageManager.messageRecipient(player, "You have left the arena '" + arena.getGameName() + "'");
	}

}
