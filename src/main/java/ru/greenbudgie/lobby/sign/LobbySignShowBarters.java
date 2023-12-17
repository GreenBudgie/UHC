package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.nether.PiglinBarterManager;

import static org.bukkit.ChatColor.*;

public class LobbySignShowBarters extends LobbySign {

    @Override
    public String getConfigName() {
        return "SHOW_BARTERS";
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
        PiglinBarterManager.openBartersPreviewInventory(clicker);
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(0, GOLD + "Торговля с");
        side.setLine(1, GOLD + "Пиглинами");
        side.setLine(2, GRAY + "/barters");
    }

}
