package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.mutator.manager.MutatorManager;

public class MutatorDoubleDamage extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.LEATHER_CHESTPLATE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Двойной Урон";
	}

	@Override
	public String getDescription() {
		return "По игрокам будет проходить двойной урон от любых источников";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.halfDamage;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void damage(EntityDamageEvent event) {
		if(!event.isCancelled() && event.getEntity() instanceof Player) {
			event.setDamage(event.getDamage() * 2);
		}
	}

}
