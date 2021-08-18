package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

public class MutatorGoodDeath extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.PLAYER_HEAD;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Уйти Красиво";
	}

	@Override
	public String getDescription() {
		return "Когда кто-то из игроков умирает, всем остальным выдается эффект регенерации, восстанавливая 2 сердца";
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void death(PlayerDeathEvent e) {
		if(PlayerManager.isPlaying(e.getEntity())) {
			for(UHCPlayer uhcPlayer : PlayerManager.getAlivePlayers()) {
				if(uhcPlayer.isOnline()) {
					Player player = uhcPlayer.getPlayer();
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50, 2));
					player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 0.5F, 1F);
					ParticleUtils.createParticlesInRange(player.getLocation(), 3, Particle.HEART, null, 15);
				} else {
					uhcPlayer.addOfflineHealth(4);
				}
			}
		}
	}


}
