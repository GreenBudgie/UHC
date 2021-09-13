package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import ru.UHC.GameState;
import ru.block.CustomBlockLandmine;
import ru.UHC.UHC;
import ru.util.ItemInfo;

public class CustomItemLandmine extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.DARK_RED + "Landmine";
	}

	public Material getMaterial() {
		return Material.END_PORTAL_FRAME;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Мина, которая активируется при приближении врага к ней")
				.extra("Радиус активации: 5 блоков, задержка перед взрывом - 1.5 секунды. Сила взрыва примерно равна динамиту.")
				.note("Можно зарыть под землю и поставить блок грязи сверху - он сразу зарастет травой");
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
		new CustomBlockLandmine(location, owner);
		return true;
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return false;
	}
}
