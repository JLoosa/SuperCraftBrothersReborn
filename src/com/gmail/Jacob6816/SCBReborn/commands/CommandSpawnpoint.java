package com.gmail.Jacob6816.SCBReborn.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public class CommandSpawnpoint extends SCBRCommand {

	public CommandSpawnpoint() {
		super("Spawnpoint", "/SCBReborn Spawnpoint (Add | Set [#]) (Name)", "sp");
	}

	@Override
	public void onCommand(Player player, String... args) {
		if (args.length < 2) {
			MessageManager.messageRecipient(player, getUsage());
			return;
		}
		String cmd = args[0];
		String arenaName = args.length > 2 ? args[2] : args[1];
		Arena arena = ArenaManager.getAM().getArena(arenaName);
		if (arena == null) {
			MessageManager.messageRecipient(player, ChatColor.RED + "That arena does not exist");
			return;
		}
		if (cmd.equalsIgnoreCase("Add")) {
			arena.addSpawnPoint(player.getLocation());
			MessageManager.messageRecipient(player, "Spawnpoint added [Total: " + arena.getSpawnPointCount() + "]");
			return;
		}
		if (cmd.equalsIgnoreCase("Set")) {
			String numStr = args[2];
			numStr = numStr.replaceAll("[^\\d]", "");
			if (numStr.length() <= 0) {
				MessageManager.messageRecipient(player, ChatColor.RED + "Please use proper int value");
				return;
			}
			int pos = Integer.parseInt(numStr);
			arena.setSpawnPoint(pos - 1, player.getLocation());
			MessageManager.messageRecipient(player, "Spawnpoint #" + pos + " set");
			return;
		}
		MessageManager.messageRecipient(player, getUsage());
	}
}
