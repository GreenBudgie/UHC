package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.block.CustomBlockInfernalLead;
import ru.block.CustomBlockTerraTracer;

public class CustomItemTerraTracer extends ClassCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Terra Tracer";
	}

	public Material getMaterial() {
		return Material.BEACON;
	}

	@Override
	public String getDescription() {
		return "При установке указывает на ближайшее расположение алмазов или древних осколков";
	}

	@Override
	public boolean placeBlock(Location location, Player owner) {
		new CustomBlockTerraTracer(location);
		return true;
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return false;
	}

}
