package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.GameType;

import static org.bukkit.ChatColor.GRAY;

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
        var side = sign.getSide(Side.FRONT);
        side.setLine(1, GRAY + "Тип игры");
        side.setLine(2, GameType.getType().getDescription());
    }

}
