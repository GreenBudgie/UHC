package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.util.ParticleUtils;

public class CustomItemAncientShard extends ClassCustomItem {

	public String getName() {
		return ChatColor.AQUA + "" + ChatColor.BOLD + "Ancient Shard";
	}

	public Material getMaterial() {
		return Material.QUARTZ;
	}

	@Override
	public void onUseRight(Player p, ItemStack item, PlayerInteractEvent e) {
		item.setAmount(item.getAmount() - 1);
		p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 10, 4));
		p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 0, false, false, false));
		ParticleUtils.createParticlesOutlineSphere(p.getLocation(), 3, Particle.VILLAGER_ANGRY, null, 35);
		p.playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_AMBIENT, 1F, 0.5F);
		p.playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 0.6F, 1.2F);
	}

	@Override
	public String getDescription() {
		return "Выдает спешку V на 10 секунд при использовании";
	}

}
