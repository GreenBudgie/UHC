package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.mutator.manager.MutatorManager;
import ru.greenbudgie.util.ParticleUtils;

import java.util.Set;

public class MutatorLowMeleeDamage extends Mutator implements Listener {

	private static final Set<EntityDamageEvent.DamageCause> causesToIgnore = Set.of(
			EntityDamageEvent.DamageCause.ENTITY_ATTACK,
			EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
	);

	@Override
	public Material getItemToShow() {
		return Material.GOLDEN_SWORD;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "На Дистанции";
	}

	@Override
	public String getDescription() {
		return "Удары в ближнем бою как по игрокам, так и по мобам, наносят лишь половину урона. Время использовать луки!";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.knockback;
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!causesToIgnore.contains(event.getCause())) {
			return;
		}
		if (event.getDamager() instanceof Player) {
			ParticleUtils.createParticlesAround(event.getEntity(), Particle.CLOUD, null, 5);
			event.setDamage(event.getDamage() / 2.0);
		}
	}


}
