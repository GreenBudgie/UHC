package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.TaskManager;

public class MutatorNoKnockback extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.SLIME_BALL;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
	}

	@Override
	public String getName() {
		return "Устойчивый";
	}

	@Override
	public String getDescription() {
		return "Мобы и игроки не получают никакой отдачи от ударов";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.knockback;
	}

	@EventHandler
	public void attack(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof LivingEntity && e.getEntity() instanceof LivingEntity) {
			if(e.getEntity() instanceof Player p2 && e.getDamager() instanceof Player p1) {
				if(PlayerManager.isTeammates(p1, p2)) return;
			}
			TaskManager.invokeLater(() -> e.getEntity().setVelocity(new Vector(0, 0, 0)));
		}
	}

}
