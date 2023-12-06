package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.lobby.Lobby;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GRAY;

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
        var side = sign.getSide(Side.FRONT);
        side.setLine(0, GRAY + "---");
        side.setLine(1, AQUA + "Вернуться");
        side.setLine(2, AQUA + "на спавн");
        side.setLine(3, GRAY + "---");
    }

}
