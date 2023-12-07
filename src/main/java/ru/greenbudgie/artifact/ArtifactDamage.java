package ru.greenbudgie.artifact;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;

import javax.annotation.Nullable;

public class ArtifactDamage extends Artifact {

    private static final int SELF_DAMAGE = 6;
    private static final int DAMAGE = 4;

    @Override
    public String getName() {
        return "Злодеяние";
    }

    @Override
    public String getDescription() {
        return "Наносит всем игрокам урон в 2 сердца. Использовавший игрок получает урон в 3 сердца. Умереть от этого артефакта нельзя.";
    }

    @Override
    public int getStartingPrice() {
        return 16;
    }

    @Override
    public float getPriceIncreaseAmount() {
        return 1.5F;
    }

    @Override
    public boolean onUse(@Nullable Player player) {
        for(UHCPlayer uhcCurrentPlayer : PlayerManager.getAlivePlayers()) {
            if(uhcCurrentPlayer.isOnline()) {
                Player currentPlayer = uhcCurrentPlayer.getPlayer();
                boolean isSelfDamage = player == currentPlayer;
                double damage = MathUtils.clamp(isSelfDamage ? SELF_DAMAGE : DAMAGE, 0, currentPlayer.getHealth() - 1);
                currentPlayer.damage(damage);
                ParticleUtils.createParticlesInRange(currentPlayer.getLocation(), 3, Particle.SMOKE_LARGE, null, 15);
            } else {
                double damage = MathUtils.clamp(DAMAGE, 0, uhcCurrentPlayer.getOfflineHealth() - 1);
                uhcCurrentPlayer.addOfflineHealth(-damage);
            }
        }
        for(Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
            currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 0.5F);
        }
        return true;
    }

    @Override
    public Material getType() {
        return Material.FERMENTED_SPIDER_EYE;
    }

}
