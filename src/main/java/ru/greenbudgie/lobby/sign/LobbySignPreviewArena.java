package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.ArenaManager;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;

public class LobbySignPreviewArena extends LobbySign {

    @Override
    public String getConfigName() {
        return "PREVIEW_ARENA";
    }

    @Override
    public boolean canUseWhilePlaying() {
        return true;
    }

    @Override
    public boolean canBeUsedByAnyone() {
        return true;
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        ArenaManager.openArenaPreviewInventory(clicker);
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(0, GREEN + "Просмотр");
        side.setLine(1, GREEN + "арены");
        side.setLine(2, GRAY + "/arena");
    }

}
