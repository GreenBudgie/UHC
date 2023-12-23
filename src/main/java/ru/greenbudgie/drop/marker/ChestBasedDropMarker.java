package ru.greenbudgie.drop.marker;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.drop.ChestBasedDrop;
import ru.greenbudgie.util.WorldHelper;

public class ChestBasedDropMarker extends DropMarker<ChestBasedDrop> {

    public ChestBasedDropMarker(ChestBasedDrop drop) {
        super(drop);
    }

    @Override
    public Vector getLocationShift() {
        return new Vector(0.5, 0.5, 0.5);
    }

    @EventHandler
    public void removeOnChestInteract(PlayerInteractEvent event) {
        if (!isDropped) {
            return;
        }
        Player player = event.getPlayer();
        if (!PlayerManager.isPlaying(player)) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null && WorldHelper.compareIntLocations(clickedBlock.getLocation(), location)) {
            remove();
        }
    }

    @EventHandler
    public void removeOnChestBreak(BlockBreakEvent event) {
        if (!isDropped) {
            return;
        }
        Block block = event.getBlock();
        if (WorldHelper.compareIntLocations(block.getLocation(), location)) {
            remove();
        }
    }

}
