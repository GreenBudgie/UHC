package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.lobby.LobbyTeamBuilder;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.LIGHT_PURPLE;

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
        var side = sign.getSide(Side.FRONT);
        side.setLine(0, LIGHT_PURPLE + "Выбрать");
        side.setLine(1, LIGHT_PURPLE + "тиммейта");
        side.setLine(2, GRAY + "/teammate");
    }

}
