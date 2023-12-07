package ru.greenbudgie.artifact;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;

import javax.annotation.Nullable;
import java.util.List;

public class ArtifactRandomEffect extends Artifact {

	private static final PotionEffect slowness = new PotionEffect(
			PotionEffectType.SLOW,
			3 * 60 * 20,
			0
	);
	private static final PotionEffect blindness = new PotionEffect(
			PotionEffectType.BLINDNESS,
			40 * 20,
			0
	);
	private static final PotionEffect fatigue = new PotionEffect(
			PotionEffectType.SLOW_DIGGING,
			3 * 60 * 20,
			0
	);
	private static final PotionEffect hunger = new PotionEffect(
			PotionEffectType.HUNGER,
			3 * 60 * 20,
			0
	);
	private static final PotionEffect poison = new PotionEffect(
			PotionEffectType.POISON,
			6 * 20,
			0
	);
	private static final PotionEffect levitation = new PotionEffect(
			PotionEffectType.LEVITATION,
			20 * 20,
			0
	);
	private static final PotionEffect weakness = new PotionEffect(
			PotionEffectType.WEAKNESS,
			3 * 60 * 20,
			0
	);
	private static final List<PotionEffect> effects = List.of(
			slowness,
			blindness,
			fatigue,
			hunger,
			poison,
			levitation,
			weakness
	);

	@Override
	public String getName() {
		return "Проклятие";
	}

	@Override
	public String getDescription() {
		return "Накладывает случайный отрицательный эффект на всех игроков, за исключением иссушения и моментального урона.";
	}

	@Override
	public int getStartingPrice() {
		return 14;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		for(Player currentPlayer : PlayerManager.getAliveOnlinePlayers()) {
			PotionEffect chosenEffect = MathUtils.choose(effects);
			currentPlayer.addPotionEffect(chosenEffect);
			ParticleUtils.createParticlesInRange(currentPlayer.getLocation(), 3, Particle.SMOKE_NORMAL, null, 20);
		}
		for(Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1F, 0.9F);
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.SUSPICIOUS_STEW;
	}

}
