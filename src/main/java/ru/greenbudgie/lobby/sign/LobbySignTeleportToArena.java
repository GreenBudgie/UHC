package ru.greenbudgie.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.lobby.LobbyGameManager;

public class LobbySignTeleportToArena extends LobbySign {

    @Override
    public String getConfigName() {
        return "TELEPORT_TO_ARENA";
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
        clicker.teleport(LobbyGameManager.PVP_ARENA.getSpawnLocation());
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(1, ChatColor.DARK_AQUA + "Телепорт");
        sign.setLine(2, ChatColor.DARK_AQUA + "на арену");
    }

}
