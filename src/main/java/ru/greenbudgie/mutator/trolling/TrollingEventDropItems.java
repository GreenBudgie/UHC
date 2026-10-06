package ru.greenbudgie.mutator.trolling;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.greenbudgie.UHC.PlayerManager;

public class TrollingEventDropItems extends TrollingEvent {

    @Override
    public String getName() {
        return "Выброс предметов";
    }

    @Override
    public void execute() {
        for (Player player : PlayerManager.getAliveOnlinePlayers()) {
            dropHandItem(player, EquipmentSlot.HAND);
            dropHandItem(player, EquipmentSlot.OFF_HAND);
        }
    }

    /**
     * Throws the whole stack from the given hand the same way as the vanilla drop does
     */
    private void dropHandItem(Player player, EquipmentSlot hand) {
        PlayerInventory inventory = player.getInventory();
        ItemStack handItem = inventory.getItem(hand);
        if (handItem == null || handItem.getType().isAir()) {
            return;
        }
        inventory.setItem(hand, null);
        ((CraftPlayer) player).getHandle().drop(CraftItemStack.asNMSCopy(handItem), false, true);
    }

}
