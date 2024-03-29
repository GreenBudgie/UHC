package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.greenbudgie.block.CustomBlockAllurementStone;
import ru.greenbudgie.util.item.ItemInfo;

public class CustomItemAllurementStone extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Stone of Allurement";
	}

	public Material getMaterial() {
		return Material.LODESTONE;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Притягательный Камень. Притягивает всех живых существ к себе.")
				.extra("Действует 30 секунд в радиусе 7 блоков, после этого исчезает.")
				.note("Можно установить на арене. Избежать его действия после притяжения возможно, но достаточно сложно. Легче использовать перл.");
	}

	@Override
	public int getRedstonePrice() {
		return 64;
	}

	@Override
	public int getLapisPrice() {
		return 16;
	}

	@Override
	public boolean placeBlock(Location location, Player owner) {
		new CustomBlockAllurementStone(location, owner);
		return true;
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return true;
	}
}
