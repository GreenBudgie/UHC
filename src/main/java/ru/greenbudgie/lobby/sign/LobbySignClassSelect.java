package ru.greenbudgie.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.classes.ClassManager;

public class LobbySignClassSelect extends LobbySign {

    @Override
    public String getConfigName() {
        return "CLASS_SELECT";
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
        ClassManager.openClassSelectInventory(clicker);
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(0, ChatColor.DARK_GREEN + "Выбрать");
        sign.setLine(1, ChatColor.DARK_GREEN + "класс");
        sign.setLine(2, ChatColor.GRAY + "/class");
    }

}
