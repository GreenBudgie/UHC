package ru.greenbudgie.mutator.trolling;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.Region;

import java.util.List;
import java.util.Objects;

public class TrollingEventFlood extends TrollingEvent {

    private static final int RADIUS = 3;

    @Override
    public String getName() {
        return "Потоп";
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
            int maxHeight = Objects.requireNonNull(location.getWorld()).getMaxHeight();
            if (location.getY() <= minHeight || location.getY() >= maxHeight) {
                return;
            }
            Location startLocation = location.clone().add(-RADIUS, -RADIUS, -RADIUS);
            Location endLocation = location.clone().add(RADIUS, RADIUS, RADIUS);
            if (startLocation.getY() <= minHeight) {
                endLocation.setY(minHeight);
            }
            if (endLocation.getY() >= maxHeight) {
                endLocation.setY(maxHeight);
            }
            Region floodRegion = new Region(
                    startLocation,
                    endLocation
            );
            List<Block> notSolidBlocks = floodRegion.getBlocksInside().stream()
                    .filter(block -> !block.getType().isSolid())
                    .toList();
            for (Block block : notSolidBlocks) {
                block.breakNaturally();
                block.setType(Material.WATER);
            }
        }
    }

}
