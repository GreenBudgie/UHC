package ru.greenbudgie.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a new spectator is added to the game.
 * Note: this method will also be called when the player becomes spectator after his death. To distinguish, use
 * isJoinedFromLobby().
 */
public class SpectatorJoinEvent extends PlayerEvent {

    private final boolean isJoinedFromLobby;

    public SpectatorJoinEvent(Player who, boolean joinedFromLobby) {
        super(who);
        isJoinedFromLobby = joinedFromLobby;
    }

    public boolean isJoinedFromLobby() {
        return isJoinedFromLobby;
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
