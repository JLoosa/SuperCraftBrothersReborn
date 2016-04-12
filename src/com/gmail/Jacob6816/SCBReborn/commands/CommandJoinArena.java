package com.gmail.Jacob6816.SCBReborn.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public class CommandJoinArena extends SCBRCommand {

	public CommandJoinArena() {
		super("Join", "/SCBReborn Join (Name)", "j");
	}

	@Override
	public void onCommand(Player player, String... args) {

		if (ArenaManager.getAM().getPlayerArena(player) != null) {
			MessageManager.messageRecipient(player, ChatColor.RED + "You are already in a game");
			return;
		}

		if (args.length == 0) {
			MessageManager.messageRecipient(player, ChatColor.RED + "You must specify and arena name");
			return;
		}
		String arenaName = args[0];
		Arena arena = ArenaManager.getAM().getArena(arenaName);
		if (arena == null) {
			MessageManager.messageRecipient(player, ChatColor.RED + "That arena does not exist");
			return;
		}
		if (!arena.isJoinable()) {
			MessageManager.messageRecipient(player, ChatColor.RED + "That arena is not joinable");
			return;
		}
		
		arena.addPlayer(player);
	}

}
