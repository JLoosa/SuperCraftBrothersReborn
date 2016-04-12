package com.gmail.Jacob6816.SCBReborn;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.Jacob6816.SCBReborn.commands.CommandClass;
import com.gmail.Jacob6816.SCBReborn.commands.CommandCreate;
import com.gmail.Jacob6816.SCBReborn.commands.CommandDisableArena;
import com.gmail.Jacob6816.SCBReborn.commands.CommandEnableArena;
import com.gmail.Jacob6816.SCBReborn.commands.CommandJoinArena;
import com.gmail.Jacob6816.SCBReborn.commands.CommandLeaveArena;
import com.gmail.Jacob6816.SCBReborn.commands.CommandListArenas;
import com.gmail.Jacob6816.SCBReborn.commands.CommandSetLobby;
import com.gmail.Jacob6816.SCBReborn.commands.CommandSpawnpoint;
import com.gmail.Jacob6816.SCBReborn.commands.SCBRCommand;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public class SCBRCommandExecutor implements CommandExecutor {

	private List<SCBRCommand>	commands;

	public SCBRCommandExecutor() {
		commands = Arrays.asList(new CommandCreate(), new CommandJoinArena(), new CommandListArenas(), new CommandSetLobby(), new CommandSpawnpoint(), new CommandLeaveArena(), new CommandEnableArena(), new CommandDisableArena(), new CommandClass());
		Collections.sort(commands);
	}

	public void sendCommandList(CommandSender target) {
		MessageManager.messageRecipient(target, "Usage: /SCBReborn <Command> (Arguments)");
		MessageManager.messageRecipient(target, "Current Commands are: ");
		for (SCBRCommand c : commands)
			MessageManager.messageRecipient(target, "- " + c.getUsage());
	}

	public String[] removeFirst(String... input) {
		if (input.length < 2) return new String[0];
		String[] output = new String[input.length - 1];
		for (int i = 0; i < output.length; i++)
			output[i] = input[i + 1];
		return output;
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String cmd, String[] arg3) {
		if (arg3.length == 0) {
			sendCommandList(arg0);
			return true;
		}
		cmd = arg3[0];
		arg3 = removeFirst(arg3);
		for (SCBRCommand command : commands) {
			if (command.isCommand(cmd)) {
				if (!command.canUseCommand(arg0)) {
					MessageManager.messageRecipient(arg0, ChatColor.RED + "You do not have permission to perform this command.");
					return true;
				}
				if (arg0 instanceof Player) command.onCommand((Player) arg0, arg3);
				else command.onConsoleCommand(arg0, arg3);
				return true;
			}
		}
		MessageManager.messageRecipient(arg0, ChatColor.RED + "Command not found.");
		return true;
	}
}
