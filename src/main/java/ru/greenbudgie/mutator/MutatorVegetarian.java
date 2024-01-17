package ru.greenbudgie.mutator;

import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.mutator.manager.MutatorManager;

import java.util.Arrays;

public class MutatorVegetarian extends Mutator implements Listener {

	private static final int INCREASED_TICK_SPEED = 30;

	private static final Material[] meat = new Material[] {
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
		return "Аграрное Общество Вегетарианцев";
	}

	@Override
	public String getDescription() {
		return "Все посевы растут в 10 раз быстрее, но при этом мясо есть нельзя: наложится эффект отравления. Другими словами, установлен randomTickSpeed = 30.";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.noHunger;
	}

	@Override
	public void onChoose() {
		WorldManager.getGameMap().setGameRule(GameRule.RANDOM_TICK_SPEED, INCREASED_TICK_SPEED);
		WorldManager.getGameMapNether().setGameRule(GameRule.RANDOM_TICK_SPEED, INCREASED_TICK_SPEED);
	}

	@Override
	public void onDeactivate() {
		WorldManager.getGameMap().setGameRule(GameRule.RANDOM_TICK_SPEED, 3);
		WorldManager.getGameMapNether().setGameRule(GameRule.RANDOM_TICK_SPEED, 3);
	}

	@EventHandler
	public void poisonOnConsume(PlayerItemConsumeEvent e) {
		Material type = e.getItem().getType();
		if(Arrays.asList(meat).contains(type)) {
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 2));
		}
	}

}
