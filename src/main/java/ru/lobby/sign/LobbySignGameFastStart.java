package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.UHC.UHC;
import ru.UHC.WorldManager;

public class LobbySignGameFastStart extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_FAST_START";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        UHC.fastStart = UHC.fastStart == 2 ? 0 : UHC.fastStart + 1;
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(1, ChatColor.DARK_BLUE + "Быстрый старт");
        sign.setLine(2, UHC.fastStart > 0 ?
                (ChatColor.DARK_GREEN + "Включен") :
                (ChatColor.DARK_GRAY + "Отключен"));
        sign.setLine(3, UHC.fastStart == 0 ?
                "" :
                (UHC.fastStart == 2 ?
                        (ChatColor.DARK_AQUA + "С мутаторами") :
                        (ChatColor.DARK_RED + "Без мутаторов")));
    }

}
