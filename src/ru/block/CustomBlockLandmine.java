package ru.block;

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
import ru.UHC.FightHelper;
import ru.UHC.GameState;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.items.CustomItem;
import ru.items.CustomItems;
import ru.mutator.MutatorManager;
import ru.util.MathUtils;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

import java.util.List;
import java.util.stream.Collectors;

public class CustomBlockLandmine extends CustomBlockItem {

	private final Player owner;
	private final int maxFuseTicks = 30;
	private int fuseTicks = maxFuseTicks;
	private int secondExplodeTicks = 4;
	private boolean triggered = false;
	private final int range = 5;
	private final List<Integer> signalTicks = Lists.newArrayList(30, 22, 15, 10, 7, 5, 3, 2, 1);
	private boolean detonated = false;

	public CustomBlockLandmine(Location location, Player owner) {
		super(location);
		this.owner = owner;
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
		if(!triggered) {
			Player teammate = PlayerManager.getTeammate(owner);
			for(Player player : PlayerManager.getAliveOnlinePlayers()) {
				if((player != owner &&
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
						location.getWorld().createExplosion(location, 4);
						remove();
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

	@Override
	public CustomItem getRepresentingItem() {
		return CustomItems.landmine;
	}

	@EventHandler
	public void place(BlockPlaceEvent e) {
		if(PlayerManager.isPlaying(e.getPlayer()) && UHC.state.isInGame()) {
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
