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

	private final Player owner;
	private final Location location;
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
			Player teammate = PlayerManager.getTeammate(owner);
			for(Player player : PlayerManager.getAliveOnlinePlayers()) {
				if((player != owner && (teammate == null || teammate != player)) && location.getWorld() == player.getWorld() && player.getLocation().distance(location) <= range) {
					triggered = true;
					break;
				}
			}
			if(MathUtils.chance(10)) {
				ParticleUtils.createParticle(location.clone().add(0.5, 0.8, 0.5), Particle.SMOKE_NORMAL, null);
			}
		} else {
			if(signalTicks.contains(fuseTicks)) {
				location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1F, 2 - ((float) fuseTicks / (float) maxFuseTicks));
				ParticleUtils.createParticle(location.clone().add(0.5, 0.8, 0.5), Particle.FLAME, null);
			}
			fuseTicks--;
			if(fuseTicks <= 0) {
				if(detonated) {
					secondExplodeTicks--;
					if(secondExplodeTicks <= 0) {
						done = true;
						for(Player p : WorldHelper.getPlayersDistance(location, 6).stream().filter(PlayerManager::isPlaying).collect(Collectors.toList())) {
							FightHelper.setDamager(p, owner, 40, "заминировал");
						}
						location.getWorld().createExplosion(location, 4);
					}
				} else {
					detonated = true;
					location.getBlock().setType(Material.AIR);
					for(Player p : WorldHelper.getPlayersDistance(location, 6).stream().filter(PlayerManager::isPlaying).collect(Collectors.toList())) {
						FightHelper.setDamager(p, owner, 40, "заминировал");
					}
					location.getWorld().createExplosion(location, isSurrounded() ? 4 : 2);
				}
			}
		}
	}

}
