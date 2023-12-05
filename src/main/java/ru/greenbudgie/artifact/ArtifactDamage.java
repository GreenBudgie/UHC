package ru.greenbudgie.artifact;

import org.bukkit.ChatColor;
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

    @Override
    public String getName() {
        return ChatColor.DARK_GRAY + "Злодеяние";
    }

    @Override
    public String getDescription() {
        return "Дамажит всех игроков на 1.5 сердца. Использовавшего игрока дамажит на 2 сердца. Умереть нельзя.";
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
    public boolean onUse(@Nullable Player player) {
        for(UHCPlayer uhcCurrentPlayer : PlayerManager.getAlivePlayers()) {
            if(uhcCurrentPlayer.isOnline()) {
                Player currentPlayer = uhcCurrentPlayer.getPlayer();
                boolean doMaxDamage = player == null || player == currentPlayer;
                double damage = MathUtils.clamp(doMaxDamage ? 4 : 3, 0, currentPlayer.getHealth() - 1);
                currentPlayer.damage(damage);
                currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 0.5F);
                ParticleUtils.createParticlesInRange(currentPlayer.getLocation(), 3, Particle.SMOKE_LARGE, null, 15);
            } else {
                double damage = MathUtils.clamp(3, 0, uhcCurrentPlayer.getOfflineHealth() - 1);
                uhcCurrentPlayer.addOfflineHealth(-damage);
            }
        }
        return true;
    }

    @Override
    public Material getType() {
        return Material.FERMENTED_SPIDER_EYE;
    }

}
