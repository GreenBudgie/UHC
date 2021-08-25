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

public class CustomItemLandmine extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.DARK_RED + "Landmine";
	}

	public Material getMaterial() {
		return Material.END_PORTAL_FRAME;
	}

	@Override
	public String getDescription() {
		return "Мина. Активируется при приближении врага к ней на расстоянии 5 блоков с задержкой в 1,5 секунды. Можно зарыть под землю.";
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
