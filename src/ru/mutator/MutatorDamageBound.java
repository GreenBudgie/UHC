package ru.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.UHC.UHC;
import ru.util.MathUtils;

public class MutatorDamageBound extends Mutator implements Listener {

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
			for(Player player : UHC.players) {
				if(p != player) {
					player.damage(e.getFinalDamage() / (MathUtils.clamp(UHC.players.size(), 8, 30) * 1.5));
				}
			}
		}
	}


}
