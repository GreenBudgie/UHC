package ru.greenbudgie.util;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeightedItemList {

    private final WeightedItem[] items;
    private final List<WeightedItem> weightedItems;

    public WeightedItemList(WeightedItem... items) {
        this.items = items;
        List<WeightedItem> weightedList = new ArrayList<>();
        for (WeightedItem item : items) {
            for (int i = 0; i < item.getWeight(); i++) {
                weightedList.add(item);
            }
        }
        weightedItems = weightedList;
    }

    public WeightedItem[] getItems() {
        return items;
    }

    public List<ItemStack> getPreviewItems() {
        return Arrays.stream(items).map(WeightedItem::getPreviewItem).toList();
    }

    public WeightedItem getRandomItemWeighted() {
        return MathUtils.choose(weightedItems);
    }

}
