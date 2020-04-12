package ru.mutator;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import ru.items.CustomItems;
import ru.main.UHCPlugin;
import ru.util.MathUtils;

public class MutatorInvalidDrop extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.COAL;
	}

	@Override
	public String getName() {
		return "Неадекватный Дроп";
	}

	@Override
	public String getDescription() {
		return "С руды падает еда, с мобов - руды. Чем сильнее моб - тем лучше руда с него упадет. Например, эндермены и мелкие зомби считаются самыми опасными. Чем реже руда - тем сытнее еда.";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.noDiamonds;
	}

	@EventHandler
	public void dig(BlockBreakEvent e) {
		Block b = e.getBlock();
		if(b.getType() == Material.DIAMOND_ORE) {
			e.setDropItems(false);
			b.getWorld().dropItemNaturally(b.getLocation(),
					new ItemStack(MathUtils.choose(Material.BEEF, Material.PORKCHOP, Material.GOLDEN_CARROT), MathUtils.randomRange(1, 3)));
		}
		if(b.getType() == Material.GOLD_ORE || b.getType() == Material.LAPIS_ORE) {
			e.setDropItems(false);
			b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(MathUtils.choose(Material.MUTTON, Material.SALMON, Material.CHICKEN), MathUtils.randomRange(1, 3)));
		}
		if(b.getType() == Material.REDSTONE_ORE) {
			e.setDropItems(false);
			b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(MathUtils.choose(Material.POTATO, Material.BREAD, Material.RABBIT), MathUtils.randomRange(1, 3)));
		}
		if(b.getType() == Material.IRON_ORE) {
			e.setDropItems(false);
			b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(MathUtils.choose(Material.SWEET_BERRIES, Material.COOKIE), MathUtils.randomRange(1, 3)));
		}
		if(b.getType() == Material.COAL_ORE) {
			e.setDropItems(false);
			b.getWorld().dropItemNaturally(b.getLocation(),
					new ItemStack(MathUtils.choose(Material.DRIED_KELP, Material.BEETROOT, Material.TROPICAL_FISH), MathUtils.randomRange(1, 3)));
		}
	}

	@EventHandler
	public void entityDrop(EntityDeathEvent e) {
		Entity ent = e.getEntity();
		if(ent instanceof Player) return;
		int tier = 1; //1-3
		if(ent instanceof Monster || ent instanceof Shulker || ent instanceof Ghast || ent instanceof Phantom || ent instanceof Boss) {
			tier = 2;
		}
		if(ent instanceof Enderman || ent instanceof Slime) {
			tier = 3;
		}
		if(ent instanceof Zombie) {
			if(((Zombie) ent).isBaby()) {
				tier = 3;
			}
		}
		ItemStack drop = null;
		switch(tier) {
		case 1:
			if(MathUtils.chance(25)) {
				drop = new ItemStack(Material.IRON_INGOT);
			} else {
				drop = new ItemStack(Material.COAL);
			}
			break;
		case 2:
			if(MathUtils.chance(8)) {
				drop = new ItemStack(Material.DIAMOND);
			} else {
				if(MathUtils.chance(30)) {
					if(MathUtils.chance(70)) {
						drop = new ItemStack(Material.REDSTONE, MathUtils.randomRange(4, 8));
					} else {
						drop = new ItemStack(Material.LAPIS_LAZULI, MathUtils.randomRange(6, 10));
					}
				} else {
					if(MathUtils.chance(25)) {
						drop = new ItemStack(Material.GOLD_INGOT);
					} else {
						drop = new ItemStack(Material.IRON_INGOT);
					}
				}
			}
			break;
		case 3:
			if(MathUtils.chance(33)) {
				drop = new ItemStack(Material.DIAMOND);
			} else {
				if(MathUtils.chance(30)) {
					if(MathUtils.chance(40)) {
						drop = new ItemStack(Material.REDSTONE, MathUtils.randomRange(5, 9));
					} else {
						drop = new ItemStack(Material.LAPIS_LAZULI, MathUtils.randomRange(7, 11));
					}
				} else {
					if(MathUtils.chance(50)) {
						drop = new ItemStack(Material.GOLD_INGOT);
					} else {
						drop = new ItemStack(Material.IRON_INGOT);
					}
				}
			}
			break;
		}
		e.getDrops().removeIf(item -> !CustomItems.darkArtifact.isEquals(item));
		ent.getWorld().dropItemNaturally(ent.getLocation(), drop);
	}

}
