package ru.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ru.UHC.UHC;
import ru.util.MathUtils;
import ru.util.WorldHelper;

public class CustomItemSoulscriber extends RequesterCustomItem implements Listener {

	public String getName() {
		return ChatColor.GOLD + "Soulscriber";
	}

	public Material getMaterial() {
		return Material.GOLDEN_SWORD;
	}

	public boolean isGlowing() {
		return false;
	}

	@EventHandler
	public void attack(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player && !e.isCancelled() && e.getFinalDamage() > 0) {
			Player attacker = (Player) e.getDamager();
			int hp = MathUtils.randomRange(0, 3);
			if(hp > 0 && attacker.getHealth() < 20 && isEquals(attacker.getInventory().getItemInMainHand())) {
				Player victim = (Player) e.getEntity();
				if(!UHC.isTeammates(victim, attacker)) {
					attacker.setHealth(MathUtils.clamp(attacker.getHealth() + hp, 0, 20));
					victim.getWorld().playSound(victim.getLocation(), Sound.ITEM_HOE_TILL, 1, 0.5F);
					WorldHelper.spawnParticlesAround(victim, Particle.REDSTONE, Color.fromRGB(80, 0, 0), 15);
				}
			}
		}
	}

	@Override
	public String getDescription() {
		return "При атаке противника пополняет тебе 0-3 хп";
	}

	@Override
	public int getRedstonePrice() {
		return 64;
	}

	@Override
	public int getLapisPrice() {
		return 0;
	}

}
