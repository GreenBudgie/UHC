package ru.event;

import org.bukkit.event.HandlerList;
import ru.UHC.UHCPlayer;

/**
 * Called when UHC player rejoins the server
 * Note: this event will not be called if the player is already dead,
 * for that use the default {@link org.bukkit.event.player.PlayerJoinEvent}
 */
public class UHCPlayerRejoinEvent extends UHCPlayerEvent {

    public UHCPlayerRejoinEvent(UHCPlayer uhcPlayer) {
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
