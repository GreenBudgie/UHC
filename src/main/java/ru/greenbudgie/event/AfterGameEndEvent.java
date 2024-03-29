package ru.greenbudgie.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called after the UHC game ends, for whatever reason.
 * At this moment players are already in the lobby.
 */
public class AfterGameEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
