package ru.greenbudgie.lobby.game.arena;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a player in lobby leaves the PVP arena
 */
public class PvpArenaLeaveEvent extends PlayerEvent {

    public PvpArenaLeaveEvent(Player who) {
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
