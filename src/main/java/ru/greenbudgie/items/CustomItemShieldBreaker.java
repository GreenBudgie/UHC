package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.item.ItemInfo;

public class CustomItemShieldBreaker extends RequesterCustomItem implements Listener {

	public String getName() {
		return ChatColor.BLUE + "Shield Breaker";
	}

	public Material getMaterial() {
		return Material.GOLDEN_AXE;
	}

	@EventHandler
	public void attack(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player victim && e.getDamager() instanceof Player attacker) {
			if(!PlayerManager.isTeammates(attacker, victim) && victim.isBlocking() && isEquals(attacker.getInventory().getItemInMainHand())) {
				ItemStack main = victim.getInventory().getItemInMainHand();
				ItemStack off = victim.getInventory().getItemInOffHand();
				if(off.getType() == Material.SHIELD) off.setAmount(0);
				else if(main.getType() == Material.SHIELD) main.setAmount(0);
				victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 0.5F);
				ParticleUtils.createParticlesAround(victim, Particle.SMOKE_NORMAL, null, 20);
			}
		}
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Ломает щит противника с одного удара, если он в этот момент им защищается");
	}

	@Override
	public int getRedstonePrice() {
		return 52;
	}

	@Override
	public int getLapisPrice() {
		return 8;
	}

}
