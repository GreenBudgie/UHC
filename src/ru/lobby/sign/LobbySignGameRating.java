package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.UHC.UHC;
import ru.UHC.WorldManager;

public class LobbySignGameRating extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_RATING";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        UHC.isRatingGame = !UHC.isRatingGame;
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(1, ChatColor.DARK_BLUE + "Тип игры:");
        sign.setLine(2, UHC.isRatingGame ?
                (ChatColor.DARK_GREEN + "Рейтинговая") :
                (ChatColor.DARK_RED + "Тестовая"));
    }

}
