package ru.greenbudgie.lobby;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.EquipmentSlot;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.util.TaskManager;

public class LobbyListener implements Listener {

    public static boolean isInLobbyOrArena(Player player) {
        return Lobby.isInLobbyOrWatchingArena(player);
    }

    @EventHandler
    public void noDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player player) {
            if(Lobby.isInLobby(player) && !LobbyGameManager.PVP_ARENA.isOnArena(player)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void noMinecartCollide(VehicleEntityCollisionEvent e) {
        if(e.getEntity() instanceof Player p) {
            if(p.getGameMode() == GameMode.ADVENTURE && isInLobbyOrArena(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void noMinecartDamage(VehicleDamageEvent e) {
        if(e.getAttacker() instanceof Player p) {
            if(p.getGameMode() == GameMode.ADVENTURE && isInLobbyOrArena(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void noItemFrameInteract(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        Entity ent = e.getRightClicked();
        if(ent instanceof ItemFrame && p.getGameMode() == GameMode.ADVENTURE && isInLobbyOrArena(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void noGrief(HangingBreakByEntityEvent e) {
        if(e.getRemover() instanceof Player p) {
            if(p.getGameMode() == GameMode.ADVENTURE && isInLobbyOrArena(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void noFrameItemBreak(EntityDamageByEntityEvent e) {
        if(e.getEntityType() == EntityType.ITEM_FRAME && e.getDamager() instanceof Player && isInLobbyOrArena((Player) e.getDamager())
                && ((Player) e.getDamager()).getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void allowMusicDisks(PlayerInteractEvent e) {
        if(isInLobbyOrArena(e.getPlayer()) && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.JUKEBOX
                && e.getHand() == EquipmentSlot.HAND && e.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            Jukebox jukebox = (Jukebox) e.getClickedBlock().getState();
            if(!jukebox.isPlaying() && jukebox.getRecord().getType() == Material.AIR && e.getItem() != null && e.getItem().getType().isRecord()) {
                e.setCancelled(true);
                e.setUseInteractedBlock(Event.Result.DENY);
                e.setUseItemInHand(Event.Result.DENY);
                jukebox.setRecord(e.getItem().clone());
                e.getItem().setAmount(e.getItem().getAmount() - 1);
                TaskManager.invokeLater(jukebox::update);
            }
        }
    }

    @EventHandler
    public void noDrop(PlayerDropItemEvent e) {
        if(isInLobbyOrArena(e.getPlayer()) && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void noPlace(BlockPlaceEvent e) {
        if(isInLobbyOrArena(e.getPlayer()) && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void noFoodLoss(FoodLevelChangeEvent e) {
        Player p = (Player) e.getEntity();
        if(isInLobbyOrArena(p) && e.getFoodLevel() < 20) {
            p.setFoodLevel(20);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void noEntityDamage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player player && player.getGameMode() == GameMode.ADVENTURE &&
                isInLobbyOrArena(player) && !(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void noLobbyArenasInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(Lobby.isWatchingArena(player) && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void lobbyDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UHC.heal(player);
        event.getDrops().clear();
        event.setKeepInventory(true);
        if(isInLobbyOrArena(player) && !LobbyGameManager.PVP_ARENA.isOnArena(player)) {
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }

}
