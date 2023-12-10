package ru.greenbudgie.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called before any UHC game preparations, right after the sign click or /start command
 */
public class BeforeGameInitializeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
