package com.gmail.Jacob6816.SCBReborn.classes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.Jacob6816.SCBReborn.arenas.Arena;
import com.gmail.Jacob6816.SCBReborn.arenas.ArenaManager;
import com.gmail.Jacob6816.SCBReborn.utilities.AbstractModularInventory;
import com.gmail.Jacob6816.SCBReborn.utilities.MessageManager;

public class ClassSelectionInventory extends AbstractModularInventory {

	public ClassSelectionInventory(int slots) {
		super(slots, "PlayerClassed", ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + " >> Select A Class");
	}

	@Override
	public void fillInventory() {
		int currentClassOrdinal = 0;
		inventory.clear();
		for (ConfigurablePlayerClass c : PlayerClassManager.getInstance().getPlayerClasses()) {
			ItemStack item = new ItemStack(Material.STONE_BUTTON);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + c.getClassName());
			item.setItemMeta(meta);
			inventory.setItem(currentClassOrdinal, item);
			currentClassOrdinal++;
		}
	}

	@Override
	public void onVerifiedInventoryClick(InventoryClickEvent event) {
		if (event.getSlot() >= PlayerClassManager.getInstance().getPlayerClasses().length) return;
		Player player = (Player) event.getWhoClicked();
		Arena arena = ArenaManager.getAM().getPlayerArena(player);
		ConfigurablePlayerClass playerClass = PlayerClassManager.getInstance().getPlayerClasses()[event.getSlot()];
		arena.getSCBRPlayerFor(player).setPlayerClass(playerClass);
		MessageManager.messageRecipient(player, "Class selected: " + playerClass.getClassName());
		super.onVerifiedInventoryClick(event);
	}

}
