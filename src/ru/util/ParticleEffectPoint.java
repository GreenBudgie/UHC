package ru.util;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectType;
import org.bukkit.Particle;
import ru.main.UHCPlugin;

public class ParticleEffectPoint extends Effect {

	public ParticleEffectPoint() {
		super(UHCPlugin.em);
		type = EffectType.INSTANT;
		visibleRange = 128F;
	}

	public Particle particle = Particle.REDSTONE;
	public int amount = 1;

	@Override
	public void onRun() {
		this.display(particle, this.getLocation(), color, 0, amount);
	}

}
