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
import ru.util.ItemInfo;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

public class CustomItemBooster extends RequesterCustomItem {

	public String getName() {
		return ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Booster";
	}

	public Material getMaterial() {
		return Material.SUGAR;
	}

	@Override
	public void onUseRight(Player p, ItemStack item, PlayerInteractEvent e) {
		item.setAmount(item.getAmount() - 1);
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 4));
		ParticleUtils.createParticlesInsideSphere(p.getLocation(), 3, Particle.CLOUD, null, 35);
		p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1F, 1.5F);
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("При использовании выдает скорость V на 8 секунд").extra("Ты становишься ровно в 2 раза быстрее");
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
