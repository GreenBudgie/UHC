package ru.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.util.MathUtils;

public class MutatorSkeletons extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.SKELETON_SKULL;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Обстрел";
	}

	@Override
	public String getDescription() {
		return "Скелеты спавнятся чаще других мобов";
	}

	@EventHandler
	public void spawn(CreatureSpawnEvent e) {
		if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && MathUtils.chance(60) && (e.getEntity() instanceof Zombie || e
				.getEntity() instanceof Creeper || e.getEntity() instanceof Spider)) {
			e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.SKELETON);
			e.setCancelled(true);
		}
	}

}
