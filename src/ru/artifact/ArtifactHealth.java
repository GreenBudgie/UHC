package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.main.UHCPlugin;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

import javax.annotation.Nullable;

public class ArtifactHealth extends Artifact {

	@Override
	public String getName() {
		return ChatColor.GREEN + "Добродетель";
	}

	@Override
	public String getDescription() {
		return "Выдает всем игрокам эффект регенерации, восстанавливая 2 сердца. Использовавшему артефакт игроку восстанавливает 1.5 сердца.";
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
		for(UHCPlayer uhcCurrentPlayer : PlayerManager.getAlivePlayers()) {
			if(uhcCurrentPlayer.isOnline()) {
				Player currentPlayer = uhcCurrentPlayer.getPlayer();
				boolean applyMaxHeal = player != null && player != currentPlayer;
				currentPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, applyMaxHeal ? 50 : 38, 2));
				ParticleUtils.createParticlesInRange(currentPlayer.getLocation(), 3, Particle.HEART, null, 15);
				currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_VILLAGER_YES, 1F, 1F);
			} else {
				uhcCurrentPlayer.addOfflineHealth(4);
			}
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.RED_DYE;
	}

}
