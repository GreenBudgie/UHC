package ru.mutator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.util.TaskManager;

public class MutatorNoKnockback extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.WOODEN_SWORD;
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
			if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
				Player p1 = (Player) e.getDamager();
				Player p2 = (Player) e.getEntity();
				if(PlayerManager.isTeammates(p1, p2)) return;
			}
			TaskManager.invokeLater(() -> e.getEntity().setVelocity(new Vector(0, 0, 0)));
		}
	}

}
