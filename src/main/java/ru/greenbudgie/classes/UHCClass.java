package ru.greenbudgie.classes;

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
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.event.GameStartEvent;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.InventoryHelper;
import ru.greenbudgie.util.ItemInfo;
import ru.greenbudgie.util.ItemUtils;

import java.util.List;

public abstract class UHCClass implements Listener {

    private ItemStack[] advantageItems;
    private ItemStack[] disadvantageItems;

    public UHCClass() {
        makeClassInfoItems();
        ClassManager.classes.add(this);
        Bukkit.getPluginManager().registerEvents(this, UHCPlugin.instance);
        if(this instanceof RecipeHolderClass recipeHolder) {
            for(Recipe classRecipe : recipeHolder.getClassRecipes()) {
                Bukkit.addRecipe(classRecipe);
            }
        }
    }

    public abstract String getName();
    public abstract ItemInfo[] getAdvantages();
    public abstract ItemInfo[] getDisadvantages();
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
        ItemUtils.addLore(item, ChatColor.GRAY + "Подробнее - <ЛКМ>");
        ItemUtils.addLore(item, ChatColor.GRAY + "Выбрать класс - <ПКМ>");
        if(ClassManager.getClassInLobby(forPlayer) == this) {
            ItemUtils.addGlow(item);
        }
        return item;
    }

    public ItemStack[] getAdvantageItems() {
        return advantageItems;
    }

    public ItemStack[] getDisadvantageItems() {
        return disadvantageItems;
    }

    public void makeClassInfoItems() {
        ItemInfo[] advantages = getAdvantages();
        this.advantageItems = new ItemStack[advantages.length];
        for(int i = 0; i < advantages.length; i++) {
            ItemInfo advantage = advantages[i];
            ItemStack advantageItem = InventoryHelper.generateHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJjMTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1MWQyN2QyNzNjMTU3MjUzMWYifX19");
            ItemUtils.setName(advantageItem, ChatColor.GREEN + "" + ChatColor.BOLD + "+");
            advantage.applyToItem(advantageItem);
            this.advantageItems[i] = advantageItem;
        }

        ItemInfo[] disadvantages = getDisadvantages();
        this.disadvantageItems = new ItemStack[disadvantages.length];
        for(int i = 0; i < disadvantages.length; i++) {
            ItemInfo disadvantage = disadvantages[i];
            ItemStack disadvantageItem = InventoryHelper.generateHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=");
            ItemUtils.setName(disadvantageItem, ChatColor.RED + "" + ChatColor.BOLD + "-");
            disadvantage.applyToItem(disadvantageItem);
            this.disadvantageItems[i] = disadvantageItem;
        }
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
