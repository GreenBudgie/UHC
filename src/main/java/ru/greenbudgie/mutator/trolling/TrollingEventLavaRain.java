package ru.greenbudgie.mutator.trolling;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.Region;
import ru.greenbudgie.util.WorldHelper;

import java.util.Objects;

public class TrollingEventLavaRain extends TrollingEvent {

    private static final int HEIGHT = 6;
    private static final int RADIUS = 2;

    @Override
    public String getName() {
        return "Лавовый Дождь";
    }

    @Override
    public boolean canWorkOnArena() {
        return false;
    }

    @Override
    public void execute() {
        for (UHCPlayer player : PlayerManager.getAlivePlayers()) {
            Location location = player.getLocation();
            int maxHeight = Objects.requireNonNull(location.getWorld()).getMaxHeight();
            if (location.getY() >= maxHeight - 2 - HEIGHT) {
                return;
            }
            Location startLocation = location.clone().add(-RADIUS, 0, -RADIUS);
            Location endLocation = location.clone().add(RADIUS, HEIGHT, RADIUS);
            if (endLocation.getY() >= maxHeight) {
                endLocation.setY(maxHeight - 2);
            }
            Region holeRegion = new Region(
                    startLocation,
                    endLocation
            );
            holeRegion.getBlocksInside().stream()
                    .filter(block -> !WorldHelper.isUnbreakable(block))
                    .forEach(Block::breakNaturally);
            Region lavaRegion = new Region(
                    endLocation.clone(),
                    endLocation.clone().add(-RADIUS * 2 - 1, 0, -RADIUS * 2 - 1)
            );
            lavaRegion.getBlocksInside().forEach(block -> block.setType(Material.LAVA));
        }
    }

}
