package ru.lobby;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import ru.UHC.PlayerOptions;
import ru.UHC.UHC;
import ru.UHC.WorldManager;
import ru.main.UHCPlugin;
import ru.util.TaskManager;

public class LobbyListener implements Listener {

    public static boolean isInLobby(Player player) {
        return Lobby.isInLobby(player);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void lobbyInvClick(InventoryClickEvent e) {
        Player clickedPlayer = (Player) e.getWhoClicked();
        InventoryView view = e.getView();
        Inventory inv = e.getInventory();
        int slot = e.getRawSlot();
        ItemStack item = e.getCurrentItem() == null ? new ItemStack(Material.AIR) : e.getCurrentItem();
        if(view.getTitle().equals(PlayerOptions.invName) && e.getClickedInventory() == view.getTopInventory()) {
            PlayerOptions option = PlayerOptions.values()[slot];
            if(option != null) {
                option.setActive(clickedPlayer, !option.isActive(clickedPlayer));
                PlayerOptions.openInventory(clickedPlayer);
            }
        }
    }

    @EventHandler
    public void noDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player p) {
            if(isInLobby(p) && !LobbyGameManager.PVP_ARENA.isOnArena(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void noMinecartCollide(VehicleEntityCollisionEvent e) {
        if(e.getEntity() instanceof Player p) {
            if(p.getGameMode() == GameMode.ADVENTURE && isInLobby(p)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void noMinecartDamage(VehicleDamageEvent e) {
        if(e.getAttacker() instanceof Player p) {
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
    public void noGrief(HangingBreakByEntityEvent e) {
        if(e.getRemover() instanceof Player p) {
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
    public void allowMusicDisks(PlayerInteractEvent e) {
        if(isInLobby(e.getPlayer()) && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.JUKEBOX
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
        if(isInLobby(e.getPlayer()) && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void noPlace(BlockPlaceEvent e) {
        if(isInLobby(e.getPlayer()) && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void lectern(PlayerInteractEvent e) {
        if(isInLobby(e.getPlayer()) && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            Block block = e.getClickedBlock();
            if(block.getType() == Material.LECTERN) {
                if(!e.getPlayer().getInventory().contains(Material.WRITTEN_BOOK)) {
                    Lectern lectern = (Lectern) block.getState();
                    ItemStack book = lectern.getInventory().getItem(0);
                    if(book != null) {
                        player.getInventory().addItem(book);
                    }
                }
                e.setCancelled(true);
            }
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

    @EventHandler
    public void noEntityDamage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player player && player.getGameMode() == GameMode.ADVENTURE &&
                isInLobby(player) && !(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if(isInLobby(p)) {
            if(e.getTo().getY() <= 0) {
                p.teleport(WorldManager.getLobby().getSpawnLocation());
            }
        }
    }

}
