package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.TaskManager;

public class MutatorRandomEffects extends Mutator {

	private int timeToApply = 0;

	private final PotionEffect[] EFFECTS = new PotionEffect[] {
			//Good
			new PotionEffect(PotionEffectType.SPEED, 20 * 20, 0),
			new PotionEffect(PotionEffectType.FAST_DIGGING, 30 * 20, 1),
			new PotionEffect(PotionEffectType.NIGHT_VISION, 60 * 20, 0),
			new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 25 * 20, 0),
			new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 0),
			new PotionEffect(PotionEffectType.JUMP, 20 * 20, 1),

			//Bad
			new PotionEffect(PotionEffectType.GLOWING, 10 * 20, 0),
			new PotionEffect(PotionEffectType.LEVITATION, 10 * 20, 0),
			new PotionEffect(PotionEffectType.BLINDNESS, 8 * 20, 0),
			new PotionEffect(PotionEffectType.SLOW_DIGGING, 25 * 20, 0),
			new PotionEffect(PotionEffectType.HUNGER, 30 * 20, 3),
			new PotionEffect(PotionEffectType.WEAKNESS, 25 * 20, 0),
	};

	@Override
	public Material getItemToShow() {
		return Material.POTION;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
	}

	@Override
	public String getName() {
		return "Случайные Эффекты";
	}

	@Override
	public String getDescription() {
		return "В течение игры к игрокам применяются случайные эффекты, хорошие и плохие";
	}

	@Override
	public void onChoose() {
		reset();
	}

	private PotionEffect getRandomPotionEffect() {
		return MathUtils.choose(EFFECTS);
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	public void reset() {
		timeToApply = MathUtils.randomRange(90, 160);
	}
	@Override
	public void update() {
		if(TaskManager.isSecUpdated()) {
			timeToApply--;
			if(timeToApply <= 0) {
				PotionEffect randomEffect = getRandomPotionEffect();
				for(Player player : PlayerManager.getAliveOnlinePlayers()) {
					player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.5F, 1.5F);
					player.addPotionEffect(randomEffect);
				}
				reset();
			}
		}
	}

}
