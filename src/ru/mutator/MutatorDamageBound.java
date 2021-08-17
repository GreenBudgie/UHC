package ru.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.util.MathUtils;

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

	@EventHandler(priority = EventPriority.HIGH)
	public void damage(EntityDamageEvent e) {
		if(!e.isCancelled() && e.getCause() != EntityDamageEvent.DamageCause.CUSTOM && e.getEntity() instanceof Player) {
			Player damager = (Player) e.getEntity();
			if(PlayerManager.isPlaying(damager)) {
				List<Player> playersCopy = Lists.newArrayList(PlayerManager.getAliveOnlinePlayers());
				double finalDamage = Math.min(e.getFinalDamage(), damager.getHealth());
				for(Player victim : playersCopy) {
					if(damager != victim) {
						if(victim.getHealth() > 1) {
							double damage = finalDamage / (MathUtils.clamp(PlayerManager.getAliveOnlinePlayers().size(), 8, 20) * 1.5);
							damage = Math.min(damage, maxDamage);
							if(damage > victim.getHealth() - 1) damage = victim.getHealth() - 1;
							victim.damage(damage);
						}
					}
				}
			}
		}
	}


}
