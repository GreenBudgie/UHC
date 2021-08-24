package ru.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import ru.UHC.UHCPlayer;
import ru.main.UHCPlugin;
import ru.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class UHCClass implements Listener {

    public UHCClass() {
        ClassManager.classes.add(this);
        Bukkit.getPluginManager().registerEvents(this, UHCPlugin.instance);
    }

    public abstract String getName();
    public abstract String[] getAdvantages();
    public abstract String[] getDisadvantages();
    public abstract Material getItemToShow();

    public final String getConfigName() {
        return this.getClass().getSimpleName();
    }

    public void onUpdate(UHCPlayer uhcPlayer) {}

    /**
     * Defines a list of items that will be given to a player as the game start
     * @return A list of items; might be empty
     */
    public List<ItemStack> getStartItems() {
        return new ArrayList<>();
    }

    public ItemStack makeItemToShow(Player forPlayer) {
        ItemStack item = new ItemStack(getItemToShow());
        ItemUtils.setName(item, getName());
        for(String advantage : getAdvantages()) {
            ItemUtils.addSplittedLore(item,
                    ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "+ " +
                    ChatColor.RESET + ChatColor.GREEN + advantage);
        }
        for(String disadvantage : getDisadvantages()) {
            ItemUtils.addSplittedLore(item,
                    ChatColor.DARK_RED + "" + ChatColor.BOLD + "- " +
                            ChatColor.RESET + ChatColor.RED + disadvantage);
        }
        if(ClassManager.getClassInLobby(forPlayer) == this) {
            ItemUtils.addGlow(item);
        }
        return item;
    }

    public final String getTabPrefix() {
        return ChatColor.DARK_GRAY + "[" + getName() + ChatColor.DARK_GRAY + "]";
    }

    public final boolean hasClass(Player player) {
        return this == ClassManager.getInGameClass(player);
    }

}
