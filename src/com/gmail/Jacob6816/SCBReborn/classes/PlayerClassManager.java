package com.gmail.Jacob6816.SCBReborn.classes;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.gmail.Jacob6816.SCBReborn.SCBReborn;

public class PlayerClassManager {

	private static PlayerClassManager					instance;
	private TreeMap<String, ConfigurablePlayerClass>	playerClasses;
	private ClassSelectionInventory						classInv;

	private PlayerClassManager() {
		playerClasses = new TreeMap<String, ConfigurablePlayerClass>();
		loadAllClassesFromFile();
	}

	public void setClassInv() {
		if (classInv == null)
			classInv = new ClassSelectionInventory(playerClasses.size() + 9);
	}

	public static PlayerClassManager getInstance() {
		if (instance == null) instance = new PlayerClassManager();
		return instance;
	}

	public ClassSelectionInventory getClassInventory() {
		return this.classInv;
	}

	public ConfigurablePlayerClass[] getPlayerClasses() {
		return playerClasses.values().toArray(new ConfigurablePlayerClass[playerClasses.size()]);
	}

	public void registerNewClass(String className, ConfigurablePlayerClass abstractPlayerClass) {
		playerClasses.put(className.toUpperCase(), abstractPlayerClass);
	}

	public ConfigurablePlayerClass getByName(String className) {
		return playerClasses.get(className.toUpperCase());
	}

	public void removeClass(String className) {
		playerClasses.remove(className);
	}

	public ConfigurablePlayerClass createNewPlayerClass(String className) {
		ConfigurablePlayerClass cpc = new ConfigurablePlayerClass(className, null, null);
		playerClasses.put(className.toUpperCase(), cpc);
		return cpc;
	}

	public void saveAllClassesToFile() {
		File saveFolder = new File(SCBReborn.getSCBR().getDataFolder(), "Classes");
		if (!saveFolder.exists()) saveFolder.mkdirs();
		for (File currentFile : saveFolder.listFiles())
			currentFile.delete();
		Iterator<String> classIterator = playerClasses.keySet().iterator();
		while (classIterator.hasNext()) {
			String name = classIterator.next();
			try {
				File file = new File(saveFolder, name.toUpperCase());
				file.createNewFile();
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				config.set("NAME", name);
				ItemStack[] currentArray = playerClasses.get(name).getClassArmor();
				for (int i = 0; i < currentArray.length; i++) {
					if (currentArray[i] == null) continue;
					config.set("Armor." + i, currentArray[i].serialize());
				}
				currentArray = playerClasses.get(name).getClassInventory();
				for (int i = 0; i < currentArray.length; i++) {
					if (currentArray[i] == null) continue;
					config.set("Inventory." + i, currentArray[i].serialize());
				}
				config.save(file);
			} catch (Exception exc) {
				exc.printStackTrace();
				SCBReborn.getSCBR().getLogger().severe("Failed to save class: " + name);
			}
		}
	}

	private void loadAllClassesFromFile() {
		File saveFolder = new File(SCBReborn.getSCBR().getDataFolder(), "Classes");
		if (!saveFolder.exists()) saveFolder.mkdirs();
		for (File currentFile : saveFolder.listFiles()) {
			try {
				YamlConfiguration config = YamlConfiguration.loadConfiguration(currentFile);
				ConfigurablePlayerClass pClass = new ConfigurablePlayerClass(config.getString("NAME"), null, null);
				ConfigurationSection section = config.getConfigurationSection("Armor");
				if (section != null) {
					ItemStack[] arr = new ItemStack[4];
					Map<String, Object> map1 = section.getValues(false);
					for (String key : map1.keySet()) {
						Map<String, Object> itemStackMap = section.getConfigurationSection(key).getValues(false);
						ItemStack item = ItemStack.deserialize(itemStackMap);
						arr[Integer.parseInt(key)] = item;
					}
					pClass.setClassArmor(arr);
				}
				section = config.getConfigurationSection("Inventory");
				if (section != null) {
					ItemStack[] arr = new ItemStack[36];
					Map<String, Object> map1 = section.getValues(false);
					for (String key : map1.keySet()) {
						Map<String, Object> itemStackMap = section.getConfigurationSection(key).getValues(false);
						ItemStack item = ItemStack.deserialize(itemStackMap);
						arr[Integer.parseInt(key)] = item;
					}
					pClass.setClassInventory(arr);
				}
				playerClasses.put(currentFile.getName(), pClass);
			} catch (Exception exc) {
				exc.printStackTrace();
				SCBReborn.getSCBR().getLogger().severe("Failed to load class: " + currentFile.getName());
			}
		}
	}
}
