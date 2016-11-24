package com.gmail.Jacob6816.SCBReborn.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;

public class SCBRBlockEvents implements Listener {

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

}
