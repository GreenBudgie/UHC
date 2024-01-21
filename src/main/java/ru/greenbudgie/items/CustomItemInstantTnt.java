package ru.greenbudgie.items;

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
import ru.greenbudgie.UHC.*;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.Messages;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.item.ItemInfo;

public class CustomItemInstantTnt extends RequesterCustomItem implements Listener {

	public String getName() {
		return ChatColor.RED + "" + ChatColor.BOLD + "Instant TNT";
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
			tnt.setFuseTicks(20);
			tnt.setMetadata("owner", new FixedMetadataValue(UHCPlugin.instance, p.getName()));
			b.setType(Material.AIR);
		} else {
			p.sendMessage(Messages.CANNOT_INTERACT_WITH_ARENA_NOW);
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
		return new ItemInfo("Динамит, который взрывается через 1 сек. после установки")
				.explanation("Взрывается на 0.5 сек. быстрее мины")
				.extra("Установившему динамит игроку и его тиммейту наносится в 4 раза меньше урона от взрыва, что " +
						"отличает этот блок от мины, которая наносит всем одинаковый урон.");
	}

	@Override
	public int getRedstonePrice() {
		return 80;
	}

	@Override
	public int getLapisPrice() {
		return 16;
	}

}
