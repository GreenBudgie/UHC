package ru.greenbudgie.util;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Helps to handle EntityPotionEffectEvent, e.g. prevent it from infinite cycles.
 */
public class EffectProcess {

    private static final Set<Map.Entry<Player, Listener>> processes = new HashSet<>();

    /**
     * Tells the specified listener to ignore any EntityPotionEffectEvent at the current tick
     * that contains the specified player
     */
    public static void ignoreCurrentTick(Player player, Listener listener) {
        processes.add(Maps.immutableEntry(player, listener));
        TaskManager.invokeLater(processes::clear);
    }

    /**
     * Returns whether the specified listener must ignore EntityPotionEffectEvent at the current tick
     * for specified player
     */
    public static boolean doIgnore(Player player, Listener listener) {
        for(Map.Entry<Player, Listener> entries : processes) {
            if(entries.getKey() == player && entries.getValue() == listener) return true;
        }
        return false;
    }

}
