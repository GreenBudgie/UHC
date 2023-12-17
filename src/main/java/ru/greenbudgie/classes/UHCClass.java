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
import ru.greenbudgie.util.item.ItemInfo;
import ru.greenbudgie.util.item.ItemUtils;

import java.util.List;

public abstract class UHCClass implements Listener {

    private static final String PLUS_HEAD_LINK = "http://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f";
    private static final String MINUS_HEAD_LINK = "http://textures.minecraft.net/texture/4e4b8b8d2362c864e062301487d94d3272a6b570afbf80c2c5b148c954579d46";
    private static final ItemStack ADVANTAGE_ITEM = InventoryHelper.generateHead(PLUS_HEAD_LINK);
    private static final ItemStack DISADVANTAGE_ITEM = InventoryHelper.generateHead(MINUS_HEAD_LINK);

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
            ItemStack advantageItem = ADVANTAGE_ITEM.clone();
            ItemUtils.setName(advantageItem, ChatColor.GREEN + "" + ChatColor.BOLD + "+");
            advantage.applyToItem(advantageItem);
            this.advantageItems[i] = advantageItem;
        }

        ItemInfo[] disadvantages = getDisadvantages();
        this.disadvantageItems = new ItemStack[disadvantages.length];
        for(int i = 0; i < disadvantages.length; i++) {
            ItemInfo disadvantage = disadvantages[i];
            ItemStack disadvantageItem = DISADVANTAGE_ITEM.clone();
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
