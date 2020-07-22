package ru.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.UHC.UHC;
import ru.util.MathUtils;

import java.util.Iterator;
import java.util.List;

public class MutatorDamageBound extends Mutator implements Listener {

	public int maxDamage = 4;

	@Override
	public Material getItemToShow() {
		return Material.ENDER_EYE;
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
			Player p = (Player) e.getEntity();
			List<Player> playersCopy = Lists.newArrayList(UHC.players);
			for(Player player : playersCopy) {
				if(p != player) {
					double health = player.getHealth();
					if(health > 1) {
						double finalDamage = Math.min(e.getFinalDamage(), health);
						double damage = finalDamage / (MathUtils.clamp(UHC.players.size(), 8, 20) * 1.5);
						damage = Math.min(damage, maxDamage);
						if(damage > health - 1) damage = health - 1;
						player.damage(damage);
					}
				}
			}
		}
	}


}
