package ru.greenbudgie.mutator.trolling;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.Region;

import java.util.Objects;

public class TrollingEventCobweb extends TrollingEvent {

    private static final int RADIUS = 1;

    @Override
    public String getName() {
        return "Западня";
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
            if (location.getY() <= minHeight) {
                return;
            }
            Location startLocation = location.clone().add(-RADIUS, 0, -RADIUS);
            Location endLocation = location.clone().add(RADIUS, 0, RADIUS);
            Region holeRegion = new Region(
                    startLocation,
                    endLocation
            );
            holeRegion.getBlocksInside().stream()
                    .filter(block -> Tag.REPLACEABLE.isTagged(block.getType()))
                    .forEach(block -> block.setType(Material.COBWEB));
        }
    }

}
