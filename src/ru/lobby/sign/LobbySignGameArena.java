package ru.lobby.sign;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.UHC.ArenaManager;
import ru.UHC.WorldManager;

public class LobbySignGameArena extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_ARENA";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        if(clicker.isSneaking()) {
            String arrows = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ">>>";
            Bukkit.broadcastMessage(arrows + ChatColor.RESET + ChatColor.GRAY + " Обновление арены...");
            ArenaManager.setupCurrentArena();
            Bukkit.broadcastMessage(arrows + ChatColor.RESET + ChatColor.GRAY + " Новая арена установлена!");
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
        if(ArenaManager.needsUpdate()) {
            sign.setLine(0, ChatColor.DARK_RED + "Shift: применить");
        }
        sign.setLine(1, ChatColor.DARK_BLUE + "Арена:");
        if(ArenaManager.getChosenArena() == null) {
            sign.setLine(2, ChatColor.DARK_GREEN + "Случайная");
            if(ArenaManager.doAnnounceArena()) {
                sign.setLine(3, ChatColor.GRAY + "- Известная -");
            } else {
                sign.setLine(3, ChatColor.GRAY + "- Скрытая -");
            }
        } else {
            sign.setLine(2, ChatColor.GREEN + ArenaManager.getChosenArena().name());
        }
    }

}
