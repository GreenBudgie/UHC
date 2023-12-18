package ru.greenbudgie.util;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectBuilder {

    private final PotionEffectType type;

    private int duration = 1;

    private int amplifier = 0;

    private boolean ambient = true;
    private boolean particles = true;

    private boolean icon = true;

    public PotionEffectBuilder(PotionEffectType type) {
        this.type = type;
    }

    public PotionEffectBuilder ticks(int ticks) {
        this.duration = ticks;
        return this;
    }

    public PotionEffectBuilder seconds(int seconds) {
        return ticks(seconds * 20);
    }

    public PotionEffectBuilder minutes(int minutes) {
        return seconds(minutes * 60);
    }

    public PotionEffectBuilder infinite() {
        this.duration = PotionEffect.INFINITE_DURATION;
        return this;
    }

    public PotionEffectBuilder amplifier(int amplifier) {
        this.amplifier = amplifier;
        return this;
    }

    public PotionEffectBuilder hidden() {
        this.particles = false;
        this.ambient = false;
        this.icon = false;
        return this;
    }

    public PotionEffectBuilder noParticles() {
        this.particles = false;
        this.ambient = false;
        return this;
    }

    public PotionEffectBuilder particles(boolean particles) {
        this.particles = particles;
        return this;
    }

    public PotionEffectBuilder ambient(boolean ambient) {
        this.ambient = ambient;
        return this;
    }

    public PotionEffectBuilder icon(boolean icon) {
        this.icon = icon;
        return this;
    }

    public PotionEffect build() {
        return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
    }

}
