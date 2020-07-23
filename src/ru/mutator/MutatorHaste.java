package ru.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;

public class MutatorHaste extends Mutator {

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public void onChoose() {
		for(Player p : UHC.players) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999, 1, false, false));
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, false, false));
		}
	}

	@Override
	public void onDeactivate() {
		for(Player p : UHC.players) {
			p.removePotionEffect(PotionEffectType.FAST_DIGGING);
			p.removePotionEffect(PotionEffectType.SPEED);
		}
	}

	@Override
	public void update() {
		for(Player p : UHC.players) {
			if(!p.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999, 1, false, false));
			}
			if(!p.hasPotionEffect(PotionEffectType.SPEED)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, false, false));
			}
		}
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	@Override
	public Material getItemToShow() {
		return Material.GOLDEN_PICKAXE;
	}

	@Override
	public String getName() {
		return "Гипершахтер";
	}

	@Override
	public String getDescription() {
		return "Все игроки становятся буквально быстрее. Выдается Скорость и Спешка II";
	}


}
