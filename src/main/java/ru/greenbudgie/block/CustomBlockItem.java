package ru.greenbudgie.block;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import ru.greenbudgie.items.CustomItem;

/**
 * A custom block that is bound to custom item.
 * That means:
 * - The block will drop specified custom item on any remove case
 * - The block will have a custom item-mirror, that represents this block
 */
public abstract class CustomBlockItem extends CustomBlock {

    public CustomBlockItem(Location location) {
        super(location);
    }

    @Override
    public Material getMaterial() {
        return getRepresentingItem().getMaterial();
    }

    public abstract CustomItem getRepresentingItem();

    @Override
    public void onBreak(BlockBreakEvent event) {
        super.onBreak(event);
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            dropAndRemove();
        }
    }

    @Override
    public void onExplode() {
        super.onExplode();
        dropAndRemove();
    }

    /**
     * Removes the current block and drops representing item in its location
     */
    public final void dropAndRemove() {
        if(location != null && location.getWorld() != null) {
            location.getWorld().dropItemNaturally(location, getRepresentingItem().getItemStack());
        }
        remove();
    }

}
