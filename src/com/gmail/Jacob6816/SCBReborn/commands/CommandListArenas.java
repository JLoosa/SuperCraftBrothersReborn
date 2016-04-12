package com.gmail.Jacob6816.SCBReborn.commands;

import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public class CommandListArenas extends SCBRCommand {

	public CommandListArenas() {
		super("List", "/SCBReborn List", "al");
	}

	@Override
	public void onCommand(Player player, String... args) {
		List<Arena> arenas = ArenaManager.getAM().getAllArenas();
		if (arenas.isEmpty()) {
			MessageManager.messageRecipient(player, "All arenas: []");
			return;
		}
		StringBuilder sb = new StringBuilder("All arenas: [ " + arenas.get(0).getGameName());
		for (int i = 1; i < arenas.size(); i++)
			sb.append(", " + arenas.get(i).getGameName());
		sb.append(" ]");
		String listOfArenas = sb.toString().trim();
		MessageManager.messageRecipient(player, listOfArenas);
	}

}
