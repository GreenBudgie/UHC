package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.UHC.GameType;
import ru.UHC.UHC;

public class LobbySignGameType extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_TYPE";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        GameType.switchType();
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(1, ChatColor.DARK_BLUE + "Тип игры:");
        sign.setLine(2, GameType.getType().getDescription());
    }

}
