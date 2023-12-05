package ru.greenbudgie.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.lobby.Lobby;

public class LobbySignGameDuo extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_DUO";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        UHC.isDuo = !UHC.isDuo;
        Lobby.getPlayersInLobbyAndArenas().forEach(UHC::updateLobbyScoreboard);
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(1, ChatColor.DARK_BLUE + "Режим:");
        if(!UHC.isDuo) {
            sign.setLine(2, ChatColor.DARK_AQUA + "Соло");
        } else {
            sign.setLine(2, ChatColor.DARK_PURPLE + "Дуо");
        }
    }

}
