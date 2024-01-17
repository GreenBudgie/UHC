package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.mutator.manager.MutatorManager;

public class MutatorBabyZombies extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.ZOMBIE_HEAD;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Опасные Зомби";
	}

	@Override
	public String getDescription() {
		return "Все зомби спавнятся мелкими";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.overpoweredMobs;
	}

	@EventHandler
	public void spawn(CreatureSpawnEvent e) {
		if(e.getEntity() instanceof Zombie zombie) {
			zombie.setBaby();
		}
	}


}
