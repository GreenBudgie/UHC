package ru.greenbudgie.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.WorldManager;

public class LobbySignSpectate extends LobbySign {

    @Override
    public String getConfigName() {
        return "SPECTATE";
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
        if(UHC.playing) {
            PlayerManager.addSpectator(clicker);
            clicker.teleport(WorldManager.spawnLocation);
            UHC.refreshScoreboards();
            for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
                inGamePlayer.sendMessage(ChatColor.GOLD + clicker.getName() + ChatColor.AQUA + " присоединился к наблюдателям");
            }
        }
    }

    @Override
    public void updateText(Sign sign) {
        if(!UHC.playing) {
            sign.setLine(1, ChatColor.GRAY + "<Наблюдать>");
            sign.setLine(2, ChatColor.DARK_RED + "Игра не идет");
        } else {
            sign.setLine(1, ChatColor.GRAY + "<" + ChatColor.AQUA + "Наблюдать" + ChatColor.GRAY + ">");
        }
    }

}
