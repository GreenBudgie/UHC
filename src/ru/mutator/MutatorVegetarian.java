package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import ru.UHC.UHC;

import java.util.Arrays;
import java.util.stream.Stream;

public class MutatorVegetarian extends Mutator implements Listener {

	private Material[] meat = new Material[] {
			Material.PORKCHOP,
			Material.COOKED_PORKCHOP,
			Material.COD,
			Material.COOKED_COD,
			Material.SALMON,
			Material.COOKED_SALMON,
			Material.TROPICAL_FISH,
			Material.BEEF,
			Material.COOKED_BEEF,
			Material.CHICKEN,
			Material.COOKED_CHICKEN,
			Material.ROTTEN_FLESH,
			Material.RABBIT,
			Material.COOKED_RABBIT,
			Material.RABBIT_STEW,
			Material.MUTTON,
			Material.COOKED_MUTTON
	};

	@Override
	public Material getItemToShow() {
		return Material.CARROT;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Вегетарианец";
	}

	@Override
	public String getDescription() {
		return "Мясо есть нельзя: наложится эффект отравления";
	}

	@EventHandler
	public void noDrink(PlayerItemConsumeEvent e) {
		Material type = e.getItem().getType();
		if(Arrays.asList(meat).contains(type)) {
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 2));
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 20, 0));
		}
	}

}
