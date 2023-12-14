package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.SpectatorManager;
import ru.greenbudgie.UHC.UHC;

import static org.bukkit.ChatColor.*;

public class LobbySignSpectate extends LobbySign {

    @Override
    public String getConfigName() {
        return "SPECTATE";
    }

    @Override
    public boolean canUseWhilePlaying() {
        return true;
    }

    @Override
    public boolean canBeUsedByAnyone() {
        return true;
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        SpectatorManager.addSpectatorFromLobby(clicker);
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        if(!UHC.playing) {
            side.setLine(1, GRAY + "<" + DARK_GRAY + BOLD + "Наблюдать" + RESET + GRAY + ">");
            side.setLine(2, RED + "Игра не идет");
            side.setLine(3, GRAY + "/watch");
        } else {
            side.setLine(1, GRAY + "<" + AQUA + BOLD + "Наблюдать" + RESET + GRAY + ">");
            side.setLine(2, GRAY + "/watch");
        }
    }

}
