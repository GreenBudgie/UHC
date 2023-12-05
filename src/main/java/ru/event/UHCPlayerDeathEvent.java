package ru.event;

import org.bukkit.event.HandlerList;
import ru.UHC.UHCPlayer;

import javax.annotation.Nullable;

/**
 * Called when UHC player dies, online or offline
 */
public class UHCPlayerDeathEvent extends UHCPlayerEvent {

    private final UHCPlayer killer;

    public UHCPlayerDeathEvent(UHCPlayer uhcPlayer, @Nullable UHCPlayer killer) {
        super(uhcPlayer);
        this.killer = killer;
    }

    /**
     * Gets the player killer
     * @return The player killer, or null if not present
     */
    public UHCPlayer getKiller() {
        return killer;
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
