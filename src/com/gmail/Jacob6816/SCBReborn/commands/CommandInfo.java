package com.gmail.Jacob6816.SCBReborn.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public class CommandInfo extends SCBRCommand {

	public CommandInfo() {
		super("Info","","i");
	}

	@Override
	public void onCommand(Player player, String... args) {
		PluginDescriptionFile pdfFile = SCBReborn.getSCBR().getDescription();
		MessageManager.messageRecipient(player, "Name: " + pdfFile.getFullName(), "Version: " + pdfFile.getVersion(), "Authors: " + pdfFile.getAuthors());
	}

}
