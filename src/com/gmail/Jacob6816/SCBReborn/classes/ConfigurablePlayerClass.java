package com.gmail.Jacob6816.SCBReborn.classes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfigurablePlayerClass implements Comparable<ConfigurablePlayerClass> {

	private String	name;
	private ItemStack[]	inventory, armor;
	private boolean		changed;

	public ConfigurablePlayerClass(String className, ItemStack[] armor, ItemStack[] inventory) {
		this.name = className;
		this.armor = armor;
		this.inventory = inventory;
		changed = false;
	}

	public ItemStack[] getClassArmor() {
		return this.armor;
	}

	public void setClassArmor(ItemStack[] newArmor) {
		this.armor = newArmor;
		changed = true;
	}

	public ItemStack[] getClassInventory() {
		return this.inventory;
	}

	public void setClassInventory(ItemStack[] newInventory) {
		this.inventory = newInventory;
		changed = true;
	}

	public void applyInventory(Player player) {
		player.getInventory().setArmorContents(getClassArmor());
		player.getInventory().setContents(getClassInventory());
	}

	public String getClassName() {
		return name;
	}

	@Override
	public int compareTo(ConfigurablePlayerClass apc) {
		return this.name.compareTo(apc.getClassName());
	}

	public ChatColor getClassColor() {
		return ChatColor.BOLD;
	}

	public boolean isChanged() {
		return this.changed;
	}
}
