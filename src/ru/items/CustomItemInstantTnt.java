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
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import ru.UHC.*;
import ru.main.UHCPlugin;
import ru.util.ItemInfo;
import ru.util.ParticleUtils;

public class CustomItemInstantTnt extends RequesterCustomItem implements Listener {

	public String getName() {
		return ChatColor.RED + "Instant TNT";
	}

	public Material getMaterial() {
		return Material.TNT;
	}

	@Override
	public void onPlace(Player p, Block b, ItemStack item, BlockPlaceEvent e) {
		boolean canPlace = true;
		ArenaManager.Arena arena = ArenaManager.getCurrentArena();
		if(arena != null) {
			if(!arena.getWorld().getPVP()) canPlace = false;
		}
		if(UHC.state != GameState.ENDING && (UHC.state != GameState.DEATHMATCH || canPlace)) {
			ParticleUtils.createParticlesOutline(b, Particle.REDSTONE, Color.RED, 15);
			Location center = b.getLocation().clone().add(0.5, 0, 0.5);
			b.getWorld().playSound(center, Sound.ENTITY_TNT_PRIMED, 1F, 1F);
			TNTPrimed tnt = (TNTPrimed) b.getWorld().spawnEntity(center, EntityType.PRIMED_TNT);
			tnt.setFuseTicks(30);
			tnt.setMetadata("owner", new FixedMetadataValue(UHCPlugin.instance, p.getName()));
			b.setType(Material.AIR);
		} else {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player p && e.getDamager() instanceof TNTPrimed tnt) {
			if(tnt.hasMetadata("owner")) {
				UHCPlayer owner = PlayerManager.asUHCPlayer(tnt.getMetadata("owner").get(0).asString());
				if(owner != null) {
					UHCPlayer teammate = owner.getTeammate();
					if(owner.getPlayer() == p || (teammate != null && teammate.getPlayer() == p)) {
						e.setDamage(e.getDamage() / 4.0);
					} else {
						FightHelper.setDamager(p, owner, 40, "взорвал");
					}
				}
			}
		}
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Динамит, который взрывается через полторы секунды после установки");
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
