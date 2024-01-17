package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.event.UHCPlayerDeathEvent;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.ParticleUtils;

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
		return "Когда кто-то из игроков умирает, всем остальным выдается эффект регенерации, восстанавливая 1 сердце";
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void death(UHCPlayerDeathEvent event) {
		for(UHCPlayer uhcPlayer : PlayerManager.getAlivePlayers()) {
			if(uhcPlayer == event.getUHCPlayer()) continue;
			if(uhcPlayer.isOnline()) {
				Player player = uhcPlayer.getPlayer();
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 0));
				player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 0.5F, 1F);
				ParticleUtils.createParticlesInRange(player.getLocation(), 3, Particle.HEART, null, 15);
			} else {
				uhcPlayer.addOfflineHealth(2);
			}
		}
	}


}
