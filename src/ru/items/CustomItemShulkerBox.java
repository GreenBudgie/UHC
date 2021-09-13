package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.util.ItemInfo;
import ru.util.WorldHelper;

public class CustomItemShulkerBox extends RequesterCustomItem {

	public String getName() {
		return ChatColor.GREEN + "" + ChatColor.ITALIC + "Shulker Box";
	}

	public Material getMaterial() {
		return Material.LIME_SHULKER_BOX;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Обычный шалкер бокс");
	}

	@Override
	public int getRedstonePrice() {
		return 20;
	}

	@Override
	public int getLapisPrice() {
		return 0;
	}

	@Override
	public boolean isGlowing() {
		return false;
	}
}
