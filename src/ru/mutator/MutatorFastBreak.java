package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import ru.util.WorldHelper;

public class MutatorFastBreak extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.GOLD_INGOT;
	}

	@Override
	public String getName() {
		return "Непрочные Инструменты";
	}

	@Override
	public String getDescription() {
		return "Все инструменты, оружие и броня ломаются в три раза быстрее";
	}

	@EventHandler
	public void damage(PlayerItemDamageEvent e) {
		e.setDamage(e.getDamage() * 3);
	}

}
