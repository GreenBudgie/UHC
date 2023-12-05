package ru.greenbudgie.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.rating.InventoryBuilderRating;

public class LobbySignShowRating extends LobbySign {

    @Override
    public String getConfigName() {
        return "SHOW_RATING";
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
        InventoryBuilderRating builder = InventoryBuilderRating.getBuilder(clicker);
        builder.setOp(false);
        builder.openInventory();
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(1, ChatColor.DARK_AQUA + "Рейтинг");
        sign.setLine(2, ChatColor.GRAY + "/rating");
    }

}
