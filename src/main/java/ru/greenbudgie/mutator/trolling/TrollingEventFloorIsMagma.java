package ru.greenbudgie.mutator.trolling;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.Region;
import ru.greenbudgie.util.WorldHelper;

import java.util.List;
import java.util.Objects;

public class TrollingEventFloorIsMagma extends TrollingEvent {

    private static final int RADIUS = 3;

    @Override
    public String getName() {
        return "Пол - это Магма";
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
            if (location.getY() <= minHeight + 1) {
                return;
            }
            Location startLocation = location.clone().add(-RADIUS, -1, -RADIUS);
            Location endLocation = location.clone().add(RADIUS, -1, RADIUS);
            Region holeRegion = new Region(
                    startLocation,
                    endLocation
            );
            List<Block> breakableBlocks = holeRegion.getBlocksInside().stream()
                    .filter(block -> !WorldHelper.isUnbreakable(block))
                    .toList();
            for (Block block : breakableBlocks) {
                block.breakNaturally();
                block.setType(Material.MAGMA_BLOCK);
            }
        }
    }

}
