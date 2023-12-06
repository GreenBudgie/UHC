package ru.greenbudgie.lobby.sign;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.ArenaManager;

import static org.bukkit.ChatColor.*;

public class LobbySignGameArena extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_ARENA";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        if(clicker.isSneaking()) {
            String arrows = DARK_GRAY + "" + BOLD + ">>>";
            Bukkit.broadcastMessage(arrows + RESET + GRAY + " Обновление арены...");
            ArenaManager.setupCurrentArena();
            Bukkit.broadcastMessage(arrows + RESET + GRAY + " Новая арена установлена!");
            return;
        }
        if(ArenaManager.getChosenArena() == null) {
            if(ArenaManager.doAnnounceArena()) {
                ArenaManager.setAnnounceArena(false);
            } else {
                ArenaManager.switchChosenArena();
                ArenaManager.setAnnounceArena(true);
            }
        } else {
            ArenaManager.switchChosenArena();
        }
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(0, GRAY + "Арена");
        if(ArenaManager.getChosenArena() == null) {
            side.setLine(1, GREEN + "" + BOLD + "Случайная");
            if(ArenaManager.doAnnounceArena()) {
                side.setLine(2, GRAY + "- Известная -");
            } else {
                side.setLine(2, GRAY + "- Скрытая -");
            }
        } else {
            side.setLine(1, GREEN + ArenaManager.getChosenArena().getName());
        }
        if(ArenaManager.needsUpdate()) {
            side.setLine(3, DARK_RED + "" + BOLD + "<SHIFT + клик>");
        }
    }

}
