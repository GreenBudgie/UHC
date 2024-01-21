package ru.greenbudgie.items;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.item.ItemInfo;
import ru.greenbudgie.util.item.ItemUtils;

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
			double regenHp = e.getFinalDamage() * 0.25;
			double maxHp = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			if(!PlayerManager.isTeammates(victim, attacker)) {
				attacker.setHealth(MathUtils.clamp(attacker.getHealth() + regenHp, 0, maxHp));
				victim.getWorld().playSound(victim.getLocation(), Sound.ITEM_HOE_TILL, 1, 0.5F);
				ParticleUtils.createParticlesAround(victim, Particle.REDSTONE, Color.fromRGB(80, 0, 0), 15);
			}
		}
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("При атаке противника регенерирует тебе 25% здоровья от нанесенного урона")
				.example("Ты нанес 4хп урона противнику - твое здоровье пополнилось на 1хп")
				.note("Действует только на игроков. Он не тратит прочность и его можно зачарить!");
	}

	@Override
	public ItemStack getItemStack() {
		return ItemUtils.builder(super.getItemStack()).unbreakable().build();
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
