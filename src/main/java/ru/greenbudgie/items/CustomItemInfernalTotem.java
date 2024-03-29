package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.greenbudgie.block.CustomBlockInfernalTotem;
import ru.greenbudgie.util.item.ItemInfo;

public class CustomItemInfernalTotem extends ClassCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Infernal Totem";
	}

	public Material getMaterial() {
		return Material.NETHER_BRICK_FENCE;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Тотем ада. Поджигает всех существ и игроков вокруг, кроме тебя и тиммейта.")
				.extra("Действует 30 секунд в радиусе 5 блоков, после этого исчезает. Противники горят 5 секунд.")
				.note("До ПВП поджигает только мобов, игроки в безопасности. Можно установить на арене!");
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
