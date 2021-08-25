package ru.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.block.CustomBlockInfernalLead;
import ru.block.CustomBlockItem;
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
	public String getDescription() {
		return "При установке показывает расположение ближайшей крепости в аду. Можно использовать неограниченное количество раз, главное суметь его сломать.";
	}

	@Override
	public int getRedstonePrice() {
		return 48;
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
