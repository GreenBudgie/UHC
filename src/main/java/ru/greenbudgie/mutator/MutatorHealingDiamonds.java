package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.ParticleUtils;

public class MutatorHealingDiamonds extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.DIAMOND;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Полезная Руда";
	}

	@Override
	public String getDescription() {
		return "Каждая вскопанная алмазная руда восстанавливает 1 ХП";
	}

	@EventHandler
	public void healByDiamonds(BlockDropItemEvent event) {
		if (!UHC.playing) {
			return;
		}

		var blockType = event.getBlockState().getType();
		if (blockType != Material.DIAMOND_ORE && blockType != Material.DEEPSLATE_DIAMOND_ORE) {
			return;
		}

		var diamondDrops = event.getItems()
				.stream()
				.anyMatch(item -> item.getItemStack().getType() == Material.DIAMOND);
		if (!diamondDrops) {
			return;
		}

		var player = event.getPlayer();
		if (!PlayerManager.isPlaying(player)) {
			return;
		}

		ParticleUtils.createParticlesInside(event.getBlock(), Particle.HEART, null, 3);
		double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
		player.setHealth(Math.min(player.getHealth() + 1, maxHealth));
	}

}
