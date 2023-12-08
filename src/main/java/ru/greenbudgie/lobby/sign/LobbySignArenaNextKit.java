package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.lobby.game.LobbyGameManager;
import ru.greenbudgie.util.InventoryHelper;

import static org.bukkit.ChatColor.*;

public class LobbySignArenaNextKit extends LobbySign {

    @Override
    public String getConfigName() {
        return "ARENA_NEXT_KIT";
    }

    @Override
    public boolean canUseWhilePlaying() {
        return true;
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        LobbyGameManager.PVP_ARENA.setKillsToNextKit(8);
        LobbyGameManager.PVP_ARENA.setCurrentKit(LobbyGameManager.PVP_ARENA.getRandomKit());
        String text = DARK_AQUA + "Новый набор" + GRAY + ": " + LIGHT_PURPLE + BOLD +
                LobbyGameManager.PVP_ARENA.getCurrentKit().getName();
        InventoryHelper.sendActionBarMessage(clicker, text);
        for(Player player : LobbyGameManager.PVP_ARENA.getPlayersOnArena()) {
            InventoryHelper.sendActionBarMessage(player, text);
        }
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(0, AQUA + "Убийств до");
        side.setLine(1, AQUA + "след. набора");
        side.setLine(2, AQUA + "" + BOLD + LobbyGameManager.PVP_ARENA.getKillsToNextKit());
        side.setLine(3, GRAY + "<" + WHITE + BOLD + "Сменить" + RESET + GRAY + ">");
    }

}
