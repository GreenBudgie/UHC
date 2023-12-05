package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.greenbudgie.UHC.ArenaManager;
import ru.greenbudgie.UHC.GameState;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.util.WorldHelper;

public class MutatorChorusDamage extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.ENDER_PEARL;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Побег от Опасности";
	}

	@Override
	public String getDescription() {
		return "При получении урона ты телепортируешься, как будто съел хорус. Не работает на закрытых аренах.";
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void teleportOnDamage(EntityDamageEvent e) {
		if(!e.isCancelled() && e.getFinalDamage() > 0 && e.getEntity() instanceof Player player) {
			if(PlayerManager.isPlaying(player) && !player.isInvulnerable() && player.getNoDamageTicks() <= 0) {
				if(UHC.state == GameState.DEATHMATCH && !ArenaManager.getCurrentArena().isOpen()) return;
				WorldHelper.chorusTeleport(player, 16);
			}
		}
	}

}
