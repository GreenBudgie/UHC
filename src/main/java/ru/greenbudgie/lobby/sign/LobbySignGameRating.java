package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;

import static org.bukkit.ChatColor.*;

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
        var side = sign.getSide(Side.FRONT);
        side.setLine(1, GRAY + "Рейтинг:");
        side.setLine(2, UHC.isRatingGame ?
                (GREEN + "" + BOLD + "Включен") :
                (RED + "" + BOLD + "Отключен"));
    }

}
