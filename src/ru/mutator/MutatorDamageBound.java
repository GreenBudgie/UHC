package ru.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.UHC.UHC;
import ru.util.MathUtils;

import java.util.List;

public class MutatorDamageBound extends Mutator implements Listener {

	public int maxDamage = 4;

	@Override
	public Material getItemToShow() {
		return Material.ENDER_EYE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "��������� ����";
	}

	@Override
	public String getDescription() {
		return "����� ���-�� �� ������� �������� ����, ��� ��������� ������ �������� ����� �� ����� �����";
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void damage(EntityDamageEvent e) {
		if(!e.isCancelled() && e.getCause() != EntityDamageEvent.DamageCause.CUSTOM && e.getEntity() instanceof Player) {
			Player damager = (Player) e.getEntity();
			if(UHC.isPlaying(damager)) {
				List<Player> playersCopy = Lists.newArrayList(UHC.players);
				double finalDamage = Math.min(e.getFinalDamage(), damager.getHealth());
				for(Player victim : playersCopy) {
					if(damager != victim) {
						if(victim.getHealth() > 1) {
							double damage = finalDamage / (MathUtils.clamp(UHC.players.size(), 8, 20) * 1.5);
							damage = Math.min(damage, maxDamage);
							if(damage > victim.getHealth() - 1) damage = victim.getHealth() - 1;
							victim.damage(damage);
						}
					}
				}
			}
		}
	}


}
