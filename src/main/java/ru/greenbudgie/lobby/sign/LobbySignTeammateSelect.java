package ru.greenbudgie.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.lobby.LobbyTeamBuilder;

public class LobbySignTeammateSelect extends LobbySign {

    @Override
    public String getConfigName() {
        return "TEAMMATE_SELECT";
    }

    @Override
    public boolean canBeUsedByAnyone() {
        return true;
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        if(!UHC.playing) {
            LobbyTeamBuilder.openRequestSendInventory(clicker);
        }
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(0, ChatColor.LIGHT_PURPLE + "Выбрать");
        sign.setLine(1, ChatColor.LIGHT_PURPLE + "тиммейта");
        sign.setLine(2, ChatColor.GRAY + "/teammate");
    }

}
