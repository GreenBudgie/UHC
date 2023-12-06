package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;

import static org.bukkit.ChatColor.*;

public class LobbySignGameMapSize extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_MAP_SIZE";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        UHC.mapSize = UHC.mapSize.nextValue();
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(0, GRAY + "Размер карты");

        String sizeInfo = switch (UHC.mapSize) {
            case SMALL -> GREEN + "" + BOLD + "Маленький";
            case DEFAULT -> AQUA + "" + BOLD + "Обычный";
            case BIG -> RED + "" + BOLD + "Большой";
            case FIXED -> LIGHT_PURPLE + "" + BOLD + "Постоянный";
        };

        side.setLine(1, sizeInfo);
        if (UHC.mapSize.isFixedSize()) {
            side.setLine(2, AQUA + "" + BOLD + UHC.getMapSize() + GRAY + " бл.");
        } else {
            side.setLine(2, AQUA + "" + BOLD + UHC.getMapSize() + GRAY + " бл. на игрока");
        }
    }

}
