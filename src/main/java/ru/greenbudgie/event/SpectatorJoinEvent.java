package ru.greenbudgie.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a new spectator enters the game
 * Note: this method will also be called when the player becomes spectator after his death
 */
public class SpectatorJoinEvent extends PlayerEvent {

    public SpectatorJoinEvent(Player who) {
        super(who);
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
