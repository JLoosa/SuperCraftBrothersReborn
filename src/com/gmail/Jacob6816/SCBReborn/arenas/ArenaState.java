package com.gmail.Jacob6816.SCBReborn.arenas;

import org.bukkit.ChatColor;

public enum ArenaState {
	LOBBY(ChatColor.AQUA + "Lobby"), INGAME(ChatColor.GREEN + "In-Game"), DISABLED(ChatColor.RED + "Disabled");

	final String	displayText;

	ArenaState(String displayText) {
		this.displayText = displayText;
	}

	public String displayText() {
		return displayText;
	}
}
