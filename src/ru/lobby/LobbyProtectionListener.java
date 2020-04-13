package ru.lobby;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import ru.UHC.PlayerHandler;
import ru.pvparena.PvpArena;

/**
 * Contains lots of event listeners to prevent lobby griefing
 */
public class LobbyProtectionListener implements Listener {

	private boolean isInLobby(Player player) {
		return PlayerHandler.isInLobby(player);
	}

	@EventHandler
	public void damage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(isInLobby(p) && !PvpArena.isOnArena(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void noMinecartCollide(VehicleEntityCollisionEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(p.getGameMode() == GameMode.ADVENTURE && isInLobby(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void noMinecartDamage(VehicleDamageEvent e) {
		if(e.getAttacker() instanceof Player) {
			Player p = (Player) e.getAttacker();
			if(p.getGameMode() == GameMode.ADVENTURE && isInLobby(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void noItemFrameInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Entity ent = e.getRightClicked();
		if(ent instanceof ItemFrame && p.getGameMode() == GameMode.ADVENTURE && isInLobby(p)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void noLobbyGrief(HangingBreakByEntityEvent e) {
		if(e.getRemover() instanceof Player) {
			Player p = (Player) e.getRemover();
			if(p.getGameMode() == GameMode.ADVENTURE && isInLobby(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void noFrameItemBreak(EntityDamageByEntityEvent e) {
		if(e.getEntityType() == EntityType.ITEM_FRAME && e.getDamager() instanceof Player && isInLobby((Player) e.getDamager())
				&& ((Player) e.getDamager()).getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void noFoodLoss(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		if(isInLobby(p) && e.getFoodLevel() < 20) {
			p.setFoodLevel(20);
			e.setCancelled(true);
		}
	}

}
