package ru.greenbudgie.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when the spectator leaves the game, either quits or returns to lobby.
 * At this point the player should already be teleported to lobby.
 */
public class SpectatorLeaveEvent extends PlayerEvent {

    public SpectatorLeaveEvent(Player who) {
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
