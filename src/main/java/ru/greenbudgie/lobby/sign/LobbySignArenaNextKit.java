package ru.greenbudgie.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.lobby.LobbyGameManager;
import ru.greenbudgie.util.InventoryHelper;

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
        String text = ChatColor.GREEN + "Новый набор: " + ChatColor.LIGHT_PURPLE +
                LobbyGameManager.PVP_ARENA.getCurrentKit().getName();
        InventoryHelper.sendActionBarMessage(clicker, text);
        for(Player player : LobbyGameManager.PVP_ARENA.getPlayersOnArena()) {
            InventoryHelper.sendActionBarMessage(player, text);
        }
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(0, ChatColor.DARK_BLUE + "Убийств до");
        sign.setLine(1, ChatColor.DARK_BLUE + "след. набора:");
        sign.setLine(2, ChatColor.DARK_AQUA + "" + LobbyGameManager.PVP_ARENA.getKillsToNextKit());
        sign.setLine(3, ChatColor.DARK_GREEN + "<Сменить>");
    }

}
