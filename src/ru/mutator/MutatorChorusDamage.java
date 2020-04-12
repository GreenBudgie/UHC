package ru.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import ru.UHC.UHC;
import ru.util.WorldHelper;

public class MutatorChorusDamage extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.ENDER_PEARL;
	}

	@Override
	public String getName() {
		return "Побег от Опасности";
	}

	@Override
	public String getDescription() {
		return "При получении урона ты телепортируешься, как будто съел хорус";
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void eat(EntityDamageEvent e) {
		if(!e.isCancelled() && e.getFinalDamage() > 0 && e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(UHC.isPlaying(p) && !p.isInvulnerable() && p.getNoDamageTicks() <= 0) {
				WorldHelper.chorusTeleport(p, 16);
			}
		}
	}

}
