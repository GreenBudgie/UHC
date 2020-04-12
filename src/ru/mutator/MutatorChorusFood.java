package ru.mutator;

import net.minecraft.server.v1_14_R1.ItemChorusFruit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
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
