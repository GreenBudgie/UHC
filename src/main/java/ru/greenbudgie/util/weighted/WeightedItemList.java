package ru.greenbudgie.util.weighted;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WeightedItemList extends WeightedList<ItemStack, WeightedItem> {

    public WeightedItemList(WeightedItem... items) {
        super(items);
    }

    public List<ItemStack> getPreviewItems() {
        return elements.stream().map(WeightedItem::getPreviewItem).toList();
    }

}
