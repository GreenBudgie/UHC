package ru.greenbudgie.mutator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import ru.greenbudgie.UHC.PlayerManager;

public class MutatorKnockback extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.IRON_SWORD;
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
		return another == MutatorManager.noKnockback;
	}

	@EventHandler
	public void attack(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player && e.getFinalDamage() > 0 && e.getEntity() instanceof LivingEntity) {
			if(e.getEntity() instanceof Player) {
				Player p1 = (Player) e.getDamager();
				Player p2 = (Player) e.getEntity();
				if(PlayerManager.isTeammates(p1, p2)) return;
			}
			LivingEntity ent = (LivingEntity) e.getEntity();
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
