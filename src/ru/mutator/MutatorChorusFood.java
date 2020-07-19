package ru.mutator;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import ru.util.WorldHelper;

public class MutatorChorusFood extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.CHORUS_FRUIT;
	}

	@Override
	public String getName() {
		return "≈да из Ёнда";
	}

	@Override
	public String getDescription() {
		return "Ћюба€ еда теперь будет работать как хорус";
	}

	@EventHandler
	public void eat(PlayerItemConsumeEvent e) {
		if(e.getItem().getType().isEdible()) {
			WorldHelper.chorusTeleport(e.getPlayer(), 16);
		}
	}


}
