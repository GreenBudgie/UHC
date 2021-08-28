package ru.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import ru.UHC.ArenaManager;
import ru.UHC.GameState;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.util.WorldHelper;

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
	public void eat(EntityDamageEvent e) {
		if(!e.isCancelled() && e.getFinalDamage() > 0 && e.getEntity() instanceof Player player) {
			if(PlayerManager.isPlaying(player) && !player.isInvulnerable() && player.getNoDamageTicks() <= 0) {
				if(UHC.state == GameState.DEATHMATCH && !ArenaManager.getCurrentArena().isOpen()) return;
				WorldHelper.chorusTeleport(player, 16);
			}
		}
	}

}
