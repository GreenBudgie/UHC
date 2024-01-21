package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.greenbudgie.block.CustomBlockInfernalLead;
import ru.greenbudgie.util.item.ItemInfo;

public class CustomItemInfernalLead extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.GOLD + "" + ChatColor.BOLD + "Infernal Lead";
	}

	public Material getMaterial() {
		return Material.RESPAWN_ANCHOR;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("При установке показывает расположение ближайшей крепости в аду")
				.extra("Также показывает дистанцию до крепости")
				.note("Можно использовать неограниченное количество раз. Однако, для этого нужно суметь его сломать, ведь блок имеет прочность обсидиана.");
	}

	@Override
	public int getRedstonePrice() {
		return 32;
	}

	@Override
	public int getLapisPrice() {
		return 0;
	}

	@Override
	public boolean placeBlock(Location location, Player owner) {
		new CustomBlockInfernalLead(location);
		return true;
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return false;
	}

}
