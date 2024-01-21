package ru.greenbudgie.mutator.trolling;

import org.bukkit.Location;
import org.bukkit.block.Block;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.Region;
import ru.greenbudgie.util.WorldHelper;

import java.util.Objects;

public class TrollingEventRemoveTerrain extends TrollingEvent {

    private static final int DEPTH = 5;
    private static final int RADIUS = 3;

    @Override
    public String getName() {
        return "Провал";
    }

    @Override
    public boolean canWorkOnArena() {
        return false;
    }

    @Override
    public void execute() {
        for (UHCPlayer player : PlayerManager.getAlivePlayers()) {
            Location location = player.getLocation();
            int minHeight = Objects.requireNonNull(location.getWorld()).getMinHeight();
            if (location.getY() <= minHeight + 2) {
                return;
            }
            Location startLocation = location.clone().add(-RADIUS, 0, -RADIUS);
            Location endLocation = location.clone().add(RADIUS, -DEPTH, RADIUS);
            if (endLocation.getY() <= minHeight) {
                endLocation.setY(minHeight + 2);
            }
            Region holeRegion = new Region(
                    startLocation,
                    endLocation
            );
            holeRegion.getBlocksInside().stream()
                    .filter(block -> !WorldHelper.isUnbreakable(block))
                    .forEach(Block::breakNaturally);
        }
    }

}
