package ru.items;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.util.MathUtils;
import ru.util.ParticleUtils;
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void attack(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player victim && e.getDamager() instanceof Player attacker &&
				!e.isCancelled() && e.getFinalDamage() > 0 && isEquals(attacker.getInventory().getItemInMainHand())) {
			double regenHp = e.getFinalDamage() * 0.2;
			double maxHp = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			if(!PlayerManager.isTeammates(victim, attacker)) {
				attacker.setHealth(MathUtils.clamp(attacker.getHealth() + regenHp, 0, maxHp));
				victim.getWorld().playSound(victim.getLocation(), Sound.ITEM_HOE_TILL, 1, 0.5F);
				ParticleUtils.createParticlesAround(victim, Particle.REDSTONE, Color.fromRGB(80, 0, 0), 15);
			}
		}
	}

	@Override
	public String getDescription() {
		return "При атаке противника регенерирует тебе 20% здоровья от нанесенного урона";
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
