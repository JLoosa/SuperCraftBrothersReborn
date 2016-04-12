package com.gmail.Jacob6816.SCBReborn.events;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;
import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaState;
import com.gmail.Jacob6816.SCBReborn.classes.PlayerClassManager;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public class SCBREventHandler implements Listener
{
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		Arena arena = ArenaManager.getAM().getPlayerArena(event.getPlayer());
		if (arena == null) return;
		if (arena.getGameState() == ArenaState.LOBBY)
			event.setCancelled(true);
	}

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
				MessageManager.messageRecipient(player, ChatColor.RED + "You must be in a agame to select a class");
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
						arena.addAndUpdateNewSign(sign);
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

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Arena arena = ArenaManager.getAM().getPlayerArena(event.getPlayer());
		if (arena == null) return;
		arena.removePlayer(event.getPlayer());
	}

	@EventHandler
	public void itemDrop(PlayerDropItemEvent event)
	{
		if (ArenaManager.getAM().getPlayerArena(event.getPlayer()) == null) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void itemDrop(InventoryClickEvent event)
	{
		if (ArenaManager.getAM().getPlayerArena((Player) event.getWhoClicked()) == null) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDamaged(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		Player p = (Player) e.getEntity();
		Arena arena = ArenaManager.getAM().getPlayerArena(p);
		if (arena == null) return;
		if (arena.getGameState() != ArenaState.INGAME) {
			e.setCancelled(true);
		}
		if (e.getCause() == DamageCause.FALL) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (ArenaManager.getAM().getPlayerArena(event.getPlayer()) == null) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (ArenaManager.getAM().getPlayerArena(event.getPlayer()) == null) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		final Player p = e.getEntity();
		Arena arena = ArenaManager.getAM().getPlayerArena(p);
		if (arena == null) return;
		e.getDrops().clear();
		arena.onDeath(p, e.getDeathMessage());
		e.setDeathMessage(null);
		new BukkitRunnable() {
			public void run() {
				try {
					Object nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
					Object packet = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".PacketPlayInClientCommand").newInstance();
					Class<?> enumClass = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EnumClientCommand");
					for (Object ob : enumClass.getEnumConstants()) {
						if (ob.toString().equals("PERFORM_RESPAWN")) {
							packet = packet.getClass()
									.getConstructor(enumClass)
									.newInstance(ob);
						}
					}
					Object con = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
					con.getClass().getMethod("sendPacket", packet.getClass()).invoke(con, packet);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}.runTaskLater(SCBReborn.getSCBR(), 3L);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Arena arena = ArenaManager.getAM().getPlayerArena(event.getPlayer());
		if (arena == null) return;
		arena.respawnPlayer(event);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Arena arena = ArenaManager.getAM().getPlayerArena(p);
		if (arena == null) return;
		if (arena.getGameState() == ArenaState.INGAME)
			if (p.getHealth() >= 1)
				if (!arena.getGameRegion().contains(e.getTo()))
					p.damage(p.getMaxHealth());

	}

	private void updateSignForArena(Sign sign, Arena arena) {
		sign.setLine(0, ChatColor.AQUA + "[SCBReborn]");
		sign.setLine(1, arena.getGameName());
		sign.setLine(2, arena.getGameState().displayText());
		sign.setLine(3, arena.getPlayers().size() + " / " + arena.getMaxPlayers());
		sign.update(true, false);
	}
}
