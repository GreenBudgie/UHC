package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;
import ru.util.MathUtils;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

public class ArtifactDamage extends Artifact {

	@Override
	public String getName() {
		return ChatColor.DARK_GRAY + "���������";
	}

	@Override
	public String getDescription() {
		return "������� ���� ������� �� 2 ������. ��������������� ������ ������� �� 2.5 ������. ������� ������.";
	}

	@Override
	public int getStartingPrice() {
		return 16;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public void onUse(@Nullable Player player) {
		for(Player currentPlayer : UHC.players) {
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
