package ru.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.block.CustomBlockInfernalLead;
import ru.block.CustomBlockItem;
import ru.util.ItemInfo;
import ru.util.ParticleUtils;

public class CustomItemInfernalLead extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Infernal" +
				ChatColor.GOLD + ChatColor.BOLD + " Lead";
	}

	public Material getMaterial() {
		return Material.RESPAWN_ANCHOR;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("При установке показывает расположение ближайшей крепости в аду")
				.note("Можно использовать неограниченное количество раз. Однако, для этого нужно суметь его сломать, ведь блок имеет прочность обсидиана.");
	}

	@Override
	public int getRedstonePrice() {
		return 40;
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
