package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.UHC.WorldManager;
import ru.lobby.LobbyMapPreview;

public class LobbySignMapPreview extends LobbySign {

    @Override
    public String getConfigName() {
        return "MAP_PREVIEW";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        if(WorldManager.hasMap()) {
            LobbyMapPreview.setPreview();
        }
    }

    @Override
    public void updateText(Sign sign) {
        if(WorldManager.hasMap()) {
            sign.setLine(1, ChatColor.GRAY + "Перезагрузить");
            sign.setLine(2, ChatColor.GRAY + "превью");
        } else {
            sign.setLine(1, ChatColor.DARK_GRAY + "Превью");
            sign.setLine(2, ChatColor.DARK_GRAY + "недоступно");
        }
    }

}
