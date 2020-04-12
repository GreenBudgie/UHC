package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import ru.util.InventoryHelper;
import ru.util.WorldHelper;

public class MutatorStrongStone extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.OBSIDIAN;
	}

	@Override
	public String getName() {
		return "Прочный Камень";
	}

	@Override
	public String getDescription() {
		return "Камень нельзя ломать киркой. На булыжник, андезит, гранит и т.д. не действует";
	}

	@EventHandler
	public void dig(BlockBreakEvent e) {
		Block b = e.getBlock();
		ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
		if(b.getType() == Material.STONE && InventoryHelper.isPickaxe(tool.getType())) {
			WorldHelper.spawnParticlesOutline(b, Particle.SMOKE_NORMAL, null, 10);
			b.getWorld().playSound(b.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3F, 1F);
			e.setCancelled(true);
		}
	}

}
