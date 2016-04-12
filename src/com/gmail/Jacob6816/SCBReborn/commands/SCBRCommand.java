package com.gmail.Jacob6816.SCBReborn.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public abstract class SCBRCommand implements Comparable<SCBRCommand> {

	private String		commandName;
	private String		usage;
	private String[]	aliases;
	private String		permissionNode;

	public SCBRCommand(String name, String usage, String... aliases) {
		this.commandName = name;
		this.usage = usage;
		this.aliases = aliases;
		this.permissionNode = "scbr.command." + commandName;
	}

	public boolean canUseCommand(Permissible param1) {
		return param1.hasPermission(permissionNode);
	}

	public boolean isCommand(String param1) {
		if (param1 == null) return false;
		if (commandName.equalsIgnoreCase(param1)) return true;
		if (aliases.length > 0)
			for (String string : aliases) {
				if (string.equalsIgnoreCase(param1)) return true;
			}
		return false;
	}

	public String getUsage() {
		return this.usage;
	}

	public String getName() {
		return this.commandName;
	}

	public String[] getAliases() {
		return this.aliases;
	}

	public void onConsoleCommand(CommandSender sender, String... args) {
		MessageManager.messageRecipient(sender, ChatColor.RED + "This is a player-only command");
	}

	public abstract void onCommand(Player player, String... args);

	@Override
	public int compareTo(SCBRCommand o) {
		return this.getName().compareTo(o.getName());
	}
}
