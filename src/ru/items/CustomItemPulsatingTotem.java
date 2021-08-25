package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.block.CustomBlockPulsatingTotem;

public class CustomItemPulsatingTotem extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.WHITE + "" + ChatColor.BOLD +"Pulsating Aura Totem";
	}

	public Material getMaterial() {
		return Material.DIORITE_WALL;
	}

	@Override
	public String getDescription() {
		return "Тотем пульсирующей ауры. Отталкивает любые чужие снаряды (стрелы, фаерболы, брошенные зелья, трезубцы...) в радиусе 6 блоков. Действует 30 секунд. Одноразовый.";
	}

	@Override
	public int getRedstonePrice() {
		return 80;
	}

	@Override
	public int getLapisPrice() {
		return 20;
	}

	@Override
	public boolean placeBlock(Location location, Player owner) {
		new CustomBlockPulsatingTotem(location, owner);
		return true;
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return true;
	}
}
