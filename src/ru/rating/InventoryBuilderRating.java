package ru.rating;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.main.UHCPlugin;
import ru.util.InventoryHelper;
import ru.util.ItemUtils;

import java.util.*;
import java.util.stream.IntStream;

public class InventoryBuilderRating {

    public static final String inventoryName = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Рейтинг";
    private static Set<InventoryBuilderRating> builders = new HashSet<>();

    //Some inner inventory constants
    private static final int SUMMARY_ROWS = 4;
    private static final int LINE_ROW = SUMMARY_ROWS;
    private static final int OPTION_ROW = SUMMARY_ROWS + 2;
    private static final int INV_SIZE = OPTION_ROW * 9;
    private static final int FILTER_SORT_SLOT = INV_SIZE - 6;
    private static final int PAGE_NEXT_SLOT = INV_SIZE - 1;
    private static final int PAGE_PREV_SLOT = INV_SIZE - 2;
    private static final int CANCEL_SLOT = INV_SIZE - 8;
    private static final int RATING_SLOT = INV_SIZE - 5;
    private static final int CHANGE_TYPE_SLOT = INV_SIZE - 9;
    //OP
    private static final int ADD_RANDOM_SUMMARY_SLOT = INV_SIZE - 3;
    private static final int CONFIG_RELOAD_SLOT = INV_SIZE - 4;
    private static final int CONFIG_SAVE_SLOT = INV_SIZE - 7;

    private Player player;
    private Sort sort = Sort.PLACE;
    private Filter filter = Filter.NONE;
    private boolean op = false;
    private int page = 1; //Starting from 1, not 0
    private boolean watchingGames = false;
    private GameSummary watchingGameSummary = null;

    public static void registerListener() {
        Listener listener = new Listener() {

            @EventHandler
            public void inventoryClick(InventoryClickEvent event) {
                if(InventoryBuilderRating.checkInventory(event.getView().getTitle()) && event.getCurrentItem() != null) {
                    Player player = (Player) event.getWhoClicked();
                    InventoryBuilderRating builder = InventoryBuilderRating.getBuilder(player);
                    builder.handleClick(event);
                    event.setCancelled(true);
                }
            }

        };
        Bukkit.getPluginManager().registerEvents(listener, UHCPlugin.instance);
    }

    private int getPages() {
        int maxCountPerPage = SUMMARY_ROWS * 9;
        return getCurrentInventoryItems().size() / maxCountPerPage + 1;
    }

