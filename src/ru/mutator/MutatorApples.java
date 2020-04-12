package ru.mutator;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import ru.util.MathUtils;
import ru.util.WorldHelper;

public class MutatorApples extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.APPLE;
	}

	@Override
	public String getName() {
		return "�������� ������";
	}

	@Override
	public String getDescription() {
		return "� ������ ����� ���� �������� ������";
	}

	@EventHandler
	public void breakLeaves(BlockBreakEvent e) {
		Block b = e.getBlock();
		if(b.getType() == Material.DARK_OAK_LEAVES || b.getType() == Material.OAK_LEAVES) {
			Leaves leaves = (Leaves) b.getState().getBlockData();
			if(!leaves.isPersistent() && MathUtils.chance(7)) {
				WorldHelper.spawnParticlesInside(b, Particle.REDSTONE, Color.YELLOW, 8);
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.APPLE));
			}
		}
	}

	@EventHandler
	public void decay(LeavesDecayEvent e) {
		Block b = e.getBlock();
		if(b.getType() == Material.DARK_OAK_LEAVES || b.getType() == Material.OAK_LEAVES) {
			if(MathUtils.chance(8)) {
				WorldHelper.spawnParticlesInside(b, Particle.REDSTONE, Color.YELLOW, 8);
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.APPLE));
			}
		}
	}

}
