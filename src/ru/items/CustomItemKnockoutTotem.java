package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.block.CustomBlockKnockoutTotem;
import ru.block.CustomBlockLandmine;
import ru.util.ItemInfo;

public class CustomItemKnockoutTotem extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.GRAY + "" + ChatColor.BOLD +"Knockout Totem";
	}

	public Material getMaterial() {
		return Material.DEEPSLATE_TILE_WALL;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Тотем отбрасывания. При установке разбрасывает в стороны всех живых существ и игроков, кроме тебя и тиммейта.")
				.extra("Действует 30 секунд в радиусе 6 блоков, после этого исчезает")
				.note("До ПВП не действует на игроков");
	}

	@Override
	public int getRedstonePrice() {
		return 64;
	}

	@Override
	public int getLapisPrice() {
		return 0;
	}

	@Override
	public boolean placeBlock(Location location, Player owner) {
		new CustomBlockKnockoutTotem(location, owner);
		return true;
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return true;
	}
}
