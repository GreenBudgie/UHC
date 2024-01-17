package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;

public class MutatorHyperExplosions extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.GUNPOWDER;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Ультра Взрывы";
	}

	@Override
	public String getDescription() {
		return "Любой взрыв становится мощнее и поджигает территорию вокруг";
	}

	public float getPowerMultiplier() {
		return 1.4F;
	}

	@EventHandler
	public void prime(ExplosionPrimeEvent event) {
		event.setFire(true);
		event.setRadius(event.getRadius() * getPowerMultiplier());
	}

	@EventHandler
	public void explode(BlockExplodeEvent event) {
		event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 6, 0.5F);
	}

	@EventHandler
	public void explode(EntityExplodeEvent event) {
		event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 6, 0.5F);
	}

}
