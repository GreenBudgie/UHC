package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.block.CustomBlockInfernalTotem;

public class CustomItemInfernalTotem extends ClassCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Infernal Totem";
	}

	public Material getMaterial() {
		return Material.NETHER_BRICK_FENCE;
	}

	@Override
	public String getDescription() {
		return "Тотем ада. Поджигает всех существ и игроков вокруг, кроме тебя и тиммейта. Действует 30 секунд в радиусе 4-х блоков. Одноразовый. До ПВП не поджигает игроков.";
	}

	@Override
	public boolean placeBlock(Location location, Player owner) {
		new CustomBlockInfernalTotem(location, owner);
		return true;
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return true;
	}

}
