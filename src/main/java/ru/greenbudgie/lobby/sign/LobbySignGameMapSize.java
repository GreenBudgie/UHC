package ru.greenbudgie.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;

public class LobbySignGameMapSize extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_MAP_SIZE";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        if(UHC.mapSize >= 3) UHC.mapSize = 0;
        else UHC.mapSize++;
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(1, ChatColor.DARK_BLUE + "Размер карты:");
        sign.setLine(2, UHC.mapSize == 0 ?
                (ChatColor.DARK_GREEN + "Маленький") :
                (UHC.mapSize == 1 ?
                        (ChatColor.DARK_AQUA + "Обычный") :
                        (UHC.mapSize == 2 ? (ChatColor.DARK_RED + "Большой") : (ChatColor.LIGHT_PURPLE + "Фиксированный"))));
        if(UHC.mapSize != 3) {
            sign.setLine(3, ChatColor.DARK_AQUA + String.valueOf(UHC.getMapSize()) + ChatColor.GOLD + " бл. на игрока");
        } else {
            sign.setLine(3, ChatColor.DARK_AQUA + String.valueOf(UHC.getMapSize()) + ChatColor.GOLD + " бл.");
        }
    }

}
