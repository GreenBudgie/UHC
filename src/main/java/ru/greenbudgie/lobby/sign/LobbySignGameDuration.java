package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.configuration.EnumCycler;
import ru.greenbudgie.UHC.configuration.GameDuration;

import static org.bukkit.ChatColor.*;

public class LobbySignGameDuration extends LobbySign {

    @Override
    public String getConfigName() {
        return "GAME_DURATION";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        UHC.gameDuration = EnumCycler.nextValue(UHC.gameDuration, GameDuration.values());
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(0, GRAY + "Длительность");

        String durationInfo = switch (UHC.gameDuration) {
            case SHORT -> GREEN + "" + BOLD + "Быстрая";
            case DEFAULT -> AQUA + "" + BOLD + "Обычная";
            case LONG -> RED + "" + BOLD + "Долгая";
        };
        int fullDuration = UHC.getNoPVPDuration() + UHC.getGameDuration();

        side.setLine(1, durationInfo + RESET + GRAY + ", " + fullDuration + "мин");

        side.setLine(
                2,
                AQUA + "" + BOLD + UHC.getNoPVPDuration() + RESET + GRAY + " минут до ПВП"
        );

        side.setLine(
                3,
                AQUA + "" + BOLD + UHC.getGameDuration() + GRAY + " минут до ДМ"
        );
    }

}
