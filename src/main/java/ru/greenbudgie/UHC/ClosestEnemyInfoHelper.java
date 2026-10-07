package ru.greenbudgie.UHC;

import org.bukkit.entity.Player;
import ru.greenbudgie.util.LocationFormatter;

import static org.bukkit.ChatColor.*;

public class ClosestEnemyInfoHelper {

    public static void showClosestEnemyInfo(Player player) {
        ActionBarManager.queueMessage(player, 1, () -> getNearestPlayerMessage(player));
    }

    public static String getNearestPlayerMessage(Player player) {
        UHCPlayer closestPlayer = null;
        double minDistanceSquared = -1;
        var teammate = PlayerManager.getUHCTeammate(player);
        for (UHCPlayer otherPlayer : PlayerManager.getAlivePlayers()) {
            if (player == otherPlayer.getPlayer() || otherPlayer == teammate) {
                continue;
            }

            if (otherPlayer.getLocation().getWorld() != player.getWorld()) {
                continue;
            }

            var distanceSquared = player.getLocation().distanceSquared(otherPlayer.getLocation());
            if (closestPlayer == null || distanceSquared < minDistanceSquared) {
                closestPlayer = otherPlayer;
                minDistanceSquared = distanceSquared;
            }
        }

        if (closestPlayer == null) {
            return GOLD + "Ближайший игрок не найден";
        }

        var closestPlayerLocation = closestPlayer.getLocation();
        if (closestPlayerLocation == null) {
            return GOLD + "Ближайший игрок не найден";
        }

        String separator = GRAY + " | ";
        String locationInfo = LocationFormatter.formatToWithDistance(
                player.getLocation(),
                closestPlayerLocation,
                DARK_AQUA,
                GRAY,
                AQUA,
                DARK_GRAY,
                false
        );
        String info = GOLD + "Ближайший игрок: " + RED + closestPlayer.getNickname()
                + RESET + separator +
                locationInfo + AQUA + " " +
                LocationFormatter.getArrowPointingTo(player.getLocation(), closestPlayerLocation);

        return info;
    }

}
