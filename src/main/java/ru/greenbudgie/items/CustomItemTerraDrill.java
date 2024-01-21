package ru.greenbudgie.items;

import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.util.Region;
import ru.greenbudgie.util.item.ItemInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomItemTerraDrill extends RequesterCustomItem implements Listener {

	private static final Map<Player, BlockFace> lastFace = new HashMap<>();
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
			Location location = b.getLocation();
			Region region = new Region(
					location.clone().add(
							face.getModX() == 0 ? -1 : 0,
							face.getModY() == 0 ? -1 : 0,
							face.getModZ() == 0 ? -1 : 0
					),
					location.clone().add(
							face.getModX() == 0 ? 1 : 0,
							face.getModY() == 0 ? 1 : 0,
							face.getModZ() == 0 ? 1 : 0
					)
			);
			Set<Block> toBreak = region.getBlocksInside();
			toBreak.remove(b);
			for(Block block : toBreak) {
				if(rock.contains(block.getType())) {
					block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
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
	public ItemInfo getDescription() {
		return new ItemInfo("Копает камень 3x3")
				.extra("К камню также относится адский камень, базальт, андезит, диорит и т.д.")
				.note("Нельзя зачарить и починить");
	}

	@Override
	public int getRedstonePrice() {
		return 64;
	}

	@Override
	public int getLapisPrice() {
		return 8;
	}

}
