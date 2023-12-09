package ru.greenbudgie.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Utility class that holds commonly used characters, formatted strings and methods to build messages for location
 */
public class LocationFormatter {

    private static final char[] ARROWS = new char[] {'↑', '⬈', '→', '⬊', '↓', '⬋', '←', '⬉', '↑'};

    /**
     * Produces a message like: 101, 99, -233 (15) or 101, 99, -233 (в Аду)
     */
    public static String formatToWithDistanceAndArrow(
            Location from,
            Location to,
            ChatColor coordinateColor,
            ChatColor commaColor,
            ChatColor distanceColor,
            ChatColor bracesColor,
            ChatColor arrowColor,
            boolean formatDifferentWorld
    ) {
        boolean differentWorlds = from.getWorld() != to.getWorld();
        if (to.getWorld() == null || (!formatDifferentWorld && differentWorlds)) {
            return format(to, coordinateColor, commaColor);
        }
        if (differentWorlds) {
            formatToWithDistance(from, to, coordinateColor, commaColor, distanceColor, bracesColor, true);
        }
        return formatToWithDistance(from, to, coordinateColor, commaColor, distanceColor, bracesColor, formatDifferentWorld) +
                arrowColor + " " + LocationFormatter.getArrowPointingTo(from, to);
    }

    /**
     * Produces a message like: 101, 99, -233 (15) or 101, 99, -233 (в Аду)
     */
    public static String formatToWithDistance(
            Location from,
            Location to,
            ChatColor coordinateColor,
            ChatColor commaColor,
            ChatColor distanceColor,
            ChatColor bracesColor,
            boolean formatDifferentWorld
    ) {
        boolean differentWorlds = from.getWorld() != to.getWorld();
        if (to.getWorld() == null || (!formatDifferentWorld && differentWorlds)) {
            return format(to, coordinateColor, commaColor);
        }
        String distanceInfo = differentWorlds ?
                (WorldHelper.getEnvironmentNamePrepositional(to.getWorld().getEnvironment(), commaColor)) :
                String.valueOf(((int) from.distance(to)));
        String fullDistanceInfo = bracesColor + " (" + distanceColor + distanceInfo + bracesColor + ")";
        return format(to, coordinateColor, commaColor) + fullDistanceInfo;
    }

    /**
     * Produces a message like: 101, 99, -233
     */
    public static String format(Location location, ChatColor coordinateColor, ChatColor commaColor) {
        String comma = commaColor + ", ";
        return coordinateColor + "" + location.getBlockX() + comma +
                coordinateColor + location.getBlockY() + comma +
                coordinateColor + location.getBlockZ();
    }

    /**
     * Produces an arrow char that is pointing to the second location from the first location
     */
    public static char getArrowPointingTo(Location from, Location to) {
        double x1 = from.getX();
        double z1 = from.getZ();
        double x2 = to.getX();
        double z2 = to.getZ();
        double fromAngle = from.getYaw();
        fromAngle = fromAngle % 360.0D;
        double toAngle = Math.atan2(z2 - z1, x2 - x1);
        double finalAngle = (Math.PI - (Math.toRadians(fromAngle - 90.0D) - toAngle)) % (Math.PI * 2);
        if(finalAngle < 0) finalAngle = 2 * Math.PI + finalAngle;
        double step = Math.PI / 4;
        double range = Math.PI / 8;
        char arrow = ' ';
        for(int i = 0; i < ARROWS.length; i++) {
            double currentAngle = i * step;
            if(inRange(finalAngle, currentAngle - range, currentAngle + range)) {
                arrow = ARROWS[i];
            }
        }
        return arrow;
    }

    private static boolean inRange(double num, double min, double max) {
        return num >= min && num <= max;
    }

}
