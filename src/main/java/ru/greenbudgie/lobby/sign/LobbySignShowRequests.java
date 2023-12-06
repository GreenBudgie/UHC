package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.requester.ItemRequester;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GRAY;

public class LobbySignShowRequests extends LobbySign {

    @Override
    public String getConfigName() {
        return "SHOW_REQUESTS";
    }

    @Override
    public boolean canBeUsedByAnyone() {
        return true;
    }

    @Override
    public boolean canUseWhilePlaying() {
        return true;
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        ItemRequester.openRequesterInventory(clicker);
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(1, AQUA + "Запросы");
        side.setLine(2, GRAY + "/requests");
    }

}
