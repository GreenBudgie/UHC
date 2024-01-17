package ru.greenbudgie.mutator.duo;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.PotionEffectBuilder;

import static org.bukkit.ChatColor.*;

public class MutatorFriendsForever extends TeammateDistanceBasedMutator<MutatorFriendsForever.FriendsForeverDistanceEffect> {

	public MutatorFriendsForever() {
		super(new TeammateDistanceEffectManager<>(FriendsForeverDistanceEffect.values()));
	}

	@Override
	public Material getItemToShow() {
		return Material.PUMPKIN_PIE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Друзья Навсегда";
	}

	@Override
	public String getDescription() {
		return "Ты чувствуешь уверенность, когда находишься рядом с тиммейтом. Если ты ближе 24-х блоков, " +
				"то тебе выдается спешка. Ближе 12-ти - эффект поглощения урона. " +
				"А если стоишь в радиусе 6-ти блоков, то получаешь силу!";
	}

	public enum FriendsForeverDistanceEffect implements TeammateDistanceEffect {

		SO_CLOSE(
				6,
				effect(PotionEffectType.INCREASE_DAMAGE),
				AQUA + "" + BOLD + "Ты близко!"
		),
		GOING_AWAY(
				12,
				effect(PotionEffectType.DAMAGE_RESISTANCE),
				DARK_AQUA + "" + BOLD + "Ты отдаляешься..."
		),
		FAR(
				24,
				effect(PotionEffectType.FAST_DIGGING),
				RED + "" + BOLD + "Ты теряешь тиммейта..."
		),
		;

		private final int maxDistanceSq;
		private final PotionEffect effect;
		private final String distanceInfo;

		FriendsForeverDistanceEffect(int maxDistance, PotionEffect effect, String distanceInfo) {
			this.maxDistanceSq = maxDistance * maxDistance;
			this.effect = effect;
			this.distanceInfo = distanceInfo;
		}

		@Override
		public int getMaxDistanceSq() {
			return maxDistanceSq;
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
			return DARK_RED + "" + BOLD + "Ты потерял тиммейта!";
		}

		private static PotionEffect effect(PotionEffectType type) {
			return new PotionEffectBuilder(type).seconds(2).build();
		}

	}

}
