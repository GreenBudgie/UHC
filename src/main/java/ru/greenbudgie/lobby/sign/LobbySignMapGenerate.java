package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.WorldManager;

import static org.bukkit.ChatColor.*;

public class LobbySignMapGenerate extends LobbySign {

    @Override
    public String getConfigName() {
        return "MAP_GENERATE";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        WorldManager.regenMap();
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        if(WorldManager.hasMap()) {
            side.setLine(1, AQUA + "Мир создан");
            side.setLine(2, GRAY + "<" + GREEN + BOLD + "Пересоздать" + RESET + GRAY + ">");
        } else {
            side.setLine(1, DARK_AQUA + "Мир не создан");
            side.setLine(2, GRAY + "<" + DARK_GREEN + BOLD + "Создать" + RESET + GRAY + ">");
        }
    }

}
