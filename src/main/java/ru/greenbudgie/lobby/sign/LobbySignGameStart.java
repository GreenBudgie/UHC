package ru.greenbudgie.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.WorldManager;

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
        if(UHC.playing) {
            sign.setLine(1, ChatColor.DARK_BLUE + "Игра идет...");
        } else {
            sign.setLine(1, (WorldManager.hasMap() ? ChatColor.DARK_GREEN : ChatColor.GRAY) + "Начать игру");
            if(!WorldManager.hasMap()) sign.setLine(2, ChatColor.RED + "Мир не создан");
        }
    }

}
