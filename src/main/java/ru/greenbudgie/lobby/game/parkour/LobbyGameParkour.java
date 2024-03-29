package ru.greenbudgie.lobby.game.parkour;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.event.BeforeGameInitializeEvent;
import ru.greenbudgie.lobby.Lobby;
import ru.greenbudgie.lobby.game.LobbyGame;
import ru.greenbudgie.lobby.game.arena.PvpArenaEnterEvent;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.WorldHelper;

import java.util.HashMap;
import java.util.Map;

public class LobbyGameParkour extends LobbyGame implements Listener {

    public static final Material PARKOUR_START_BLOCK = Material.LIGHT_WEIGHTED_PRESSURE_PLATE;
    public static final Material PARKOUR_END_BLOCK = Material.HEAVY_WEIGHTED_PRESSURE_PLATE;

    private final Map<Player, ParkourSession> parkourSessions = new HashMap<>();

    public LobbyGameParkour() {
        Bukkit.getPluginManager().registerEvents(this, UHCPlugin.instance);
    }

    @Override
    public String getConfigName() {
        return null;
    }

    @Override
    public void parseConfigOption(String option, Object value) {
    }

    @Override
    public boolean isParticipating(Player player) {
        return parkourSessions.containsKey(player);
    }

    @Override
    public void update() {
        parkourSessions.values().forEach(ParkourSession::update);
    }

    public void endAllSessions() {
        Map<Player, ParkourSession> parkourSessionsCopy = Map.copyOf(parkourSessions);
        parkourSessions.values().forEach(ParkourSession::end);
        parkourSessions.clear();
        parkourSessionsCopy.forEach(((player, parkourSession) ->
                Bukkit.getPluginManager().callEvent(new LobbyParkourLeaveEvent(player)))
        );
    }

    @EventHandler
    public void onStepOnBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Lobby.isInLobby(player)) {
            return;
        }
        if (player.isFlying()) {
            return;
        }
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (block.getType() == PARKOUR_START_BLOCK) {
            startParkourSession(player, block);
        }
        if (block.getType() == PARKOUR_END_BLOCK) {
            completeParkourSession(player, block.getLocation());
        }
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Lobby.isInLobby(player)) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        if (item.equals(ParkourItems.RESET_ITEM)) {
            restartParkourSession(event.getPlayer());
            event.setCancelled(true);
        }
        if (item.equals(ParkourItems.END_ITEM)) {
            endParkourSession(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void endSessionOnTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        ParkourSession session = parkourSessions.get(player);
        if (session == null) {
            return;
        }
        if (event.getTo() == null) {
            return;
        }
        if (WorldHelper.compareIntLocations(event.getTo(), session.getStartLocation())) {
            return;
        }
        session.end();
        parkourSessions.remove(player);
    }

    @EventHandler
    public void endSessionOnFly(PlayerToggleFlightEvent event) {
        endParkourSession(event.getPlayer());
    }

    @EventHandler
    public void endSessionOnQuit(PlayerQuitEvent event) {
        endParkourSession(event.getPlayer());
    }

    @EventHandler
    public void endSessionDeath(PlayerDeathEvent event) {
        endParkourSession(event.getEntity());
    }

    @EventHandler
    public void endSessionsOnGameStart(BeforeGameInitializeEvent event) {
        endAllSessions();
    }

    @EventHandler
    public void pluginDisable(PluginDisableEvent event) {
        endAllSessions();
    }

    @EventHandler
    public void endSessionOnArenaEnter(PvpArenaEnterEvent event) {
        endParkourSession(event.getPlayer());
    }

    private void startParkourSession(Player player, Block startBlock) {
        ParkourSession currentSession = parkourSessions.get(player);
        if (currentSession != null) {
            boolean sameLocation = WorldHelper.compareIntLocations(
                    startBlock.getLocation(),
                    currentSession.getStartLocation()
            );
            if (sameLocation) {
                return;
            }
            currentSession.end();
            unregisterParkourSession(player);
        }
        ParkourSession session = new ParkourSession(player, startBlock);
        parkourSessions.put(player, session);
        session.start();
    }

    private void completeParkourSession(Player player, Location endLocation) {
        ParkourSession session = parkourSessions.get(player);
        if (session == null) {
            return;
        }
        if (session.tryComplete(endLocation)) {
            unregisterParkourSession(player);
        }
    }

    private void endParkourSession(Player player) {
        ParkourSession session = parkourSessions.get(player);
        if (session != null) {
            session.end();
            unregisterParkourSession(player);
        }
    }

    private void restartParkourSession(Player player) {
        ParkourSession session = parkourSessions.get(player);
        if (session != null) {
            session.restart();
        }
    }

    private void unregisterParkourSession(Player player) {
        parkourSessions.remove(player);
        Bukkit.getPluginManager().callEvent(new LobbyParkourLeaveEvent(player));
    }

}
