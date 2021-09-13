package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.mutator.InventoryBuilderMutator;
import ru.requester.ItemRequester;

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
        sign.setLine(1, ChatColor.AQUA + "Запросы");
        sign.setLine(2, ChatColor.GRAY + "/requests");
    }

}
