package ru.items;

import com.google.common.collect.Sets;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.mutator.MutatorManager;
import ru.util.WorldHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomItemTerraDrill extends RequesterCustomItem implements Listener {

	private static Map<Player, BlockFace> lastFace = new HashMap<>();
	private static final Set<Material> rock = Sets.newHashSet(
			Material.STONE,
			Material.ANDESITE,
			Material.DIORITE,
			Material.GRANITE,
			Material.TUFF,
			Material.DEEPSLATE,
			Material.NETHERRACK,
			Material.BLACKSTONE,
			Material.BASALT);

	public String getName() {
		return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Terra Drill";
	}

	public Material getMaterial() {
		return Material.IRON_PICKAXE;
	}

	@Override
	public void onBreak(Player p, ItemStack item, BlockBreakEvent e) {
		Block b = e.getBlock();
		if(rock.contains(b.getType()) && !e.isCancelled()) {
			BlockFace face = lastFace.getOrDefault(p, BlockFace.EAST);
			Location l = b.getLocation();
			Set<Block> toBreak = new HashSet<>();
			if(face == BlockFace.DOWN || face == BlockFace.UP) {
				toBreak.add(l.clone().add(1, 0, 0).getBlock());
				toBreak.add(l.clone().add(1, 0, 1).getBlock());
				toBreak.add(l.clone().add(1, 0, -1).getBlock());
				toBreak.add(l.clone().add(-1, 0, 0).getBlock());
				toBreak.add(l.clone().add(-1, 0, 1).getBlock());
				toBreak.add(l.clone().add(-1, 0, -1).getBlock());
				toBreak.add(l.clone().add(0, 0, 1).getBlock());
				toBreak.add(l.clone().add(0, 0, -1).getBlock());
			}
			if(face == BlockFace.EAST || face == BlockFace.WEST) {
				toBreak.add(l.clone().add(0, 1, 0).getBlock());
				toBreak.add(l.clone().add(0, 1, 1).getBlock());
				toBreak.add(l.clone().add(0, 1, -1).getBlock());
				toBreak.add(l.clone().add(0, -1, 0).getBlock());
				toBreak.add(l.clone().add(0, -1, 1).getBlock());
				toBreak.add(l.clone().add(0, -1, -1).getBlock());
				toBreak.add(l.clone().add(0, 0, 1).getBlock());
				toBreak.add(l.clone().add(0, 0, -1).getBlock());
			}
			if(face == BlockFace.NORTH || face == BlockFace.SOUTH) {
				toBreak.add(l.clone().add(1, 0, 0).getBlock());
				toBreak.add(l.clone().add(1, 1, 0).getBlock());
				toBreak.add(l.clone().add(1, -1, 0).getBlock());
				toBreak.add(l.clone().add(-1, 0, 0).getBlock());
				toBreak.add(l.clone().add(-1, 1, 0).getBlock());
				toBreak.add(l.clone().add(-1, -1, 0).getBlock());
				toBreak.add(l.clone().add(0, 1, 0).getBlock());
				toBreak.add(l.clone().add(0, -1, 0).getBlock());
			}
			for(Block block : toBreak) {
				if(block.getType() == Material.STONE && MutatorManager.strongStone.isActive()) continue;
				if(rock.contains(block.getType())) {
					block.breakNaturally(item);
				}
			}
		}
	}

	@Override
	public void onUseLeftBlock(Player p, ItemStack item, Block b, PlayerInteractEvent e) {
		lastFace.put(p, e.getBlockFace());
	}

	@Override
	public String getDescription() {
		return "Копает камень 3x3. Нельзя зачарить и починить.";
	}

	@Override
	public int getRedstonePrice() {
		return 176;
	}

	@Override
	public int getLapisPrice() {
		return 32;
	}

}
