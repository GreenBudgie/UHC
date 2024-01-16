package ru.greenbudgie.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.MathUtils;

import java.util.List;

public class MutatorDamageBound extends Mutator implements Listener {

	public int maxDamage = 4;

	@Override
	public Material getItemToShow() {
		return Material.LEAD;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Связанные Души";
	}

	@Override
	public String getDescription() {
		return "Когда кто-то из игроков получает урон, все остальные игроки получают часть от этого урона";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.healthUnion;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void damage(EntityDamageEvent e) {
		if(!e.isCancelled() && e.getCause() != EntityDamageEvent.DamageCause.CUSTOM && e.getEntity() instanceof Player damager) {
			if(PlayerManager.isPlaying(damager)) {
				List<UHCPlayer> playersCopy = Lists.newArrayList(PlayerManager.getAlivePlayers());
				double finalDamage = Math.min(e.getFinalDamage(), damager.getHealth());
				double damage = finalDamage / (MathUtils.clamp(PlayerManager.getAlivePlayers().size(), 8, 20) * 1.5);
				damage = Math.min(damage, maxDamage);
				for(UHCPlayer uhcVictim : playersCopy) {
					if(uhcVictim.isOnline()) {
						Player victim = uhcVictim.getPlayer();
						if(damager != victim) {
							if(victim.getHealth() > 1) {
								if(damage > victim.getHealth() - 1) damage = victim.getHealth() - 1;
								victim.damage(damage);
							}
						}
					} else {
						if(uhcVictim.getOfflineHealth() > 1) {
							if(damage > uhcVictim.getOfflineHealth() - 1) damage = uhcVictim.getOfflineHealth() - 1;
							uhcVictim.addOfflineHealth(-damage);
						}
					}
				}
			}
		}
	}


}
