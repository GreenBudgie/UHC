package ru.greenbudgie.lobby.game.parkour;

import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.util.InventoryHelper;
import ru.greenbudgie.util.item.ItemUtils;

import static org.bukkit.ChatColor.*;

public class ParkourItems {

    private static final String RESTART_HEAD_LINK =
            "http://textures.minecraft.net/texture/6e8c3ce2aee6cf2faade7db37bbae73a36627ac1473fef75b410a0af97659f";

    private static final String END_HEAD_LINK =
            "http://textures.minecraft.net/texture/a3852bf616f31ed67c37de4b0baa2c5f8d8fca82e72dbcafcba66956a81c4";

    public static final ItemStack RESET_ITEM = ItemUtils
            .builder(InventoryHelper.generateHead(RESTART_HEAD_LINK))
            .withName(GOLD + "" + BOLD + "Рестарт" + DARK_GRAY + BOLD + " > " + GRAY + "ПКМ")
            .build();

    public static final ItemStack END_ITEM = ItemUtils
            .builder(InventoryHelper.generateHead(END_HEAD_LINK))
            .withName(DARK_RED + "" + BOLD + "Завершить паркур" + DARK_GRAY + BOLD + " > " + GRAY + "ПКМ")
            .build();

}
