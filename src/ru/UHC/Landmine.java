package ru.UHC;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.util.MathUtils;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

import java.util.List;
import java.util.stream.Collectors;

public class Landmine {

	private Player owner;
	private Location location;
	private final int maxFuseTicks = 30;
	private int fuseTicks = maxFuseTicks;
	private int secondExplodeTicks = 4;
	private boolean triggered = false;
	private final int range = 5;
	private final List<Integer> signalTicks = Lists.newArrayList(30, 22, 15, 10, 7, 5, 3, 2, 1);
	private boolean done = false;
	private boolean detonated = false;

	public Landmine(Player owner, Location loc) {
		this.owner = owner;
		location = loc;
	}

	public Location getLocation() {
		return location;
	}

	private boolean isSurrounded() {
		return WorldHelper.getBlocksAround(location).stream().allMatch(block -> block.getType().isSolid());
	}

	public void update() {
		if(done) return;
		if(!triggered) {
			Player teammate = UHC.getTeammate(owner);
			for(Player p : UHC.players) {
				if((p != owner && (teammate == null || teammate != p)) && location.getWorld() == p.getWorld() && p.getLocation().distance(location) <= range) {
					triggered = true;
					break;
				}
			}
			if(MathUtils.chance(10)) {
				ParticleUtils.createParticle(location.clone().add(0.5, 0.8, 0.5), Particle.SMOKE_NORMAL, null);
			}
		} else {
			if(signalTicks.indexOf(fuseTicks) != -1) {
				location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1F, 2 - ((float) fuseTicks / (float) maxFuseTicks));
				ParticleUtils.createParticle(location.clone().add(0.5, 0.8, 0.5), Particle.FLAME, null);
			}
			fuseTicks--;
			if(fuseTicks <= 0) {
				if(detonated) {
					secondExplodeTicks--;
					if(secondExplodeTicks <= 0) {
						done = true;
						for(Player p : WorldHelper.getPlayersDistance(location, 6).stream().filter(UHC::isPlaying).collect(Collectors.toList())) {
							FightHelper.setDamager(p, owner, 40, "заминировал");
						}
						location.getWorld().createExplosion(location, 4);
					}
				} else {
					detonated = true;
					location.getBlock().setType(Material.AIR);
					for(Player p : WorldHelper.getPlayersDistance(location, 6).stream().filter(UHC::isPlaying).collect(Collectors.toList())) {
						FightHelper.setDamager(p, owner, 40, "заминировал");
					}
					location.getWorld().createExplosion(location, isSurrounded() ? 4 : 2);
				}
			}
		}
	}

}
