package ru.event;

import org.bukkit.event.HandlerList;
import ru.UHC.UHCPlayer;

import javax.annotation.Nullable;

/**
 * Called when UHC player, that is currently playing, leaves the server.
 * Note: this event will not be called if the player dies on leave instantly, for that see {@link UHCPlayerDeathEvent}
 */
public class UHCPlayerLeaveEvent extends UHCPlayerEvent {

    public UHCPlayerLeaveEvent(UHCPlayer uhcPlayer) {
        super(uhcPlayer);
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
