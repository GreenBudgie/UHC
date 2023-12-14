package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.configuration.EnumCycler;
import ru.greenbudgie.configuration.MapSize;
import ru.greenbudgie.util.NumericalCases;

import static org.bukkit.ChatColor.*;

public class LobbySignGameMapSize extends LobbySign {

    private static final NumericalCases chunkCases = new NumericalCases(
            "чанк",
            "чанка",
            "чанков"
    );

    @Override
    public String getConfigName() {
        return "GAME_MAP_SIZE";
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        UHC.mapSize = EnumCycler.nextValue(UHC.mapSize, MapSize.values());
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(0, GRAY + "Размер карты");

        String sizeInfo = switch (UHC.mapSize) {
            case SMALL -> GREEN + "" + BOLD + "Маленький";
            case DEFAULT -> AQUA + "" + BOLD + "Обычный";
            case BIG -> RED + "" + BOLD + "Большой";
            case EXTREME -> DARK_RED + "" + BOLD + "Гигантский";
        };

        int chunksPerPlayer = UHC.mapSize.getChunksPerPlayer();

        side.setLine(1, sizeInfo);
        side.setLine(2, AQUA + "" + BOLD + chunksPerPlayer + RESET + GRAY + " " + chunkCases.byNumber(chunksPerPlayer));
        side.setLine(3, GRAY + "на игрока");
    }

}
