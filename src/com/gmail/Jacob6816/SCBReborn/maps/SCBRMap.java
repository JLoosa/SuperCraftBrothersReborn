package com.gmail.Jacob6816.SCBReborn.maps;

import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;

public class SCBRMap {

	private String			name;
	private World			mapWorld;
	private CuboidSelection	mapRegion;

	public SCBRMap(String name, CuboidSelection selection) {
		this.name = name;
		setMapRegion(selection);
	}

	public String getName() {
		return this.name;
	}

	public boolean isLocationInRegion(Location location) {
		return this.mapRegion.contains(location);
	}

	public World getWorld() {
		return this.mapWorld;
	}

	public CuboidSelection getMapRegion() {
		return this.mapRegion;
	}

	public void setMapRegion(CuboidSelection selection) {
		this.mapWorld = selection.getWorld();
		this.mapRegion = selection;
	}

}
