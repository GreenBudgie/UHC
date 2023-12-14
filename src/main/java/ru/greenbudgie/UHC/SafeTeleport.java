package ru.greenbudgie.UHC;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import ru.greenbudgie.lobby.Lobby;
import ru.greenbudgie.util.TaskManager;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.RED;

/**
 * Helps to control teleportation events.
 * Some teleports (especially via commands and using spectator menu) might be dangerous.
 * So, before teleport, you can call {@link SafeTeleport#allowTeleport(Player)} for the plugin to know that this
 * teleportation is allowed, safe and checked.
 * Or you can just use {@link SafeTeleport#performSafeTeleport(Player, Location)}.
 */
public class SafeTeleport implements Listener {

    private static final String TELEPORT_TO_GAME_RESTRICTED =
            RED + "Нельзя телепортироваться к игроку через команду! Используй табличку <наблюдать>!";
    private static final String TELEPORT_TO_LOBBY_RESTRICTED =
            RED + "Чтобы телепортироваться в лобби, используй /lobby!";

    private static final Set<Player> teleportAllowed = new HashSet<>();

    /**
     * Allows player teleportation for the current tick
     */
    public static void allowTeleport(Player player) {
        teleportAllowed.add(player);
        TaskManager.invokeLater(() -> restrictTeleport(player));
    }

    public static void restrictTeleport(Player player) {
        teleportAllowed.remove(player);
    }

    public static boolean isTeleportAllowed(Player player) {
        return teleportAllowed.contains(player);
    }

    /**
     * Allows teleport and performs it
     */
    public static void performSafeTeleport(Player player, Location location) {
        allowTeleport(player);
        player.teleport(location);
    }

    @EventHandler
    public void handleTeleportation(PlayerTeleportEvent e) {
        if (!UHC.playing) {
            return;
        }
        Location from = e.getFrom();
        Location to = e.getTo();
        if (to == null) {
            return;
        }
        World fromWorld = from.getWorld();
        World toWorld = to.getWorld();
        if (fromWorld == toWorld) {
            return;
        }
        Player player = e.getPlayer();
        if (SafeTeleport.isTeleportAllowed(player)) {
            SafeTeleport.restrictTeleport(player);
            return;
        }
        boolean isFromLobby = Lobby.isInLobbyOrWatchingArena(player);
        boolean isFromGame = PlayerManager.isInGame(player);
        boolean isToLobby = toWorld == Lobby.getLobby() || ArenaManager.getArenaWorlds().contains(toWorld);
        boolean isToGameWorld = toWorld == WorldManager.getGameMap() ||
                toWorld == WorldManager.getGameMapNether() ||
                (ArenaManager.getCurrentArena() != null && toWorld == ArenaManager.getCurrentArena().getWorld());
        if (isFromLobby && isToGameWorld) {
            e.setTo(from);
            e.getPlayer().sendMessage(TELEPORT_TO_GAME_RESTRICTED);
            return;
        }
        if (isFromGame && isToLobby) {
            e.setTo(from);
            e.getPlayer().sendMessage(TELEPORT_TO_LOBBY_RESTRICTED);
        }
    }

}
