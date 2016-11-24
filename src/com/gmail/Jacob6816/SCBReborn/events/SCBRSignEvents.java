package com.gmail.Jacob6816.SCBReborn.events;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;
import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaState;
import com.gmail.Jacob6816.SCBReborn.classes.PlayerClassManager;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public class SCBRSignEvents implements Listener
{

	@EventHandler
	public void onRightClickSign(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!(event.getClickedBlock().getState() instanceof Sign)) return;
		Sign sign = (Sign) event.getClickedBlock().getState();
		String[] lines = sign.getLines();
		String mainLine = ChatColor.stripColor(lines[0]);
		final Player player = event.getPlayer();
		if (mainLine.equalsIgnoreCase("[SCBReborn]")) {
			String arenaName = ChatColor.stripColor(lines[1]);
			player.performCommand("SCBReborn Join " + arenaName);
			return;
		}
		if (mainLine.equalsIgnoreCase("[Leave]")) {
			player.performCommand("SCBReborn Leave");
		}
		if (mainLine.equalsIgnoreCase("[Class]")) {
			Arena arena = ArenaManager.getAM().getPlayerArena(player);
			if (arena == null) {
				MessageManager.messageRecipient(player, ChatColor.RED + "You must be in a game to select a class");
				return;
			}
			if (arena.getGameState() != ArenaState.LOBBY) {
				MessageManager.messageRecipient(player, ChatColor.RED + "You must be in the lobby to choose a class");
				return;
			}
			player.openInventory(PlayerClassManager.getInstance().getClassInventory().getInventory());
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getLine(0).equalsIgnoreCase("[SCBR]") || e.getLine(0).equalsIgnoreCase("[SCBReborn]")) {
			if (!e.getPlayer().hasPermission("SCBR.Signs.Create")) {
				MessageManager.messageRecipient(e.getPlayer(), ChatColor.RED + "You do not have permission to create arena signs");
				e.getBlock().breakNaturally();
				return;
			}
			final Sign sign = (Sign) e.getBlock().getState();
			String name = e.getLine(1);
			final Arena arena = ArenaManager.getAM().getArena(name);
			if (arena != null) {
				new BukkitRunnable() {
					public void run() {
						arena.addNewSign(sign);
						updateSignForArena(sign, arena);
					}
				}.runTaskLater(SCBReborn.getSCBR(), 3);
			}
			else {
				e.getBlock().breakNaturally();
				MessageManager.messageRecipient(e.getPlayer(), ChatColor.RED + "That arena does not exist");
			}
		}
		if (e.getLine(0).equalsIgnoreCase("[class]")) {
			e.setLine(0, "§2[Class]");
			e.setLine(2, "§b> Select a class <");
		}
		if (e.getLine(0).equalsIgnoreCase("[leave]")) {
			e.setLine(0, "§2[Leave]");
			e.setLine(2, "§b> Leave Game <");
		}
	}

	private void updateSignForArena(Sign sign, Arena arena) {
		sign.setLine(0, ChatColor.AQUA + "[SCBReborn]");
		sign.setLine(1, arena.getGameName());
		sign.setLine(2, arena.getGameState().displayText());
		sign.setLine(3, arena.getPlayers().size() + " / " + arena.getMaxPlayers());
		sign.update(true, false);
	}
}
