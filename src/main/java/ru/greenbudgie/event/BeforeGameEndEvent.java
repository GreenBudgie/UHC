package ru.greenbudgie.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called before the UHC game ends, for whatever reason.
 * At this moment players are still in the game world, but {@link ru.greenbudgie.UHC.UHC#playing} is already false.
 */
public class BeforeGameEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
