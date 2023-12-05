package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class MutatorHalfDamage extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.GLISTERING_MELON_SLICE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Крепкое Здоровье";
	}

	@Override
	public String getDescription() {
		return "По игрокам будет проходить лишь половина урона от любых источников";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.doubleDamage;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void damage(EntityDamageEvent event) {
		if(!event.isCancelled() && event.getEntity() instanceof Player) {
			event.setDamage(event.getDamage() / 2);
		}
	}

}
