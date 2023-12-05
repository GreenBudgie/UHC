package ru.greenbudgie.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;

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
        sign.setLine(1, ChatColor.DARK_BLUE + "Рейтинг:");
        sign.setLine(2, UHC.isRatingGame ?
                (ChatColor.DARK_GREEN + "Включен") :
                (ChatColor.DARK_RED + "Отключен"));
    }

}
