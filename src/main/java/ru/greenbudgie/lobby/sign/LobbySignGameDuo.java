package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.lobby.Lobby;
import ru.greenbudgie.lobby.LobbyTeamBuilder;

import static org.bukkit.ChatColor.*;

public class LobbySignGameDuo extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_DUO";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        UHC.isDuo = !UHC.isDuo;
        LobbyTeamBuilder.giveOrRemoveTeammateSelectItems();
        Lobby.getPlayersInLobbyAndArenas().forEach(UHC::updateLobbyScoreboard);
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(1, GRAY + "Режим");
        if(!UHC.isDuo) {
            side.setLine(2, AQUA + "" + BOLD + "Соло");
        } else {
            side.setLine(2, LIGHT_PURPLE + "" + BOLD + "Дуо");
        }
    }

}
