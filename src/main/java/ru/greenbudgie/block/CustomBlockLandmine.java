package ru.greenbudgie.block;

import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.FightHelper;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.mutator.MutatorManager;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.WorldHelper;

import java.util.List;
import java.util.stream.Collectors;

public class CustomBlockLandmine extends CustomBlockItem {

	private final UHCPlayer owner;
	private final int maxFuseTicks = 30;
	private int fuseTicks = maxFuseTicks;
	private int secondExplodeTicks = 4;
	private boolean triggered = false;
	private final int range = 5;
	private final List<Integer> signalTicks = Lists.newArrayList(30, 22, 15, 10, 7, 5, 3, 2, 1);
	private boolean detonated = false;

	public CustomBlockLandmine(Location location, Player owner) {
		super(location);
		this.owner = PlayerManager.asUHCPlayer(owner);
	}

	@Override
	public boolean removeIfRealBlockNotPresent() {
		return false;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		location.getWorld().playSound(location, Sound.ENTITY_ENDER_EYE_DEATH, 1, 1);
	}

	private boolean isSurrounded() {
		return WorldHelper.getBlocksAround(location).stream().allMatch(block -> block.getType().isSolid());
	}

	@Override
	public void onUpdate() {
		if(owner == null) return;
		if(!triggered) {
			Player teammate = null;
			if(owner.getTeammate() != null) teammate = owner.getTeammate().getPlayer();
			for(Player player : PlayerManager.getAliveOnlinePlayers()) {
				if((player != owner.getPlayer() &&
						(teammate == null || teammate != player))
						&& location.getWorld() == player.getWorld()
						&& player.getLocation().distance(location) <= range) {
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
						for(Player p : WorldHelper.getPlayersDistance(location, 6).stream().filter(PlayerManager::isPlaying).collect(Collectors.toList())) {
							FightHelper.setDamager(p, owner, 40, "заминировал");
						}
						float power = 4;
						if(MutatorManager.hyperExplosions.isActive()) {
							location.getWorld().createExplosion(location, power * MutatorManager.hyperExplosions.getPowerMultiplier(), true);
						} else {
							location.getWorld().createExplosion(location, power);
						}
						remove();
					}
				} else {
					detonated = true;
					location.getBlock().setType(Material.AIR);
					for(Player p : WorldHelper.getPlayersDistance(location, 6).stream().filter(PlayerManager::isPlaying).collect(Collectors.toList())) {
						FightHelper.setDamager(p, owner, 40, "заминировал");
					}
					float power = isSurrounded() ? 4 : 2;
					if(MutatorManager.hyperExplosions.isActive()) {
						location.getWorld().createExplosion(location, power * MutatorManager.hyperExplosions.getPowerMultiplier(), true);
					} else {
						location.getWorld().createExplosion(location, power);
					}
				}
			}
		}
	}

	@Override
	public CustomItem getRepresentingItem() {
		return CustomItems.landmine;
	}

	@EventHandler
	public void place(BlockPlaceEvent e) {
		if(PlayerManager.isPlaying(e.getPlayer()) && UHC.state.isBeforeDeathmatch()) {
			ItemStack item = e.getItemInHand();
			if(item.getType() == Material.DIRT) {
				Block under = e.getBlock().getLocation().clone().add(0, -1, 0).getBlock();
				if(equals(under)) {
					e.getBlock().setType(Material.GRASS_BLOCK);
					ParticleUtils.createParticlesOutline(e.getBlock(), Particle.VILLAGER_HAPPY, null, 20);
					e.getBlock().getWorld().playSound(e.getBlock().getLocation(), Sound.BLOCK_CHORUS_FLOWER_GROW, 1F, 1F);
				}
			}
		}
	}

}
