package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import ru.util.InventoryHelper;

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
