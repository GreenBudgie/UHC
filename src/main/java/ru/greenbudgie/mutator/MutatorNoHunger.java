package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.mutator.manager.MutatorManager;

public class MutatorNoHunger extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.COOKED_CHICKEN;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Вечная Сытость";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.vegetarian;
	}

	@Override
	public String getDescription() {
		return "Голод не тратится";
	}

	@EventHandler
	public void food(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		if(p.getFoodLevel() < 20) {
			p.setFoodLevel(20);
		}
		e.setCancelled(true);
	}


}
