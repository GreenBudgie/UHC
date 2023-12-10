package ru.greenbudgie.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called after the UHC game is initialized, right after all preparations are done:
 * players are set up and teleported, platform generated, game state changed to VOTE e.t.c.
 */
public class AfterGameInitializeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
