package ru.greenbudgie.util.weighted;

import org.bukkit.potion.PotionEffectType;

public class WeightedPotionEffectList extends WeightedList<PotionEffectType, WeightedPotionEffect> {

    public WeightedPotionEffectList(WeightedPotionEffect... weightedEffects) {
        super(weightedEffects);
    }

}
