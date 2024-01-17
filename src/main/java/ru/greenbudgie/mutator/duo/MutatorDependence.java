package ru.greenbudgie.mutator.duo;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.mutator.ThreatStatus;
import ru.greenbudgie.util.PotionEffectBuilder;

import static org.bukkit.ChatColor.*;

public class MutatorDependence extends TeammateDistanceBasedMutator<MutatorDependence.DependenceDistanceEffect> {

	public MutatorDependence() {
		super(new TeammateDistanceEffectManager<>(DependenceDistanceEffect.values()));
	}

	@Override
	public Material getItemToShow() {
		return Material.SUSPICIOUS_STEW;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Зависимость";
	}

	@Override
	public String getDescription() {
		return "Ты начинаешь чувствовать себя плохо, если отходишь далеко от своего тиммейта. Начинают накладываться " +
				"эффекты медлительности, слабости и даже слепоты. Чем дальше ты находишься, тем хуже эффекты. " +
				"Действие начинается с 15-ти блоков. В другом измерении будет совсем плохо! " +
				"Но не бойся, отравления или иссушения ты не получишь. Если твой тиммейт умер или ты начал играть " +
				"без него, то на тебя эффекты не накладываются.";
	}

	public enum DependenceDistanceEffect implements TeammateDistanceEffect {

		QUITE_FAR(
				15,
				effect(PotionEffectType.WEAKNESS),
				GOLD + "" + BOLD + "Ты далековато..."
		),
		FAR(
				25,
				effect(PotionEffectType.SLOW_DIGGING),
				RED + "" + BOLD + "Ты далеко"
		),
		VERY_FAR(
				35,
				effect(PotionEffectType.SLOW),
				DARK_RED + "" + BOLD + "Ты очень далеко!"
		),
		EXTREMELY_FAR(
				45,
				effect(PotionEffectType.HUNGER),
				DARK_PURPLE + "" + BOLD + "Ты крайне далеко!"
		),
		LONELY(
				55,
				effect(PotionEffectType.BLINDNESS),
				DARK_PURPLE + "" + BOLD + "ТЕБЕ ОЧЕНЬ ОДИНОКО"
		),
		;

		private final int minDistanceSq;
		private final PotionEffect effect;
		private final String distanceInfo;

		DependenceDistanceEffect(int minDistance, PotionEffect effect, String distanceInfo) {
			this.minDistanceSq = minDistance * minDistance;
			this.effect = effect;
			this.distanceInfo = distanceInfo;
		}

		@Override
		public int getMinDistanceSq() {
			return minDistanceSq;
		}

		@Override
		public PotionEffect getPotionEffect() {
			return effect;
		}

		@Override
		public String getDistanceInfo() {
			return distanceInfo;
		}

		@Override
		public String getTooFarDistanceInfo() {
			return AQUA + "" + BOLD + "Тебе комфортно";
		}

		private static PotionEffect effect(PotionEffectType type) {
			return new PotionEffectBuilder(type).seconds(5).build();
		}

	}

}
