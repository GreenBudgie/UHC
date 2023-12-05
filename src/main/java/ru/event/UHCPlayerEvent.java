package ru.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.UHC.UHCPlayer;

/**
 * A base class for any player-related events
 */
public abstract class UHCPlayerEvent extends Event {

    private final UHCPlayer uhcPlayer;

    public UHCPlayerEvent(UHCPlayer uhcPlayer) {
        this.uhcPlayer = uhcPlayer;
    }

    public UHCPlayer getUHCPlayer() {
        return uhcPlayer;
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
