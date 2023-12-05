package ru.greenbudgie.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when the UHC game ends, for whatever reason
 */
public class GameEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
