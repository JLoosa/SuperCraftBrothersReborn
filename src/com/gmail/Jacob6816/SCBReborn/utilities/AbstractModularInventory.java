package com.gmail.Jacob6816.SCBReborn.utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;

public abstract class AbstractModularInventory implements Listener {

	public final Inventory				inventory;
	private String						name;
	private AbstractModularInventory	parentInventory;

	public AbstractModularInventory(int slots, String key) {
		this(slots, key, ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + key);
	}

	public AbstractModularInventory(int slots, String key, String invName) {
		this.name = key;
		while (slots % 9 != 0)
			slots++;
		invName = invName.substring(0, Math.min(32, invName.length()));
		Bukkit.getPluginManager().registerEvents(this, SCBReborn.getSCBR());
		inventory = Bukkit.getServer().createInventory(null, slots, invName);
		parentInventory = null;
	}

	public abstract void fillInventory();

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (inventory == null) return;
		if (event.getInventory().hashCode() == inventory.hashCode()) {
			event.setCancelled(true);
			onVerifiedInventoryClick(event);
		}
	}

	public void setParentInventory(AbstractModularInventory newParent) {
		parentInventory = newParent;
	}

	public void onVerifiedInventoryClick(InventoryClickEvent event) {

	}

	@EventHandler
	public void onOpen(InventoryOpenEvent event) {
		if (inventory == null) return;
		if (event.getInventory().hashCode() == inventory.hashCode()) {
			onVerifiedInventoryOpen(event);
		}
	}

	public void onVerifiedInventoryOpen(InventoryOpenEvent event) {
		fillInventory();
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (inventory == null) return;
		if (event.getInventory().hashCode() == inventory.hashCode()) {
			onVerifiedInventoryClose(event);
		}
	}

	public void onVerifiedInventoryClose(InventoryCloseEvent event) {
		if (parentInventory != null) {
			final Player player = (Player) event.getPlayer();
			new BukkitRunnable() {
				public void run() {
					player.openInventory(parentInventory.getInventory());
				}
			}.runTaskLater(SCBReborn.getSCBR(), 1);
		}
	}

	public Inventory getInventory() {
		return inventory;
	}

	public boolean isInventory(String string) {
		return string.equalsIgnoreCase(name) || string.equalsIgnoreCase("Inventory" + name);
	}

}
