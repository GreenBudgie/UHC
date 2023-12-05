package ru.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.UHC.GameState;

/**
 * Called when the UHC game starts, right after {@link GameState#PREPARING}
 */
public class GameStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
