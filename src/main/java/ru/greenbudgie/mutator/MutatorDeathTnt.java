package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.MathUtils;

public class MutatorDeathTnt extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.CREEPER_HEAD;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Взрывной Сюрприз";
	}

	@Override
	public String getDescription() {
		return "Со всех убитых мобов и игроков будет вылетать динамит вверх и взрываться через 4 секунды";
	}

	@EventHandler
	public void dig(EntityDeathEvent e) {
		LivingEntity ent = e.getEntity();
		if (ent.getKiller() == null) {
			return;
		}
		ent.getWorld().playSound(ent.getLocation(), Sound.ENTITY_TNT_PRIMED, 1F, 1.5F);
		TNTPrimed tnt = (TNTPrimed) ent.getWorld().spawnEntity(ent.getLocation(), EntityType.PRIMED_TNT);
		tnt.setVelocity(new Vector(MathUtils.randomRangeDouble(-0.2, 0.2), MathUtils.randomRangeDouble(1, 1.2), MathUtils.randomRangeDouble(-0.2, 0.2)));
		tnt.setFuseTicks(80);
	}


}
