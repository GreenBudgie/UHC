package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.classes.ClassManager;
import ru.lobby.Lobby;

public class LobbySignReturnLobby extends LobbySign {

    @Override
    public String getConfigName() {
        return "RETURN_LOBBY";
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
        clicker.teleport(Lobby.getLobby().getSpawnLocation());
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(1, ChatColor.DARK_AQUA + "Вернуться");
        sign.setLine(2, ChatColor.DARK_AQUA + "на спавн");
    }

}
