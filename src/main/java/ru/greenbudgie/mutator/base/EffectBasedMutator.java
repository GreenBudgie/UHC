package ru.greenbudgie.mutator.base;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;

public abstract class EffectBasedMutator extends Mutator {

	public abstract EffectEntry[] getEffects();

	@Override
	public void onChoose() {
		for (Player player : PlayerManager.getAliveOnlinePlayers()) {
			for (EffectEntry effectEntry : getEffects()) {
				player.addPotionEffect(effectEntry.getEffect());
			}
		}
	}

	@Override
	public void onDeactivate() {
		for(Player player : PlayerManager.getAliveOnlinePlayers()) {
			for (EffectEntry effectEntry : getEffects()) {
				player.removePotionEffect(effectEntry.type());
			}
		}
	}

	@Override
	public void update() {
		for (UHCPlayer uhcPlayer : PlayerManager.getAlivePlayers()) {
			if (uhcPlayer.isOnline()) {
				Player player = uhcPlayer.getPlayer();
				for (EffectEntry effectEntry : getEffects()) {
					if(!player.hasPotionEffect(effectEntry.type())) {
						player.addPotionEffect(effectEntry.getEffect());
					}
				}
				continue;
			}
			if (!applyToOfflinePlayerGhosts()) {
				continue;
			}
			ArmorStand ghost = uhcPlayer.getGhost();
			if (ghost == null) {
				continue;
			}
			for (EffectEntry effectEntry : getEffects()) {
				if(!ghost.hasPotionEffect(effectEntry.type())) {
					ghost.addPotionEffect(effectEntry.getEffect());
				}
			}
		}
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	public boolean applyToOfflinePlayerGhosts() {
		return false;
	}

	public record EffectEntry(PotionEffectType type, int amplifier) {

		public PotionEffect getEffect() {
			return new PotionEffect(
					type,
					PotionEffect.INFINITE_DURATION,
					amplifier,
					false,
					false
			);
		}

	}

}
