package ru.greenbudgie.mutator.duo;

import org.bukkit.potion.PotionEffect;

import javax.annotation.Nullable;

public interface TeammateDistanceEffect {

    default int getMinDistanceSq() {
        return -1;
    }

    default int getMaxDistanceSq() {
        return -1;
    }

    PotionEffect getPotionEffect();

    String getDistanceInfo();

    @Nullable
    default String getTooFarDistanceInfo() {
        return null;
    }

}
