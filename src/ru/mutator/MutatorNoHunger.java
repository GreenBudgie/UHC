package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import ru.util.WorldHelper;

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
