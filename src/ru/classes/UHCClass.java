package ru.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import ru.UHC.PlayerManager;
import ru.UHC.RecipeHandler;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.event.GameStartEvent;
import ru.main.UHCPlugin;
import ru.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class UHCClass implements Listener {

    public UHCClass() {
        ClassManager.classes.add(this);
        Bukkit.getPluginManager().registerEvents(this, UHCPlugin.instance);
        if(this instanceof RecipeHolderClass recipeHolder) {
            for(Recipe classRecipe : recipeHolder.getClassRecipes()) {
                Bukkit.addRecipe(classRecipe);
            }
        }
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
     * Defines an array of items that will be given to a player as the game starts
     * @return A list of items; might be empty
     */
    public ItemStack[] getStartItems() {
        return new ItemStack[0];
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

    public final List<UHCPlayer> getAliveOnlinePlayersWithClass() {
        return PlayerManager.getPlayers().stream().filter(uhcPlayer -> uhcPlayer.getUHCClass() == this && uhcPlayer.isAliveAndOnline()).toList();
    }

    public final List<UHCPlayer> getAlivePlayersWithClass() {
        return PlayerManager.getPlayers().stream().filter(uhcPlayer -> uhcPlayer.getUHCClass() == this && uhcPlayer.isAlive()).toList();
    }

    public final List<UHCPlayer> getPlayersWithClass() {
        return PlayerManager.getPlayers().stream().filter(uhcPlayer -> uhcPlayer.getUHCClass() == this).toList();
    }

    public final String getTabPrefix() {
        return ChatColor.DARK_GRAY + "[" + getName() + ChatColor.DARK_GRAY + "]";
    }

    public final boolean hasClass(Player player) {
        return this == ClassManager.getInGameClass(player);
    }

    @EventHandler
    public void giveStartItems(GameStartEvent event) {
        for(UHCPlayer uhcPlayer : getAliveOnlinePlayersWithClass()) {
            for(ItemStack item : getStartItems()) {
                uhcPlayer.getPlayer().getInventory().addItem(item);
            }
        }
    }

    @EventHandler
    public void customRecipeHandle(PrepareItemCraftEvent event) {
        if(event.getView().getPlayer() instanceof Player player && this instanceof RecipeHolderClass recipeHolder) {
            Recipe eventRecipe = event.getRecipe();
            if(eventRecipe instanceof Keyed keyedEventRecipe) {
                boolean isCustom = false;
                for(Recipe classRecipe : recipeHolder.getClassRecipes()) {
                    if(classRecipe instanceof Keyed keyedClassRecipe) {
                        if(keyedClassRecipe.getKey().equals(keyedEventRecipe.getKey())) {
                            isCustom = true;
                            break;
                        }
                    }
                }
                if((isCustom && !hasClass(player)) || !UHC.playing) {
                    event.getInventory().setResult(null);
                }
            }
        }
    }

}
