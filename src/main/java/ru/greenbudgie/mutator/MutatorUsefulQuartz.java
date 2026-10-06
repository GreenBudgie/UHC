package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;

import java.util.Set;

public class MutatorUsefulQuartz extends Mutator implements Listener {

	private static final Set<Material> oresToDrop = Set.of(
			Material.DIAMOND,
			Material.GOLD_INGOT,
			Material.REDSTONE,
			Material.LAPIS_LAZULI,
			Material.IRON_INGOT
	);

	@Override
	public Material getItemToShow() {
		return Material.QUARTZ;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Кварцевая Революция";
	}

	@Override
	public String getDescription() {
		return "С кварцевой руды падают другие случайные руды: алмазы, золото, редстоун, лазурит и железо";
	}

	@EventHandler
	public void healByDiamonds(BlockDropItemEvent event) {
		if (!UHC.playing) {
			return;
		}

		var blockType = event.getBlockState().getType();
		if (blockType != Material.NETHER_QUARTZ_ORE) {
			return;
		}

		var quartzDrops = event.getItems()
				.stream()
				.anyMatch(item -> item.getItemStack().getType() == Material.QUARTZ);
		if (!quartzDrops) {
			return;
		}

		var player = event.getPlayer();
		if (!PlayerManager.isPlaying(player)) {
			return;
		}

		ParticleUtils.createParticlesInside(event.getBlock(), Particle.END_ROD, null, 3);
		for (Item item : event.getItems()) {
			item.getItemStack().setType(MathUtils.choose(oresToDrop));
		}
	}

}
