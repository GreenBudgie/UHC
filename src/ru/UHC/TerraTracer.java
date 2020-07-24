package ru.UHC;

import org.bukkit.*;
import ru.util.MathUtils;
import ru.util.ParticleUtils;
import ru.util.TaskManager;

public class TerraTracer {

	private static final int RADIUS = 8;
	private Location location;
	private int tick = 0, phase = 0;
	private boolean working = true;

	private TerraTracer() {
	}

	public static void putTerraTracer(Location location) {
		TerraTracer tracer = new TerraTracer();
		tracer.location = location;
		location.getWorld().playSound(location, Sound.BLOCK_END_PORTAL_SPAWN, 0.5F, 0.5F);
		ParticleUtils.createParticlesInsideSphere(location, RADIUS / 1.5, Particle.SMOKE_NORMAL, null, 40);
		UHC.tracers.add(tracer);
	}

	public void update() {
		if(working) {
			tick++;
			if(tick > 0 && tick % 30 == 0) {
				OreType ore = OreType.values()[phase];
				int count = 0;
				for(int x = -RADIUS; x <= RADIUS; x++) {
					for(int z = -RADIUS; z <= RADIUS; z++) {
						for(int y = 1; y < 60; y++) {
							if(location.clone().add(x, -y, z).getBlock().getType() == ore.oreToSearch) {
								count++;
							}
						}
					}
				}
				if(count > 0) {
					Location show = location.clone().add(MathUtils.randomRangeDouble(-1.5,  1.5), MathUtils.randomRangeDouble(1.5, 2.5),
							MathUtils.randomRangeDouble(-1.5, 1.5));
					ParticleUtils.createParticlesInsideSphere(show, 0.8, Particle.REDSTONE, ore.color, count);
					ParticleUtils.createParticle(show, Particle.FLASH, null);
					show.getWorld().playSound(show, Sound.ENTITY_ENDER_EYE_DEATH, (float) MathUtils.clamp(count / 5.0, 0.8, 1.5),
							(float) MathUtils.clamp(count / 5.0, 1, 2));
				} else {
					location.getWorld().playSound(location, Sound.ENTITY_PLAYER_BURP, 0.5F, 1F);
					ParticleUtils.createParticle(location.clone().add(0.5, 1.5, 0.5), Particle.SMOKE_NORMAL, null);
				}
				if(phase < OreType.values().length - 1) {
					phase++;
				} else {
					working = false;
				}
			}
			if(TaskManager.ticksPassed(50)) {
				ParticleUtils.createCircle(location.clone().add(0, 0.7, 0), Particle.CAMPFIRE_COSY_SMOKE, null, RADIUS, 50);
			}
		}
	}

	public Location getLocation() {
		return location;
	}

	private enum OreType {

		COAL(Color.BLACK, Material.COAL_ORE),
		IRON(Color.fromRGB(132, 110, 90), Material.IRON_ORE),
		REDSTONE(Color.RED, Material.REDSTONE_ORE),
		LAPIS(Color.BLUE, Material.LAPIS_ORE),
		GOLD(Color.YELLOW, Material.GOLD_ORE),
		DIAMOND(Color.AQUA, Material.DIAMOND_ORE),
		EMERALD(Color.GREEN, Material.EMERALD_ORE);

		Color color;
		Material oreToSearch;

		OreType(Color color, Material ore) {
			this.color = color;
			oreToSearch = ore;
		}

	}

}
