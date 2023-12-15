package ru.greenbudgie.lobby;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when the spectator leaves the game and joins lobby.
 * At this point the player should already be teleported to lobby.
 */
public class SpectatorReturnToLobbyEvent extends PlayerEvent {

    public SpectatorReturnToLobbyEvent(Player who) {
        super(who);
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
