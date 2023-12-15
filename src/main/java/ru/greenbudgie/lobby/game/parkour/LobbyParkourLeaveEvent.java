package ru.greenbudgie.lobby.game.parkour;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a player in lobby leaves the parkour in lobby
 */
public class LobbyParkourLeaveEvent extends PlayerEvent {

    public LobbyParkourLeaveEvent(Player who) {
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
