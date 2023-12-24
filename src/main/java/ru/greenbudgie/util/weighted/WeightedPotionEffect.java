package ru.greenbudgie.util.weighted;

import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.PotionEffectBuilder;
import ru.greenbudgie.util.item.Localizer;

import javax.annotation.Nonnull;
import java.util.List;

import static org.bukkit.ChatColor.*;
import static org.bukkit.potion.PotionEffectType.*;

public class WeightedPotionEffect extends WeightedElement<PotionEffectType> {

    private static final List<PotionEffectType> GOOD_EFFECTS = List.of(
            SPEED,
            FAST_DIGGING,
            INCREASE_DAMAGE,
            HEAL,
            JUMP,
            REGENERATION,
            DAMAGE_RESISTANCE,
            FIRE_RESISTANCE,
            WATER_BREATHING,
            INVISIBILITY,
            NIGHT_VISION,
            HEALTH_BOOST,
            ABSORPTION,
            SATURATION,
            LUCK,
            SLOW_FALLING,
            CONDUIT_POWER,
            DOLPHINS_GRACE,
            HERO_OF_THE_VILLAGE
    );

    private final int minDuration;
    private final int maxDuration;
    private final int minAmplifier;
    private final int maxAmplifier;

    private final boolean ambient;
    private final boolean particles;
    private final boolean icon;

    private WeightedPotionEffect(
            @Nonnull PotionEffectType type,
            int weight,
            int minDuration,
            int maxDuration,
            int minAmplifier,
            int maxAmplifier,
            boolean ambient,
            boolean particles,
            boolean icon
    ) {
        super(type, weight);
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.minAmplifier = minAmplifier;
        this.maxAmplifier = maxAmplifier;
        this.ambient = ambient;
        this.particles = particles;
        this.icon = icon;
    }

    public PotionEffect getPotionEffect() {
        return new PotionEffectBuilder(element)
                .ticks(MathUtils.randomRange(minDuration, maxDuration))
                .amplifier(MathUtils.randomRange(minAmplifier, maxAmplifier))
                .ambient(ambient)
                .particles(particles)
                .icon(icon)
                .build();
    }

    public String getPreviewString(boolean isWeighted) {
        String duration;
        if (minDuration == maxDuration) {
            duration = "(" + formatDuration(minDuration) + ")";
        } else {
            duration = "(" + formatDuration(minDuration) + " - " + formatDuration(maxDuration) + ")";
        }
        String amplifier;
        if (minAmplifier == maxAmplifier) {
            amplifier = Localizer.localizeLevel(minAmplifier + 1);
        } else {
            amplifier = Localizer.localizeLevel(minAmplifier + 1) + "-" + Localizer.localizeLevel(maxAmplifier + 1);
        }
        String effectName = Localizer.localizePotionEffectType(element);
        String effectPreview;
        if (minAmplifier == maxAmplifier && minAmplifier == 0) {
            effectPreview = getEffectDescriptionColor(element) + effectName + " " + duration;
        } else {
            effectPreview = getEffectDescriptionColor(element) + effectName + " " + amplifier + " " + duration;
        }
        if (!isWeighted) {
            return effectPreview;
        }
        return effectPreview + WHITE + ", " + GRAY + "шанс " + AQUA + getFormattedChancePercent();
    }

    private String formatDuration(int ticks) {
        int seconds = (int) Math.round(ticks / 20.0);
        return MathUtils.formatTime(seconds);
    }

    private ChatColor getEffectDescriptionColor(PotionEffectType type) {
        return isGoodEffect(type) ? BLUE : RED;
    }

    private boolean isGoodEffect(PotionEffectType type) {
        return GOOD_EFFECTS.contains(type);
    }

    public static Builder builder(PotionEffectType type) {
        return new Builder(type);
    }

    public static class Builder {

        private final PotionEffectType type;

        private int minDuration = 1;
        private int maxDuration = 1;
        private int minAmplifier = 0;
        private int maxAmplifier = 0;

        private boolean ambient = true;
        private boolean particles = true;
        private boolean icon = true;

        private int weight = 1;

        public Builder(PotionEffectType type) {
            this.type = type;
        }

        public Builder ticks(int ticks) {
            return ticks(ticks, ticks);
        }

        public Builder ticks(int minTicks, int maxTicks) {
            this.minDuration = minTicks;
            this.maxDuration = maxTicks;
            return this;
        }

        public Builder seconds(int seconds) {
            return seconds(seconds, seconds);
        }

        public Builder seconds(int minSeconds, int maxSeconds) {
            return ticks(minSeconds * 20, maxSeconds * 20);
        }

        public Builder minutes(int minutes) {
            return minutes(minutes, minutes);
        }

        public Builder minutes(int minMinutes, int maxMinutes) {
            return seconds(minMinutes * 60, maxMinutes * 60);
        }

        public Builder infinite() {
            this.minDuration = PotionEffect.INFINITE_DURATION;
            this.maxDuration = PotionEffect.INFINITE_DURATION;
            return this;
        }

        public Builder amplifier(int amplifier) {
            return amplifier(amplifier, amplifier);
        }

        public Builder amplifier(int minAmplifier, int maxAmplifier) {
            this.minAmplifier = minAmplifier;
            this.maxAmplifier = maxAmplifier;
            return this;
        }

        public Builder hidden() {
            this.particles = false;
            this.ambient = false;
            this.icon = false;
            return this;
        }

        public Builder noParticles() {
            this.particles = false;
            this.ambient = false;
            return this;
        }

        public Builder particles(boolean particles) {
            this.particles = particles;
            return this;
        }

        public Builder ambient(boolean ambient) {
            this.ambient = ambient;
            return this;
        }

        public Builder icon(boolean icon) {
            this.icon = icon;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public WeightedPotionEffect build() {
            return new WeightedPotionEffect(
                    type,
                    weight,
                    minDuration,
                    maxDuration,
                    minAmplifier,
                    maxAmplifier,
                    ambient,
                    particles,
                    icon
            );
        }

    }

}
