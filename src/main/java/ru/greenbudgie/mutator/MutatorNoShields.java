package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class MutatorNoShields extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.SHIELD;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Беззащитный";
	}

	@Override
	public String getDescription() {
		return "Щиты больше не скрафтить";
	}

	@EventHandler
	public void craft(CraftItemEvent e) {
		if(e.getRecipe().getResult().getType() == Material.SHIELD) {
			Player p = (Player) e.getWhoClicked();
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 0.5F, 1F);
			e.setCancelled(true);
		}
	}


}
