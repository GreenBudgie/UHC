package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.classes.ClassManager;

import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GRAY;

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
        var side = sign.getSide(Side.FRONT);
        side.setLine(0, DARK_GREEN + "Выбрать");
        side.setLine(1, DARK_GREEN + "класс");
        side.setLine(2, GRAY + "/class");
    }

}
