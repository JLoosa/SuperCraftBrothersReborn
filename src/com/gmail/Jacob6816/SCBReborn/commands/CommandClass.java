package com.gmail.Jacob6816.SCBReborn.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.Jacob6816.SCBReborn.classes.ConfigurablePlayerClass;
import com.gmail.Jacob6816.SCBReborn.classes.PlayerClassManager;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public class CommandClass extends SCBRCommand {

	public CommandClass() {
		super("Class", "/SCBReborn Class (Save[S] | Delete[D]) (ClassName)");
	}

	@Override
	public void onCommand(Player player, String... args) {
		if (args.length < 2) {
			MessageManager.messageRecipient(player, ChatColor.RED + "Not Enough Arguments");
			return;
		}
		String cmd = args[0];
		String className = args[1];

		if (cmd.startsWith("s")) {
			saveClass(player, className);
			return;
		}
		if (cmd.startsWith("d")) {
			deleteClass(player, className);
			return;
		}

		MessageManager.messageRecipient(player, getUsage());
	}

	private void deleteClass(Player player, String className) {
		PlayerClassManager pcm = PlayerClassManager.getInstance();
		if (pcm.getByName(className) == null) {
			MessageManager.messageRecipient(player, ChatColor.RED + "That class does not exist");
			return;
		}
		pcm.removeClass(className);
		MessageManager.messageRecipient(player, "Class successfully removed");
	}

	private void saveClass(Player player, String className) {
		ConfigurablePlayerClass pClass = PlayerClassManager.getInstance().getByName(className);
		if (pClass == null)
			pClass = PlayerClassManager.getInstance().createNewPlayerClass(className);
		pClass.setClassArmor(player.getInventory().getArmorContents());
		pClass.setClassInventory(player.getInventory().getContents());
		MessageManager.messageRecipient(player, "Successfully created/updated class '" + className + "'");
	}

}
