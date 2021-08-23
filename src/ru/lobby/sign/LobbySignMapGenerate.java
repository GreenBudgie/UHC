package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.UHC.WorldManager;

public class LobbySignMapGenerate extends LobbySign {

    @Override
    public String getConfigName() {
        return "MAP_GENERATE";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        if(!WorldManager.hasMap()) {
            WorldManager.regenMap();
        }
    }

    @Override
    public void updateText(Sign sign) {
        if(WorldManager.hasMap()) {
            sign.setLine(1, ChatColor.DARK_GREEN + "Мир создан");
            sign.setLine(2, ChatColor.DARK_BLUE + "<Пересоздать>");
        } else {
            sign.setLine(1, ChatColor.DARK_RED + "Мир не создан");
            sign.setLine(2, ChatColor.DARK_BLUE + "<Сгенерировать>");
        }
    }

}
