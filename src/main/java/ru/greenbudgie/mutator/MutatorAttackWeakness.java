package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;

public class MutatorAttackWeakness extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.FERMENTED_SPIDER_EYE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Усталость от Борьбы";
	}

	@Override
	public String getDescription() {
		return "При атаке любого игрока или моба ты получаешь эффект слабости III на 5 секунд, что делает любую твою ближнюю атаку бесполезной на это время";
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void damage(EntityDamageByEntityEvent event) {
		if(!event.isCancelled() && event.getFinalDamage() > 0 && event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player attacker) {
			if(PlayerManager.isPlaying(attacker)) {
				attacker.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 7 * 20, 2));
			}
		}
	}


}
