package ru.mutator;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;

public abstract class EffectBasedMutator extends Mutator {

	public abstract PotionEffectType getEffect();
	public abstract int getAmplifier();

	@Override
	public void onChoose() {
		for(Player p : UHC.players) {
			p.addPotionEffect(new PotionEffect(getEffect(), 999999, getAmplifier(), false, false));
		}
	}

	@Override
	public void onDeactivate() {
		for(Player p : UHC.players) {
			p.removePotionEffect(getEffect());
		}
	}

	@Override
	public void update() {
		for(Player p : UHC.players) {
			if(!p.hasPotionEffect(PotionEffectType.JUMP)) {
				p.addPotionEffect(new PotionEffect(getEffect(), 999999, getAmplifier(), false, false));
			}
		}
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

}
