package ru.greenbudgie.mutator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.PlayerTeam;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.PotionEffectBuilder;
import ru.greenbudgie.util.TaskManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

import static org.bukkit.ChatColor.*;

public class MutatorFriendsForever extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.LODESTONE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Друзья Навеки";
	}

	@Override
	public String getDescription() {
		return "Ты начинаешь чувствовать себя плохо, если отходишь далеко от своего тиммейта. Начинают накладываться " +
				"эффекты медлительности, слабости и даже слепоты. Чем дальше ты находишься, тем хуже эффекты. " +
				"Действие начинается с 20-ти блоков. В другом измерении будет совсем плохо! " +
				"Но не бойся, отравления или иссушения ты не получишь. Если твой тиммейт умер или ты начал играть " +
				"без него, то на тебя эффекты не накладываются.";
	}

	@Override
	public boolean isDuoOnly() {
		return true;
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	@Override
	public void update() {
		if (!TaskManager.isSecUpdated()) {
			return;
		}
		for (PlayerTeam team : PlayerManager.getAliveTeams()) {
			if (!team.isDual() || !team.allPlayersAlive()) {
				continue;
			}
			List<DistanceEffect> effectsForTeam = getEffectsForTeam(team);
			if (effectsForTeam.isEmpty()) {
				continue;
			}
			applyEffects(team, effectsForTeam);
		}
	}

	public String getAdditionalActionBarInfo(Location player1Location, Location player2Location) {
		if (!isActive()) {
			return "";
		}
		double distanceSquared = player1Location.distanceSquared(player2Location);
		List<DistanceEffect> effects = DistanceEffect.getEffectsForDistanceSquared(distanceSquared);
		DistanceEffect lastEffect = getLastEffect(effects);
		if (lastEffect == null) {
			return "";
		}
		return " " + lastEffect.getDistanceInfo();
	}

	private List<DistanceEffect> getEffectsForTeam(PlayerTeam team) {
		Location player1Location = team.getPlayer1().getLocation();
		Location player2Location = team.getPlayer2().getLocation();
		if (player1Location.getWorld() != player2Location.getWorld()) {
			return List.of(DistanceEffect.values());
		}
		double distanceSquared = player1Location.distanceSquared(player2Location);
		return DistanceEffect.getEffectsForDistanceSquared(distanceSquared);
	}

	@Nullable
	private DistanceEffect getLastEffect(List<DistanceEffect> effects) {
		if (effects.isEmpty()) {
			return null;
		}
		return effects.get(effects.size() - 1);
	}

	private void applyEffects(PlayerTeam team, List<DistanceEffect> effects) {
		List<PotionEffect> potionEffects = effects.stream().map(DistanceEffect::getEffect).toList();
		for (UHCPlayer uhcPlayer : team.getPlayers()) {
			if (!uhcPlayer.isAliveAndOnline()) {
				continue;
			}
			Player player = uhcPlayer.getPlayer();
			player.addPotionEffects(potionEffects);
		}
	}

	public enum DistanceEffect {

		QUITE_FAR(
				20,
				effect(PotionEffectType.WEAKNESS),
				GOLD + "" + BOLD + "Ты далековато..."
		),
		FAR(
				35,
				effect(PotionEffectType.SLOW_DIGGING),
				RED + "" + BOLD + "Ты далеко"
		),
		VERY_FAR(
				50,
				effect(PotionEffectType.SLOW),
				DARK_RED + "" + BOLD + "Ты очень далеко!"
		),
		EXTREMELY_FAR(
				65,
				effect(PotionEffectType.HUNGER),
				DARK_PURPLE + "" + BOLD + "Ты крайне далеко!"
		),
		LONELY(
				80,
				effect(PotionEffectType.BLINDNESS),
				DARK_PURPLE + "" + BOLD + "ТЕБЕ ОЧЕНЬ ОДИНОКО"
		),
		;

		private final int minDistanceSq;
		private final PotionEffect effect;
		private final String distanceInfo;

		DistanceEffect(int minDistance, PotionEffect effect, String distanceInfo) {
			this.minDistanceSq = minDistance * minDistance;
			this.effect = effect;
			this.distanceInfo = distanceInfo;
		}

		public PotionEffect getEffect() {
			return effect;
		}

		public String getDistanceInfo() {
			return distanceInfo;
		}

		public static List<DistanceEffect> getEffectsForDistanceSquared(final double distanceSquared) {
			return Stream.of(values()).filter(effect -> effect.minDistanceSq <= distanceSquared).toList();
		}

		private static PotionEffect effect(PotionEffectType type) {
			return new PotionEffectBuilder(type).seconds(5).build();
		}

	}

}
