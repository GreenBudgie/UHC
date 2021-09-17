package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.block.CustomBlockAllurementStone;
import ru.block.CustomBlockKnockoutTotem;
import ru.util.ItemInfo;

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
		return 72;
	}

	@Override
	public int getLapisPrice() {
		return 8;
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
