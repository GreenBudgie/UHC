package ru.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;
import ru.util.WorldHelper;

public class CustomItemPlayerTracker extends RequesterCustomItem {

	public String getName() {
		return ChatColor.GREEN + "Player Tracker";
	}

	public Material getMaterial() {
		return Material.COMPASS;
	}

	@Override
	public String getDescription() {
		return "Указывает на ближайшего врага, когда держишь его в руке";
	}

	@Override
	public int getRedstonePrice() {
		return 128;
	}

	@Override
	public int getLapisPrice() {
		return 20;
	}

}
