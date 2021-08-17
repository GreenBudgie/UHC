package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.util.MathUtils;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

public class ArtifactDamage extends Artifact {

	@Override
	public String getName() {
		return ChatColor.DARK_GRAY + "Злодеяние";
	}

	@Override
	public String getDescription() {
		return "Дамажит всех игроков на 2 сердца. Использовавшего игрока дамажит на 2.5 сердца. Умереть нельзя.";
	}

	@Override
	public int getStartingPrice() {
		return 16;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	//TODO Think about offline players
	@Override
	public void onUse(@Nullable Player player) {
		for(Player currentPlayer : PlayerManager.getAliveOnlinePlayers()) {
			boolean doMaxDamage = player == null || player == currentPlayer;
			double damage = MathUtils.clamp(doMaxDamage ? 5 : 4, 0, currentPlayer.getHealth() - 1);
			currentPlayer.damage(damage);
			currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 0.5F);
			ParticleUtils.createParticlesInRange(currentPlayer.getLocation(), 3, Particle.SMOKE_LARGE, null, 15);
		}
	}

	@Override
	public Material getType() {
		return Material.FERMENTED_SPIDER_EYE;
	}

}
