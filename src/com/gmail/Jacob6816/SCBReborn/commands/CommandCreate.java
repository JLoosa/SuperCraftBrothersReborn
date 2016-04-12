package com.gmail.Jacob6816.SCBReborn.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;
import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class CommandCreate extends SCBRCommand {

	public CommandCreate() {
		super("Create", "/SCBReborn Create (Name)", "c", "add");
	}

	@Override
	public void onCommand(Player player, String... args) {
		Selection selection = SCBReborn.getWorldEdit().getSelection(player);
		if (selection == null) {
			MessageManager.messageRecipient(player, ChatColor.RED + "You must have a WorldEdit selection to create an arena");
			return;
		}
		if (!(selection instanceof CuboidSelection)) {
			MessageManager.messageRecipient(player, ChatColor.RED + "Your WorldEdit selection must be a cuboid");
			return;
		}
		if (args.length == 0) {
			MessageManager.messageRecipient(player, ChatColor.RED + "You must provide a name");
			return;
		}
		String name = args[0];
		Arena arena = new Arena(name, (CuboidSelection) selection);
		ArenaManager.getAM().addArenaToList(arena);
		MessageManager.messageRecipient(player, ChatColor.GREEN + "Arena '" + arena.getGameName() + "' was sucessfully created using your WorldEdit selection", ChatColor.GREEN + "Please be sure to set the player spawns");
	}

}
