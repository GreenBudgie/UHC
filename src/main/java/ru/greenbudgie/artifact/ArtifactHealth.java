package ru.greenbudgie.artifact;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.ParticleUtils;

import javax.annotation.Nullable;

public class ArtifactHealth extends Artifact {

	@Override
	public String getName() {
		return "Добродетель";
	}

	@Override
	public String getDescription() {
		return "Выдает всем игрокам эффект регенерации, восстанавливая 2 сердца. Использовавшему артефакт игроку восстанавливает 1 сердце.";
	}

	@Override
	public int getStartingPrice() {
		return 14;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 2F;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		for(UHCPlayer uhcCurrentPlayer : PlayerManager.getAlivePlayers()) {
			if(uhcCurrentPlayer.isOnline()) {
				Player currentPlayer = uhcCurrentPlayer.getPlayer();
				boolean applyMaxHeal = player != null && player != currentPlayer;
				currentPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, applyMaxHeal ? 50 : 28, 2));
				ParticleUtils.createParticlesInRange(currentPlayer.getLocation(), 3, Particle.HEART, null, 15);
			} else {
				uhcCurrentPlayer.addOfflineHealth(4);
			}
		}
		for(Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_VILLAGER_YES, 1F, 1F);
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.RED_DYE;
	}

}
