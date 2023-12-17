package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.drop.DropsPreviewInventory;

import static org.bukkit.ChatColor.*;

public class LobbySignShowDrops extends LobbySign {

    @Override
    public String getConfigName() {
        return "SHOW_DROPS";
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
        DropsPreviewInventory.openDropsPreviewInventory(clicker);
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(1, AQUA + "Дропы");
        side.setLine(2, GRAY + "/drops");
    }

}
