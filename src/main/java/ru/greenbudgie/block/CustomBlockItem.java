package ru.greenbudgie.block;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
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

    /**
     * Gets the item that drops when this block removes.
     * The block is still present when this method is called.
     */
    protected ItemStack getDropItem() {
        return getRepresentingItem().getItemStack();
    }

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
            location.getWorld().dropItemNaturally(centerLocation, getDropItem());
        }
        remove();
    }

}
