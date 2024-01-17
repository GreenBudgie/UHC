package ru.greenbudgie.mutator.duo;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import ru.greenbudgie.UHC.PlayerTeam;
import ru.greenbudgie.UHC.UHCPlayer;

import javax.annotation.Nullable;
import java.util.List;

public class TeammateDistanceEffectManager<T extends TeammateDistanceEffect> {

    private final List<T> teammateDistanceEffects;
    private final boolean usesMinDistance;

    public TeammateDistanceEffectManager(T[] teammateDistanceEffects) {
        if (teammateDistanceEffects.length == 0) {
            throw new IllegalArgumentException("No teammate distance effects provided");
        }
        this.usesMinDistance = teammateDistanceEffects[0].getMinDistanceSq() != -1;
        this.teammateDistanceEffects = List.of(teammateDistanceEffects);
        this.teammateDistanceEffects.forEach(this::validateDistanceConfiguration);
    }

    public List<T> getEffectsForTeam(PlayerTeam team) {
        Location player1Location = team.getPlayer1().getLocation();
        Location player2Location = team.getPlayer2().getLocation();
        if (player1Location.getWorld() != player2Location.getWorld()) {
            return teammateDistanceEffects;
        }
        double distanceSquared = player1Location.distanceSquared(player2Location);
        return getEffectsForDistanceSquared(distanceSquared);
    }

    public void applyEffects(PlayerTeam team, List<T> effects) {
        List<PotionEffect> potionEffects = effects.stream().map(T::getPotionEffect).toList();
        for (UHCPlayer uhcPlayer : team.getPlayers()) {
            if (!uhcPlayer.isAliveAndOnline()) {
                continue;
            }
            Player player = uhcPlayer.getPlayer();
            player.addPotionEffects(potionEffects);
        }
    }

    @Nullable
    public String getDistanceInfoFor(final double distanceSquared) {
        List<T> effects = getEffectsForDistanceSquared(distanceSquared);
        if (usesMinDistance) {
            if (effects.isEmpty()) {
                return this.teammateDistanceEffects.get(0).getTooFarDistanceInfo();
            }
            return effects.get(effects.size() - 1).getDistanceInfo();
        }
        if (effects.isEmpty()) {
            return this.teammateDistanceEffects.get(0).getTooFarDistanceInfo();
        }
        return effects.get(0).getDistanceInfo();
    }

    public List<T> getEffectsForDistanceSquared(final double distanceSquared) {
        return teammateDistanceEffects.stream()
                .filter(effect -> distanceApplies(effect, distanceSquared))
                .toList();
    }

    private void validateDistanceConfiguration(T teammateDistanceEffect) {
        int minDistance = teammateDistanceEffect.getMinDistanceSq();
        int maxDistance = teammateDistanceEffect.getMaxDistanceSq();
        if (usesMinDistance && maxDistance != -1) {
            throw new IllegalStateException(
                    "A mix of min/max distance effects are provided"
            );
        }
        if (minDistance == -1 && maxDistance == -1) {
            throw new IllegalStateException(
                    "Teammate distance effect has neither min distance nor max distance configured"
            );
        }
        if (minDistance != -1 && maxDistance != -1) {
            throw new IllegalStateException(
                    "Teammate distance effect has both min and max distance configured"
            );
        }
    }

    private boolean distanceApplies(T teammateDistanceEffect, double distanceSq) {
        int minDistance = teammateDistanceEffect.getMinDistanceSq();
        int maxDistance = teammateDistanceEffect.getMaxDistanceSq();
        if (usesMinDistance && minDistance <= distanceSq) {
            return true;
        }
        return maxDistance >= distanceSq;
    }

}
