package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import ru.UHC.UHC;
import ru.util.WorldHelper;

public class MutatorFlameFist extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.FIRE_CHARGE;
	}

	@Override
	public String getName() {
		return "Огненный Кулак";
	}

	@Override
	public String getDescription() {
		return "Любое живое существо (включая игроков), по которому был нанесен удар, поджигается на 3 секунды. Вскопанные руды переплавляются автоматически";
	}

	@EventHandler
	public void dig(BlockBreakEvent e) {
		Block b = e.getBlock();
		Material instrument = e.getPlayer().getInventory().getItemInMainHand().getType();
		if(b.getType() == Material.GOLD_ORE) {
			if(instrument == Material.DIAMOND_PICKAXE || instrument == Material.IRON_PICKAXE) {
				e.setDropItems(false);
				WorldHelper.spawnParticlesInside(b, Particle.FLAME, null, 8);
				b.getWorld().playSound(b.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5F, 1F);
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT));
			}
		}
		if(b.getType() == Material.IRON_ORE) {
			if(instrument == Material.DIAMOND_PICKAXE || instrument == Material.IRON_PICKAXE || instrument == Material.STONE_PICKAXE) {
				e.setDropItems(false);
				WorldHelper.spawnParticlesInside(b, Particle.FLAME, null, 8);
				b.getWorld().playSound(b.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5F, 1F);
				b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_INGOT));
			}
		}
	}

	@EventHandler
	public void attack(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			if(e.getEntity() instanceof Player) {
				Player p1 = (Player) e.getDamager();
				Player p2 = (Player) e.getEntity();
				if(UHC.isTeammates(p1, p2)) return;
			}
			e.getEntity().setFireTicks(60);
		}
	}

}
