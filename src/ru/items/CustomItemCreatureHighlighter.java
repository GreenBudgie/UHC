package ru.items;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

public class CustomItemCreatureHighlighter extends RequesterCustomItem {

	public String getName() {
		return ChatColor.AQUA + "" + ChatColor.BOLD + "Creature Highlighter";
	}

	public Material getMaterial() {
		return Material.BONE_MEAL;
	}

	@Override
	public void onUseRight(Player p, ItemStack item, PlayerInteractEvent e) {
		item.setAmount(item.getAmount() - 1);
		boolean found = false;
		for(LivingEntity entity : p.getWorld().getEntitiesByClass(LivingEntity.class)) {
			if(!(entity instanceof Player) && p.getLocation().distance(entity.getLocation()) < 50) {
				entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 240, 0));
			}
		}
		ParticleUtils.createParticlesInsideSphere(p.getLocation(), 3, Particle.FIREWORKS_SPARK, null, 35);
		p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.7F);
	}

	@Override
	public String getDescription() {
		return "При использовании подсвечивает всех мобов в радиусе 50 блоков, но не игроков";
	}

	@Override
	public int getRedstonePrice() {
		return 48;
	}

	@Override
	public int getLapisPrice() {
		return 0;
	}

}
