package ru.greenbudgie.block;

import org.bukkit.Location;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;

public class CustomBlockTrashCan extends CustomBlockItem {

    public CustomBlockTrashCan(Location location) {
        super(location);
    }

    @Override
    public CustomItem getRepresentingItem() {
        return CustomItems.trashCan;
    }

    @Override
    public boolean doReplaceBlockOnCreate() {
        return false;
    }

    @Override
    protected ItemStack getDropItem() {
        ItemStack item = super.getDropItem();
        if (!(getBlock().getState() instanceof ShulkerBox placedShulkerBox)) {
            return item;
        }
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
        shulkerBox.getInventory().setContents(placedShulkerBox.getInventory().getContents());
        meta.setBlockState(shulkerBox);
        item.setItemMeta(meta);
        return item;
    }

}
