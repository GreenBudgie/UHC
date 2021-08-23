package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.classes.ClassManager;
import ru.mutator.InventoryBuilderMutator;

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
        ClassManager.openClassInventory(clicker);
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(0, ChatColor.DARK_GRAY + "<" + ChatColor.DARK_GREEN + "Выбрать");
        sign.setLine(1, ChatColor.DARK_GREEN + "класс" + ChatColor.DARK_GRAY + ">");
        sign.setLine(2, ChatColor.GRAY + "/class");
    }

}