    private void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        ItemStack item = event.getCurrentItem();
        boolean clickedSummary = slot < SUMMARY_ROWS * 9;
        if(clickedSummary) {
            if(watchingGames && watchingGameSummary == null) {
                if(ItemUtils.hasCustomValue(item, "date")) {
                    long dateMillis = Long.parseLong(ItemUtils.getCustomValue(item, "date"));
                    GameSummary summary = Rating.getGameSummaryByDate(dateMillis);
                    if(summary != null) {
                        if(isOp() && event.isRightClick()) {
                            Rating.getGameSummaries().remove(summary);
                        } else {
                            watchingGameSummary = summary;
                            page = 1;
                        }
                        openInventory();
                    }
                }
            }
        } else {
            if(item != null && item.getType() != Material.AIR) {
                switch(slot) {
                    case CANCEL_SLOT -> {
                        watchingGameSummary = null;
                        page = 1;
                    }
                    case FILTER_SORT_SLOT -> {
                        if(watchingGames) {
                            int filterIndex = filter.ordinal() + 1;
                            if(filterIndex >= Filter.values().length) filterIndex = 0;
                            filter = Filter.values()[filterIndex];
                        } else {
                            int sortIndex = sort.ordinal() + 1;
                            if(sortIndex >= Sort.values().length) sortIndex = 0;
                            sort = Sort.values()[sortIndex];
                        }
                    }
                    case PAGE_NEXT_SLOT -> nextPage();
                    case PAGE_PREV_SLOT -> prevPage();
                    case ADD_RANDOM_SUMMARY_SLOT -> {
                        if(player.isOp()) {
                            int minPlayers = event.isRightClick() ? SUMMARY_ROWS * 9 : 5;
                            int maxPlayers = event.isRightClick() ? SUMMARY_ROWS * 27 : 12;
                            if(event.isShiftClick()) {
                                for(int i = 0; i < 5; i++)
                                    Rating.getGameSummaries().add(GameSummary.generateRandomSummary(minPlayers, maxPlayers));
                            } else {
                                Rating.getGameSummaries().add(GameSummary.generateRandomSummary(minPlayers, maxPlayers));
                            }
                            Rating.updateRatingSummaries();
                        }
                    }
                    case CONFIG_RELOAD_SLOT -> {
                        if(player.isOp()) {
                            Rating.loadFromConfig();
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1);
                        }
                    }
                    case CONFIG_SAVE_SLOT -> {
                        if(player.isOp() && event.isShiftClick()) {
                            Rating.updateConfig();
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1);
                            InventoryHelper.sendActionBarMessage(player, ChatColor.RED + "Config file has been updated");
                        }
                    }
                    case CHANGE_TYPE_SLOT -> {
                        watchingGames = !watchingGames;
                        page = 1;
                    }
                }
                openInventory();
            }
        }
    }

    public void nextPage() {
        if(++page > getPages()) page = 1;
    }

    public void prevPage() {
        if(--page < 1) page = getPages();
    }

    public void setOp(boolean op) {
        this.op = op;
    }

    public boolean isOp() {
        return op;
    }

    public static boolean checkInventory(String title) {
        return title.startsWith(inventoryName);
    }

    private static Set<InventoryBuilderRating> getOpenedInventories() {
        Set<InventoryBuilderRating> viewers = new HashSet<>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(checkInventory(player.getOpenInventory().getTitle())) viewers.add(getBuilder(player));
        }
        return viewers;
    }

    public static void reopenAll() {
        getOpenedInventories().forEach(InventoryBuilderRating::openInventory);
    }

    public static InventoryBuilderRating getBuilder(Player player) {
        InventoryBuilderRating builder = builders.stream().filter(b -> b.player == player).findFirst().orElse(null);
        if(builder != null) return builder;
        InventoryBuilderRating newBuilder = new InventoryBuilderRating(player);
        builders.add(newBuilder);
        return newBuilder;
    }

    public InventoryBuilderRating(Player player) {
        this.player = player;
    }

    public void openInventory() {
        List<ItemStack> currentItems = getCurrentInventoryItems();
        int pages = getPages();

        Inventory inventory = Bukkit.createInventory(player, INV_SIZE, inventoryName + " " + getPagesInfo(pages));
        int realPage = page - 1;
        int summaryLength = SUMMARY_ROWS * 9;

        for(int index = realPage * summaryLength, slot = 0; slot < summaryLength && index < currentItems.size(); index++, slot++) {
            inventory.setItem(slot, currentItems.get(index));
        }

        ItemStack linePanel = ItemUtils.builder(Material.GRAY_STAINED_GLASS_PANE).withName(" ").build();
        IntStream.range(LINE_ROW * 9, (LINE_ROW + 1) * 9).forEach(sl -> inventory.setItem(sl, linePanel));

        placeOptionalItems(inventory);
        player.openInventory(inventory);
    }

    private void placeOptionalItems(Inventory inventory) {
        PlayerRatingSummary ratingSummary = Rating.getRatingSummaryByName(player.getName());
        if(ratingSummary != null) {
            inventory.setItem(RATING_SLOT, ratingSummary.getRepresentingItem());
        } else {
            inventory.setItem(RATING_SLOT, ItemUtils.builder(Material.PAPER).
                            withName(ChatColor.GOLD + player.getName()).
                            withLore(ChatColor.GRAY + "Тебя пока нет в рейтинге").
                            build());
        }
        if(watchingGames) {
            inventory.setItem(CHANGE_TYPE_SLOT,
                    ItemUtils.builder(Material.PLAYER_HEAD).withName(ChatColor.GRAY + "> Показать игроков <").build());
        } else {
            inventory.setItem(CHANGE_TYPE_SLOT,
                    ItemUtils.builder(Material.BOOK).withName(ChatColor.GRAY + "> Показать игры <").build());
        }
        if(!watchingGames) {
            ItemStack sortItem = ItemUtils.builder(sort.itemToShow)
                    .withName(ChatColor.GOLD + "Сортировка")
                    .withLore(ChatColor.YELLOW + "" + ChatColor.BOLD + sort.description)
                    .build();
            inventory.setItem(FILTER_SORT_SLOT, sortItem);
        } else if(watchingGameSummary != null) {
            inventory.setItem(CANCEL_SLOT, ItemUtils.builder(Material.BARRIER).withName(ChatColor.RED + "" + ChatColor.BOLD + "Назад").build());
        } else {
            ItemStack filterItem = ItemUtils.builder(filter.itemToShow)
                    .withName(ChatColor.DARK_AQUA + "Фильтр")
                    .withLore(ChatColor.AQUA + "" + ChatColor.BOLD + filter.description)
                    .build();
            inventory.setItem(FILTER_SORT_SLOT, filterItem);

            if(isOp()) {
                ItemStack randomSummaryItem = ItemUtils.builder(Material.KNOWLEDGE_BOOK)
                        .withName(ChatColor.DARK_GREEN + "Добавить рандомную игру")
                        .withLore(
                                ChatColor.GRAY + "ПКМ: очень много игроков",
                                ChatColor.GRAY + "ШИФТ: добавить сразу 5")
                        .build();
                inventory.setItem(ADD_RANDOM_SUMMARY_SLOT, randomSummaryItem);

                ItemStack configReloadItem = ItemUtils.builder(Material.NAME_TAG)
                        .withName(ChatColor.RED + "Перезагрузить с конфига")
                        .withSplittedLore(ChatColor.DARK_RED + "ОСТОРОЖНО! НЕ СОХРАНЕННЫЕ В КОНФИГ ДАННЫЕ БУДУТ УДАЛЕНЫ!", 20)
                        .build();
                inventory.setItem(CONFIG_RELOAD_SLOT, configReloadItem);

                ItemStack configSaveItem = ItemUtils.builder(Material.MAP)
                        .withName(ChatColor.DARK_RED + "Сохранить в конфиг")
                        .withSplittedLore(ChatColor.DARK_RED + "ИСПОЛЬЗОВАТЬ С ОСТОРОЖНОСТЬЮ!")
                        .withSplittedLore(ChatColor.GRAY + "Для безопасности работает только с шифт-кликом")
                        .build();
                inventory.setItem(CONFIG_SAVE_SLOT, configSaveItem);
            }
        }

        if(getPages() > 1) {
            ItemStack pgNextItem = InventoryHelper.generateHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVmMzU2YWQyYWE3YjE2NzhhZWNiODgyOTBlNWZhNWEzNDI3ZTVlNDU2ZmY0MmZiNTE1NjkwYzY3NTE3YjgifX19");
            ItemUtils.setName(pgNextItem, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ">>> " + ChatColor.AQUA + "След. страница");
            inventory.setItem(PAGE_NEXT_SLOT, pgNextItem);

            ItemStack pgPrevItem = InventoryHelper.generateHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmU4YzNjZTJhZWU2Y2YyZmFhZGU3ZGIzN2JiYWU3M2EzNjYyN2FjMTQ3M2ZlZjc1YjQxMGEwYWY5NzY1OWYifX19");
            ItemUtils.setName(pgPrevItem, ChatColor.GOLD + "" + ChatColor.BOLD + "<<< " + ChatColor.DARK_AQUA + "Пред. страница");
            inventory.setItem(PAGE_PREV_SLOT, pgPrevItem);
        }


    }

    public List<ItemStack> getCurrentInventoryItems() {
        List<ItemStack> items = new ArrayList<>();
        if(!watchingGames) {
            List<PlayerRatingSummary> summaries = Lists.newArrayList(Rating.getRatingSummaries());
            sort(summaries);
            for(PlayerRatingSummary summary : summaries) {
                ItemStack representingItem = summary.getRepresentingItem();
                items.add(representingItem);
            }
            return items;
        }
        if(watchingGameSummary == null) {
            List<GameSummary> summaries = Lists.newArrayList(Rating.getGameSummaries());

            //Sorting by date
            Comparator<GameSummary> comparator = Comparator.<GameSummary>comparingLong(summary -> summary.getDate().getTime()).reversed();
            summaries.sort(comparator);

            filter(summaries);
            for(GameSummary summary : summaries) {
                ItemStack representingItem = summary.getRepresentingItem();
                if(isOp())
                    ItemUtils.addLore(representingItem, ChatColor.RED + "ПКМ: Удалить");
                items.add(representingItem);
            }
        } else {
            List<PlayerSummary> summaries = Lists.newArrayList(watchingGameSummary.getPlayerSummaries());

            //Sorting by place
            Comparator<PlayerSummary> comparator = Comparator.comparingInt(PlayerSummary::getWinningPlace);
            summaries.sort(comparator);

            for(PlayerSummary summary : summaries) {
                items.add(summary.getRepresentingItem());
            }
        }
        return items;
    }

    private void sort(List<PlayerRatingSummary> summaries) {
        switch(sort) {
            case PLACE -> summaries.sort(Comparator.comparingInt(PlayerRatingSummary::getRatingPlace));
            case WINRATE -> summaries.sort(Comparator.comparingDouble(PlayerRatingSummary::getWinRate).reversed());
            case GAMES -> summaries.sort(Comparator.comparingInt(PlayerRatingSummary::getGamesPlayed).reversed());
            case KILLS -> summaries.sort(Comparator.comparingInt(PlayerRatingSummary::getOverallKills).reversed());
        }
    }

    private void filter(List<GameSummary> summaries) {
        if(filter == Filter.NONE) return;
        if(filter == Filter.PARTICIPANT) {
            summaries.removeIf(summary -> !summary.hasParticipated(player.getName()));
        }
        if(filter == Filter.WINNER) {
            summaries.removeIf(summary -> !summary.hasWon(player.getName()));
        }
    }

    private String getPagesInfo(int pages) {
        return ChatColor.DARK_GREEN + "" + ChatColor.BOLD + page + ChatColor.DARK_GRAY + " / " + ChatColor.DARK_GREEN + pages;
    }

    private enum Sort {
        PLACE("По месту в рейтинге", Material.COMPARATOR),
        WINRATE("По винрейту", Material.GOLD_INGOT),
        GAMES("По количеству игр", Material.GRASS_BLOCK),
        KILLS("По количеству убийств", Material.IRON_SWORD);

        private String description;
        private Material itemToShow;

        Sort(String description, Material itemToShow) {
            this.description = description;
            this.itemToShow = itemToShow;
        }
    }

    private enum Filter {
        NONE("Нет", Material.HOPPER),
        PARTICIPANT("Принято участие", Material.PLAYER_HEAD),
        WINNER("Победа", Material.GOLD_INGOT);

        private String description;
        private Material itemToShow;

        Filter(String description, Material itemToShow) {
            this.description = description;
            this.itemToShow = itemToShow;
        }

    }

}
