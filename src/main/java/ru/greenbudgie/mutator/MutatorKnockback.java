package ru.greenbudgie.mutator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.mutator.manager.MutatorManager;

public class MutatorKnockback extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.PISTON;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
	}

	@Override
	public String getName() {
		return "Отдача";
	}

	@Override
	public String getDescription() {
		return "Обычные удары далеко откидывают всех живых существ";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.lowMeleeDamage;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void attack(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player damager
				&& e.getFinalDamage() > 0
				&& !e.isCancelled()
				&& e.getEntity() instanceof LivingEntity ent) {
			if(e.getEntity() instanceof Player player) {
				if(PlayerManager.isTeammates(damager, player)) return;
			}
			Location locP = e.getDamager().getLocation();
			Location locO = ent.getLocation();

			double d5 = locO.getX() - locP.getX();
			double d7 = locO.getY() + ent.getEyeHeight() - locP.getY();
			double d9 = locO.getZ() - locP.getZ();
			double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

			if(d13 != 0.0D) {
				d5 = d5 / d13;
				d7 = d7 / d13;
				d9 = d9 / d13;

				ent.setVelocity(new Vector(d5, d7, d9).multiply(2));
			}
		}
	}

}
