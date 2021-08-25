package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

public class MutatorNoDiamonds extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.DIAMOND_ORE;
	}

	@Override
	public boolean conflictsWithClasses() {
		return true;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Влад Идет Нахуй";
	}

	@Override
	public String getDescription() {
		return "С алмазной руды теперь будет падать земля вместо алмазов";
	}

	@EventHandler
	public void dig(BlockBreakEvent e) {
		Block b = e.getBlock();
		if(b.getType() == Material.DIAMOND_ORE) {
			e.setDropItems(false);
			e.setExpToDrop(0);
			ParticleUtils.createParticlesInside(b, Particle.SMOKE_NORMAL, null, 10);
			b.getWorld().playSound(b.getLocation(), Sound.ENTITY_PLAYER_BURP, 0.5F, 1F);
			b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.DIRT));
		}
	}


}
