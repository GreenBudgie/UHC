package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.lobby.game.LobbyGameManager;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GRAY;

public class LobbySignTeleportToArena extends LobbySign {

    @Override
    public String getConfigName() {
        return "TELEPORT_TO_ARENA";
    }

    @Override
    public boolean canBeUsedByAnyone() {
        return true;
    }

    @Override
    public boolean canUseWhilePlaying() {
        return true;
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        clicker.teleport(LobbyGameManager.PVP_ARENA.getSpawnLocation());
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(0, GRAY + "---");
        side.setLine(1, AQUA + "Телепорт");
        side.setLine(2, AQUA + "на ПВП арену");
        side.setLine(3, GRAY + "---");
    }

}
