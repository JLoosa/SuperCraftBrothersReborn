package com.gmail.Jacob6816.SCBReborn.utilities;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.Jacob6816.SCBReborn.arenas.Arena;

/**
 * 
 * @author Jacob THIS CLASS IS ONLY INTENDED FOR STATIC ACCESS!
 *
 */
public class MessageManager {

	private static final String	messagePrefix	= ChatColor.GOLD + "[SCBReborn] " + ChatColor.GREEN;

	public static void messageGame(Arena arenaObject, String... strings) {
		for (SCBRPlayer player : arenaObject.getPlayers()) {
			for (String string : strings)
				player.sendMessage(messagePrefix + string);
		}
	}

	public static void messageRecipient(CommandSender arg0, String... strings) {
		for (String string : strings)
			arg0.sendMessage(messagePrefix + string);
	}

}
