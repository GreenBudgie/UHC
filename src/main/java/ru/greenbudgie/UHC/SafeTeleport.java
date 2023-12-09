package ru.greenbudgie.UHC;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class SafeTeleport {

    private static final Set<Player> teleportAllowed = new HashSet<>();

    public static void allowTeleport(Player player) {
        teleportAllowed.add(player);
    }

    public static void restrictTeleport(Player player) {
        teleportAllowed.remove(player);
    }

    public static boolean isTeleportAllowed(Player player) {
        return teleportAllowed.contains(player);
    }

}
