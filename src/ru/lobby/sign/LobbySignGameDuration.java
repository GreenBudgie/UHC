package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.UHC.UHC;
import ru.UHC.WorldManager;

public class LobbySignGameDuration extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_DURATION";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        if(UHC.gameDuration >= 2) UHC.gameDuration = 0;
        else UHC.gameDuration++;
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(0, ChatColor.DARK_BLUE + "Длит. игры:");

        sign.setLine(1, (UHC.gameDuration == 0 ?
                (ChatColor.DARK_GREEN + "Короткая") :
                (UHC.gameDuration == 1 ?
                        (ChatColor.DARK_AQUA + "Обычная") :
                        (ChatColor.DARK_RED + "Долгая"))) +
                ChatColor.DARK_BLUE + ", " + (UHC.getNoPVPDuration() + UHC.getGameDuration()) + "мин");

        sign.setLine(2, ChatColor.DARK_AQUA + String.valueOf(UHC.getNoPVPDuration()) +
                ChatColor.DARK_BLUE + " минут без пвп");

        sign.setLine(3, ChatColor.DARK_AQUA + String.valueOf(UHC.getGameDuration()) +
                ChatColor.DARK_BLUE + " минут до ДМ");
    }

}
