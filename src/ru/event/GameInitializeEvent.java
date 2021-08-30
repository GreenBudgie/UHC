package ru.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when the UHC game initializes, right after all preparations are done:
 * players are set up and teleported, platform generated, e.t.c.
 */
public class GameInitializeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
