package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.UHC.UHC;
import ru.UHC.WorldManager;

public class LobbySignGameArena extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_ARENA";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {

    }

    @Override
    public void updateText(Sign sign) {

    }

}
