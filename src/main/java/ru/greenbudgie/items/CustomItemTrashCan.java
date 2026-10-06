package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.ShulkerBox;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.item.ItemInfo;

import java.util.Map;
import java.util.Set;

public class CustomItemTrashCan extends RequesterCustomItem implements Listener {

    private static final Set<Material> trash = Set.of(
            Material.STONE,
            Material.COBBLESTONE,
            Material.ANDESITE,
            Material.DIORITE,
            Material.GRANITE,
            Material.DEEPSLATE,
            Material.COBBLED_DEEPSLATE,
            Material.NETHERRACK,
            Material.DIRT,
            Material.GRAVEL,
            Material.CLAY_BALL,
            Material.CLAY,
            Material.MOSS_BLOCK,
            Material.MOSS_CARPET,
            Material.CALCITE,
            Material.TUFF,
            Material.DRIPSTONE_BLOCK,
            Material.CRIMSON_NYLIUM,
            Material.WARPED_NYLIUM,
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.LEAF_LITTER,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.CORNFLOWER,
            Material.FIREFLY_BUSH,
            Material.CRIMSON_ROOTS,
            Material.WARPED_ROOTS,
            Material.NETHER_SPROUTS,
            Material.LILAC,
            Material.ROSE_BUSH,
            Material.PEONY,
            Material.GLOW_LICHEN,
            Material.HANGING_ROOTS,
            Material.SANDSTONE,
            Material.RED_SANDSTONE,
            Material.COARSE_DIRT,
            Material.ROOTED_DIRT,
            Material.GRASS_BLOCK,
            Material.ROTTEN_FLESH,
            Material.SPIDER_EYE,
            Material.BONE
    );

    public String getName() {
        return ChatColor.GRAY + "" + ChatColor.BOLD + "Trash Can";
    }

    public Material getMaterial() {
        return Material.GRAY_SHULKER_BOX;
    }

    @Override
    public ItemInfo getDescription() {
        return new ItemInfo("Шалкер бокс, который, находясь в инвентаре, автоматически засасывает в себя подбираемый тобой мусор")
                .extra("К мусору относится любой камень, бесполезные дропы с мобов, растительность и т.п.");
    }

    @Override
    public int getRedstonePrice() {
        return 8;
    }

    @Override
    public int getLapisPrice() {
        return 0;
    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!UHC.playing) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!PlayerManager.isPlaying(player)) {
            return;
        }

        Item itemEntity = event.getItem();
        ItemStack pickedItem = itemEntity.getItemStack();
        if (!trash.contains(pickedItem.getType())) {
            return;
        }

        int totalAmount = pickedItem.getAmount() + event.getRemaining();
        ItemStack itemToStore = pickedItem.clone();
        itemToStore.setAmount(totalAmount);
        int leftAmount = storeInTrashCans(player, itemToStore);
        if (leftAmount == totalAmount) {
            return;
        }
        if (leftAmount == 0) {
            event.setCancelled(true);
            playPickupAnimation(player, itemEntity, totalAmount);
            itemEntity.remove();
            return;
        }
        ItemStack leftItem = pickedItem.clone();
        leftItem.setAmount(leftAmount);
        itemEntity.setItemStack(leftItem);
    }

    /**
     * Plays the vanilla item pickup animation and sound.
     * Must be called before the item entity is removed.
     */
    private void playPickupAnimation(Player player, Item itemEntity, int amount) {
        ((CraftPlayer) player).getHandle().take(((CraftItem) itemEntity).getHandle(), amount);
        itemEntity.getWorld().playSound(itemEntity.getLocation(), Sound.ENTITY_SHULKER_CLOSE, SoundCategory.PLAYERS, 0.2F, (float)MathUtils.randomRangeDouble(1.4f, 1.8f));
    }

    /**
     * Stores the item in the trash cans from the player's inventory
     * @return The amount of the item that did not fit into the trash cans
     */
    private int storeInTrashCans(Player player, ItemStack item) {
        PlayerInventory inventory = player.getInventory();
        int leftAmount = item.getAmount();
        for (int slot = 0; slot < inventory.getSize() && leftAmount > 0; slot++) {
            ItemStack trashCan = inventory.getItem(slot);
            if (!isEquals(trashCan)) {
                continue;
            }
            BlockStateMeta meta = (BlockStateMeta) trashCan.getItemMeta();
            ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
            ItemStack itemToAdd = item.clone();
            itemToAdd.setAmount(leftAmount);
            Map<Integer, ItemStack> notAdded = shulkerBox.getInventory().addItem(itemToAdd);
            leftAmount = notAdded.values().stream().mapToInt(ItemStack::getAmount).sum();
            meta.setBlockState(shulkerBox);
            trashCan.setItemMeta(meta);
            inventory.setItem(slot, trashCan);
        }
        return leftAmount;
    }
}
