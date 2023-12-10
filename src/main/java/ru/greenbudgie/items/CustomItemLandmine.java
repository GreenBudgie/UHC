package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.greenbudgie.block.CustomBlockLandmine;
import ru.greenbudgie.util.ItemInfo;

public class CustomItemLandmine extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Landmine";
	}

	public Material getMaterial() {
		return Material.END_PORTAL_FRAME;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Мина, которая активируется при приближении врага к ней")
				.extra("Радиус активации: 5 блоков, задержка перед взрывом - 1.5 секунды. Сила взрыва примерно равна динамиту. Можно установить на арене! Взрывается сильнее, если окружена блоками со всех сторон, т.е. полностью зарыта.")
				.note("Нанесет тебе и тиммейту столько же урона, сколько и врагу, если будешь рядом! Можно зарыть под землю и поставить блок грязи сверху - он сразу зарастет травой.");
	}

	@Override
	public int getRedstonePrice() {
		return 80;
	}

	@Override
	public int getLapisPrice() {
		return 0;
	}

	@Override
	public boolean placeBlock(Location location, Player owner) {
		new CustomBlockLandmine(location, owner);
		return true;
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return true;
	}
}
