package ru.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

public class CustomItemHeavenMembrane extends RequesterCustomItem {

	public String getName() {
		return ChatColor.AQUA + "" + ChatColor.ITALIC + "Heaven Membrane";
	}

	public Material getMaterial() {
		return Material.PHANTOM_MEMBRANE;
	}

	@Override
	public void onUseRight(Player p, ItemStack item, PlayerInteractEvent e) {
		item.setAmount(item.getAmount() - 1);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 30, 0));
		ParticleUtils.createParticlesInsideSphere(p.getLocation(), 3, Particle.END_ROD, null, 35);
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 2F, 2F);
		p.teleport(p.getLocation().clone().add(0, p.getWorld().getHighestBlockYAt(p.getLocation()) + 100, 0));
		ParticleUtils.createParticlesInsideSphere(p.getLocation(), 3, Particle.END_ROD, null, 35);
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 2F, 2F);
	}

	@Override
	public String getDescription() {
		return "При использовании телепортирует высоко в воздух, выдавая эффект медленного падения. Может вытащить из шахты. Работает и на арене.";
	}

	@Override
	public int getRedstonePrice() {
		return 96;
	}

	@Override
	public int getLapisPrice() {
		return 16;
	}

}
