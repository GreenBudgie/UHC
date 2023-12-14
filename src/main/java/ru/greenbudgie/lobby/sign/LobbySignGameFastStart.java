package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.configuration.EnumCycler;
import ru.greenbudgie.configuration.FastStart;

import static org.bukkit.ChatColor.*;

public class LobbySignGameFastStart extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_FAST_START";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        UHC.fastStart =  EnumCycler.nextValue(UHC.fastStart, FastStart.values());
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(1, GRAY + "Быстрый старт");
        side.setLine(2, UHC.fastStart != FastStart.DISABLED ?
                (GREEN + "" + BOLD +"Включен") :
                (DARK_GRAY + "" + BOLD + "Отключен"));
        if (UHC.fastStart == FastStart.DISABLED) {
            side.setLine(3, "");
            return;
        }
        side.setLine(3, UHC.fastStart == FastStart.WITH_MUTATORS ?
                        (LIGHT_PURPLE + "С мутаторами") :
                        (RED + "Без мутаторов"));
    }

}
