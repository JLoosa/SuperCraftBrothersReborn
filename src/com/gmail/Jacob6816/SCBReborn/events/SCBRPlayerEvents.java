package com.gmail.Jacob6816.SCBReborn.events;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;
import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaState;

public class SCBRPlayerEvents implements Listener {

	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Arena arena = ArenaManager.getAM().getPlayerArena(event.getPlayer());
		if (arena == null)
			return;
		if (arena.getGameState() == ArenaState.LOBBY)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Arena arena = ArenaManager.getAM().getPlayerArena(event.getPlayer());
		if (arena == null)
			return;
		arena.removePlayer(event.getPlayer());
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (ArenaManager.getAM().getPlayerArena(event.getPlayer()) == null)
			return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDamaged(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		Player p = (Player) e.getEntity();
		Arena arena = ArenaManager.getAM().getPlayerArena(p);
		if (arena == null)
			return;
		if (arena.getGameState() != ArenaState.INGAME) {
			e.setCancelled(true);
		}
		if (e.getCause() == DamageCause.FALL) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		final Player p = e.getEntity();
		Arena arena = ArenaManager.getAM().getPlayerArena(p);
		if (arena == null)
			return;
		e.getDrops().clear();
		arena.onDeath(p, e.getDeathMessage());
		e.setDeathMessage(null);
		new BukkitRunnable() {
			public void run() {
				try {
					Object nmsPlayer = p.getClass().getMethod("getHandle").invoke(p);
					Object packet = Class
							.forName(nmsPlayer.getClass().getPackage().getName() + ".PacketPlayInClientCommand")
							.newInstance();
					Class<?> enumClass = Class.forName(packet.getClass().getName() + "$EnumClientCommand");
					for (Object ob : enumClass.getEnumConstants()) {
						if (ob.toString().equals("PERFORM_RESPAWN")) {
							packet = packet.getClass().getConstructor(enumClass).newInstance(ob);
						}
					}
					Object con = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
					con.getClass().getMethod("sendPacket", packet.getClass()).invoke(con, packet);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}.runTaskLater(SCBReborn.getSCBR(), 3);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Arena arena = ArenaManager.getAM().getPlayerArena(event.getPlayer());
		if (arena == null)
			return;
		arena.respawnPlayer(event);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Arena arena = ArenaManager.getAM().getPlayerArena(p);
		if (arena == null)
			return;
		if (arena.getGameState() == ArenaState.INGAME)
			if (p.getHealth() >= 1)
				if (!arena.getGameRegion().contains(e.getTo()))
					p.damage(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

	}

}
