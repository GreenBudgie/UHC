package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.WorldManager;

import static org.bukkit.ChatColor.*;

public class LobbySignGameStart extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_START";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        if(!UHC.playing) {
            UHC.startGame();
        }
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        if(UHC.playing) {
            side.setLine(1, RED + "" + BOLD + "Игра идет");
            return;
        }
        var startGameColor = WorldManager.hasMap() ? GREEN : DARK_GRAY;
        side.setLine(
                1,
                GRAY + "| " + startGameColor + BOLD + "Начать игру" + RESET + GRAY + " |"
        );
        if(!WorldManager.hasMap()) {
            side.setLine(2, RED + "Мир не создан");
        }

    }

}
