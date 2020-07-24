package ru.items;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import ru.UHC.FightHelper;
import ru.UHC.GameState;
import ru.UHC.UHC;
import ru.main.UHCPlugin;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

public class CustomItemInstantTnt extends RequesterCustomItem implements Listener {

	public String getName() {
		return ChatColor.RED + "Instant TNT";
	}

	public Material getMaterial() {
		return Material.TNT;
	}

	@Override
	public void onPlace(Player p, Block b, ItemStack item, BlockPlaceEvent e) {
		if(UHC.state != GameState.ENDING && (UHC.state != GameState.DEATHMATCH || UHC.arenaPvpTimer <= 0)) {
			ParticleUtils.createParticlesOutline(b, Particle.REDSTONE, Color.RED, 15);
			Location center = b.getLocation().clone().add(0.5, 0, 0.5);
			b.getWorld().playSound(center, Sound.ENTITY_TNT_PRIMED, 1F, 1F);
			TNTPrimed tnt = (TNTPrimed) b.getWorld().spawnEntity(center, EntityType.PRIMED_TNT);
			tnt.setFuseTicks(30);
			tnt.setMetadata("owner", new FixedMetadataValue(UHCPlugin.instance, p));
			b.setType(Material.AIR);
		} else {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof TNTPrimed) {
			Player p = (Player) e.getEntity();
			TNTPrimed tnt = (TNTPrimed) e.getDamager();
			if(tnt.hasMetadata("owner")) {
				Player owner = (Player) tnt.getMetadata("owner").get(0).value();
				if(owner != null) {
					Player teammate = UHC.getTeammate(owner);
					if(owner == p || (teammate != null && teammate == p)) {
						e.setDamage(e.getDamage() / 4.0);
					} else {
						FightHelper.setDamager(p, owner, 40, "взорвал");
					}
				}
			}
		}
	}

	@Override
	public String getDescription() {
		return "Динамит, который взрывается через полторы секунды после установки";
	}

	@Override
	public int getRedstonePrice() {
		return 96;
	}

	@Override
	public int getLapisPrice() {
		return 12;
	}

}
