package ru.greenbudgie.lobby.sign;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.ArenaManager;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.WorldManager;

import static org.bukkit.ChatColor.*;

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
            Location teleportLocation;
            if (UHC.state.isDeathmatch()) {
                teleportLocation = ArenaManager.getCurrentArena().getWorld().getSpawnLocation();
            } else {
                teleportLocation = WorldManager.spawnLocation;
            }
            clicker.teleport(teleportLocation);
            UHC.refreshScoreboards();
            for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
                inGamePlayer.sendMessage(GOLD + clicker.getName() + AQUA + " присоединился к наблюдателям");
            }
        }
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        if(!UHC.playing) {
            side.setLine(1, GRAY + "<" + DARK_GRAY + BOLD + "Наблюдать" + RESET + GRAY + ">");
            side.setLine(2, RED + "Игра не идет");
        } else {
            side.setLine(1, GRAY + "<" + AQUA + BOLD + "Наблюдать" + RESET + GRAY + ">");
        }
    }

}
