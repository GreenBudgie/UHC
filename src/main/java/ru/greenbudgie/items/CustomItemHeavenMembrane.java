package ru.greenbudgie.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.ArenaManager;
import ru.greenbudgie.UHC.GameState;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.item.ItemInfo;

public class CustomItemHeavenMembrane extends RequesterCustomItem {

	public String getName() {
		return ChatColor.AQUA + "" + ChatColor.BOLD + "Heaven Membrane";
	}

	public Material getMaterial() {
		return Material.PHANTOM_MEMBRANE;
	}

	@Override
	public void onUseRight(Player p, ItemStack item, PlayerInteractEvent e) {
		if(UHC.state != GameState.DEATHMATCH || ArenaManager.getCurrentArena().isOpen()) {
			if(p.getWorld().getEnvironment() != World.Environment.NETHER) {
				item.setAmount(item.getAmount() - 1);
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 30, 0));
				ParticleUtils.createParticlesInsideSphere(p.getLocation(), 3, Particle.END_ROD, null, 35);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 2F, 2F);
				p.teleport(p.getLocation().clone().add(0, p.getWorld().getHighestBlockYAt(p.getLocation()) + 100, 0));
				ParticleUtils.createParticlesInsideSphere(p.getLocation(), 3, Particle.END_ROD, null, 35);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 2F, 2F);
			}
		}
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("При использовании телепортирует высоко в воздух, выдавая эффект медленного падения")
				.note("Не работает в аду и на закрытых аренах")
				.extra("Телепортирует на 100 блоков выше, чем самый верхний блок на твоих координатах")
				.example("Может мгновенно вытащить из пещеры или помочь избежать битвы с игроком или мобом");
	}

	@Override
	public int getRedstonePrice() {
		return 64;
	}

	@Override
	public int getLapisPrice() {
		return 8;
	}

}
