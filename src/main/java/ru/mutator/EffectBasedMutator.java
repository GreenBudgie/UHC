package ru.mutator;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;

public abstract class EffectBasedMutator extends Mutator {

	public abstract PotionEffectType getEffect();
	public abstract int getAmplifier();

	@Override
	public void onChoose() {
		for(Player p : PlayerManager.getAliveOnlinePlayers()) {
			p.addPotionEffect(new PotionEffect(getEffect(), 999999, getAmplifier(), false, false));
		}
	}

	@Override
	public void onDeactivate() {
		for(Player p : PlayerManager.getAliveOnlinePlayers()) {
			p.removePotionEffect(getEffect());
		}
	}

	@Override
	public void update() {
		for(UHCPlayer uhcPlayer : PlayerManager.getAlivePlayers()) {
			if(uhcPlayer.isOnline()) {
				Player player = uhcPlayer.getPlayer();
				if(!player.hasPotionEffect(getEffect())) {
					player.addPotionEffect(new PotionEffect(getEffect(), 999999, getAmplifier(), false, false));
				}
			} else if(applyToOfflinePlayerGhosts()) {
				ArmorStand ghost = uhcPlayer.getGhost();
				if(ghost != null) {
					if(!ghost.hasPotionEffect(getEffect())) {
						ghost.addPotionEffect(new PotionEffect(getEffect(), 999999, getAmplifier(), false, false));
					}
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

}
