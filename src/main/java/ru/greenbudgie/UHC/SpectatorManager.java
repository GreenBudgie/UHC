package ru.greenbudgie.UHC;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.drop.Drops;
import ru.greenbudgie.event.SpectatorJoinEvent;
import ru.greenbudgie.lobby.Lobby;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.TaskManager;
import ru.greenbudgie.util.item.ItemUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public class SpectatorManager implements Listener {

    private static final String INVENTORY_TITLE = DARK_AQUA + "" + BOLD + "Меню Наблюдателя";
    private static final String MSG_OPEN_SPECTATOR_INVENTORY_INFO =
            DARK_GRAY + "" + BOLD + "> " + DARK_AQUA + "Нажми " + AQUA + BOLD + "ЛКМ" +
                    DARK_AQUA + " для доступа к меню наблюдателя";
    private static final String MSG_SPECTATOR_ACCESS_PLAYER_INVENTORY_INFO =
            DARK_GRAY + "" + BOLD + "> " + DARK_AQUA + "Нажми " + AQUA + BOLD + "ПКМ" +
                    DARK_AQUA + " по игроку в игре, чтобы посмотреть его инвентарь";
    private static final String INFO_LEFT_CLICK_TELEPORT = GOLD + "" + BOLD + "<ЛКМ>" + GRAY + " телепортироваться";
    private static final String INFO_RIGHT_CLICK_INVENTORY = GOLD + "" + BOLD + "<ПКМ>" + GRAY + " открыть инвентарь";
    private static final String MSG_CANNOT_VIEW_INVENTORY_PLAYER_OFFLINE =
            DARK_RED + "Игрок оффлайн, просмотр инвентаря невозможен";
    private static final String INFO_SPECTATOR_RULES = "За наблюдателя тебе доступна информация о дропах, игроках, " +
            "их здоровье, координатах и так далее. Эту информацию запрещено сообщать как другим игрокам, " +
            "так и своему тиммейту, если вы в команде!";

    private static final int INFORMATION_ITEM_RELATIVE_SLOT = 4;
    private static final ItemStack INFORMATION_ITEM = ItemUtils.builder(Material.PAPER)
            .withName(AQUA + "" + BOLD + "Напоминание")
            .withSplittedLore(GRAY + INFO_SPECTATOR_RULES)
            .build();

    private static final int TEAMMATE_ITEM_RELATIVE_SLOT = 3;
    private static final int TELEPORT_AIR_DROP_ITEM_RELATIVE_SLOT = 0;
    private static final ItemStack TELEPORT_AIR_DROP_ITEM = ItemUtils.builder(Material.PHANTOM_MEMBRANE)
            .withName(GRAY + "Телепортироваться к " + AQUA + BOLD + "Эирдропу")
            .build();
    private static final int TELEPORT_CAVE_DROP_ITEM_RELATIVE_SLOT = 1;
    private static final ItemStack TELEPORT_CAVE_DROP_ITEM = ItemUtils.builder(Material.STONE)
            .withName(GRAY + "Телепортироваться к " + DARK_GREEN + BOLD + "Кейвдропу")
            .build();
    private static final int TELEPORT_NETHER_DROP_ITEM_RELATIVE_SLOT = 2;
    private static final ItemStack TELEPORT_NETHER_DROP_ITEM = ItemUtils.builder(Material.OBSIDIAN)
            .withName(GRAY + "Телепортироваться к " + DARK_RED + BOLD + "Незердропу")
            .build();

    private static final int TELEPORT_OVERWORLD_ITEM_RELATIVE_SLOT = 5;
    private static final ItemStack TELEPORT_OVERWORLD_ITEM = ItemUtils.builder(Material.GRASS_BLOCK)
            .withName(GRAY + "Телепортироваться в " + GREEN + BOLD + "Обычный Мир")
            .build();
    private static final int TELEPORT_NETHER_ITEM_RELATIVE_SLOT = 6;
    private static final ItemStack TELEPORT_NETHER_ITEM = ItemUtils.builder(Material.NETHERRACK)
            .withName(GRAY + "Телепортироваться в " + RED + BOLD + "Ад")
            .build();
    private static final int TELEPORT_ARENA_ITEM_RELATIVE_SLOT = 7;
    private static final ItemStack TELEPORT_ARENA_ITEM = ItemUtils.builder(Material.DIAMOND_SWORD)
            .withName(GRAY + "Телепортироваться на " + AQUA + BOLD + "Арену")
            .build();
    private static final int RETURN_LOBBY_ITEM_RELATIVE_SLOT = 8;
    private static final ItemStack RETURN_LOBBY_ITEM = ItemUtils.builder(Material.DARK_OAK_DOOR)
            .withName(GRAY + "Вернуться в " + DARK_AQUA + BOLD + "Лобби")
            .build();

    private static final ItemStack FILLER_ITEM = ItemUtils.builder(Material.BLACK_STAINED_GLASS_PANE)
            .withName(" ")
            .build();

    private static final int MAX_LAST_PLAYER_SLOT = 35;
    private static final int SLOTS_IN_ROW = 9;

    private static final String PLAYER_NAME_NBT = "playerName";

    /**
     * Prepares player to become a spectator: clears inventory, heals, sets game mode e.t.c.
     * This method does not teleport the player and does not register it in player manager.
     */
    public static void preparePlayerToSpectate(Player player) {
        UHC.resetPlayer(player);
        player.setGameMode(GameMode.SPECTATOR);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                PotionEffect.INFINITE_DURATION,
                0,
                false,
                false)
        );
    }

    /**
     * Adds a lobby player to the game spectators if it is possible.
     * Teleports the player to the game and performs some additional work.
     */
    public static void addSpectatorFromLobby(Player player) {
        if (!UHC.playing) {
            player.sendMessage(RED + "" + BOLD + "- Игра не идет, наблюдать за ней не получится! -");
            return;
        }
        if (!Lobby.isInLobbyOrWatchingArena(player)) {
            player.sendMessage(RED + "" + BOLD + "- Ты сейчас не в лобби! -");
            return;
        }
        preparePlayerToSpectate(player);
        Location teleportLocation;
        if (UHC.state.isDeathmatch()) {
            teleportLocation = ArenaManager.getCurrentArena().getWorld().getSpawnLocation();
        } else {
            teleportLocation = WorldManager.spawnLocation;
        }
        SafeTeleport.performSafeTeleport(player, teleportLocation);
        PlayerManager.registerSpectator(player);
        UHC.refreshScoreboards();
        for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
            inGamePlayer.sendMessage(
                    DARK_AQUA + "" + BOLD + "+ " + GOLD + player.getName() + AQUA + " присоединился к наблюдателям"
            );
        }
    }

    private void openInventory(Player player) {
        List<UHCPlayer> players = PlayerManager.getAlivePlayers();
        int playersSlots = MathUtils.clamp(players.size() - 1, 0, MAX_LAST_PLAYER_SLOT);
        int fillerStartSlot = (playersSlots / SLOTS_IN_ROW + 1) * SLOTS_IN_ROW;
        int inventorySize = fillerStartSlot + (2 * SLOTS_IN_ROW);
        Inventory inventory = Bukkit.createInventory(player, inventorySize, INVENTORY_TITLE);
        for (int i = 0; i <= playersSlots; i++) {
            UHCPlayer uhcPlayer = players.get(i);
            inventory.setItem(i, getPlayerItem(player, uhcPlayer));
        }
        int fillUntil = fillerStartSlot + SLOTS_IN_ROW;
        for (int i = fillerStartSlot; i < fillUntil; i++) {
            inventory.setItem(i, FILLER_ITEM);
        }
        inventory.setItem(fillerStartSlot + INFORMATION_ITEM_RELATIVE_SLOT, INFORMATION_ITEM);

        putTeammateItem(inventory, player, fillUntil + TEAMMATE_ITEM_RELATIVE_SLOT);
        inventory.setItem(fillUntil + TELEPORT_AIR_DROP_ITEM_RELATIVE_SLOT, TELEPORT_AIR_DROP_ITEM);
        inventory.setItem(fillUntil + TELEPORT_CAVE_DROP_ITEM_RELATIVE_SLOT, TELEPORT_CAVE_DROP_ITEM);
        inventory.setItem(fillUntil + TELEPORT_NETHER_DROP_ITEM_RELATIVE_SLOT, TELEPORT_NETHER_DROP_ITEM);

        inventory.setItem(fillUntil + TELEPORT_OVERWORLD_ITEM_RELATIVE_SLOT, TELEPORT_OVERWORLD_ITEM);
        inventory.setItem(fillUntil + TELEPORT_NETHER_ITEM_RELATIVE_SLOT, TELEPORT_NETHER_ITEM);
        inventory.setItem(fillUntil + TELEPORT_ARENA_ITEM_RELATIVE_SLOT, TELEPORT_ARENA_ITEM);

        inventory.setItem(fillUntil + RETURN_LOBBY_ITEM_RELATIVE_SLOT, RETURN_LOBBY_ITEM);

        player.openInventory(inventory);
    }

    private void putTeammateItem(Inventory inventory, Player spectator, int slot) {
        if (!UHC.isDuo) {
            return;
        }
        UHCPlayer teammate = PlayerManager.getUHCTeammate(spectator);
        if (teammate == null) {
            return;
        }
        inventory.setItem(slot, getPlayerItem(spectator, teammate));
    }

    private ItemStack getPlayerItem(Player spectator, UHCPlayer player) {
        boolean isTeammates = PlayerManager.isTeammates(PlayerManager.asUHCPlayer(spectator), player);
        String separator = DARK_GRAY + " | ";
        String namePrefix = "";
        if (isTeammates) {
            namePrefix = LIGHT_PURPLE + "" + BOLD + "Тиммейт" + separator;
        }
        String playerName = namePrefix + GOLD + player.getNickname() +
                separator + RED + (int) Math.round(player.getRealOrOfflineHealth()) + DARK_RED + " ❤";
        return ItemUtils.builder(ItemUtils.getHead(player.getOfflinePlayer()))
                .withName(playerName)
                .ifFalse(player.isOnline()).withLore(DARK_RED + "" + BOLD + "Оффлайн")
                .withLore(
                        player.getSummary().formatOverallKills(),
                        INFO_LEFT_CLICK_TELEPORT,
                        INFO_RIGHT_CLICK_INVENTORY
                )
                .withValue(PLAYER_NAME_NBT, player.getNickname())
                .build();
    }

    private void handlePlayerHeadClick(Player player, ItemStack item, boolean isLeftClick) {
        String playerName = ItemUtils.getCustomValue(item, PLAYER_NAME_NBT);
        if (playerName == null) {
            return;
        }
        UHCPlayer uhcPlayer = PlayerManager.getPlayerByNickname(playerName);
        if (uhcPlayer == null) {
            return;
        }
        if (isLeftClick) {
            Location teleportLocation = uhcPlayer.getLocation();
            if (teleportLocation == null) {
                return;
            }
            SafeTeleport.performSafeTeleport(player, teleportLocation);
            return;
        }
        if (!uhcPlayer.isOnline()) {
            player.sendMessage(MSG_CANNOT_VIEW_INVENTORY_PLAYER_OFFLINE);
            return;
        }
        Player target = uhcPlayer.getPlayer();
        PlayerInventoryView.viewInventory(player, target);
    }

    private void handleClick(Player player, ItemStack item, boolean isLeftClick) {
        Material type = item.getType();

        if (type == Material.PLAYER_HEAD) {
            handlePlayerHeadClick(player, item, isLeftClick);
            return;
        }

        if (type == TELEPORT_AIR_DROP_ITEM.getType()) {
            SafeTeleport.performSafeTeleport(player, Drops.AIRDROP.getLocation());
            return;
        }
        if (type == TELEPORT_CAVE_DROP_ITEM.getType()) {
            SafeTeleport.performSafeTeleport(player, Drops.CAVEDROP.getLocation());
            return;
        }
        if (type == TELEPORT_NETHER_DROP_ITEM.getType()) {
            SafeTeleport.performSafeTeleport(player, Drops.NETHERDROP.getLocation());
            return;
        }
        if (type == TELEPORT_OVERWORLD_ITEM.getType()) {
            SafeTeleport.performSafeTeleport(player, WorldManager.getGameMap().getSpawnLocation());
            return;
        }
        if (type == TELEPORT_NETHER_ITEM.getType()) {
            SafeTeleport.performSafeTeleport(player, WorldManager.getGameMapNether().getSpawnLocation());
            return;
        }
        if (type == TELEPORT_ARENA_ITEM.getType()) {
            SafeTeleport.performSafeTeleport(player, ArenaManager.getCurrentArena().getWorld().getSpawnLocation());
        }
        if (type == RETURN_LOBBY_ITEM.getType()) {
            Lobby.returnPlayerToLobby(player);
        }
    }

    private final Set<Player> ignoreInventoryOpen = new HashSet<>();

    private void ignoreInventoryOpen(Player player) {
        ignoreInventoryOpen.add(player);
        TaskManager.invokeLater(() -> ignoreInventoryOpen.remove(player));
    }

    private boolean shouldIgnoreInventoryOpen(Player player) {
        return ignoreInventoryOpen.contains(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void openPlayerInventory(PlayerInteractEntityEvent event) {
        if (!UHC.state.isGameActive()) {
            return;
        }
        Player player = event.getPlayer();
        if (!PlayerManager.isSpectator(player)) {
            return;
        }
        ignoreInventoryOpen(player);
        if(event.getHand() == EquipmentSlot.HAND && event.getRightClicked() instanceof Player clicked) {
            if(PlayerManager.isPlaying(clicked)) {
                PlayerInventoryView.viewInventory(player, clicked);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void spectatorJoin(SpectatorJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(
                MSG_OPEN_SPECTATOR_INVENTORY_INFO,
                MSG_SPECTATOR_ACCESS_PLAYER_INVENTORY_INFO
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void openSpectatorInventory(PlayerAnimationEvent event) {
        if (event.getAnimationType() != PlayerAnimationType.ARM_SWING) {
            return;
        }
        Player player = event.getPlayer();
        if (shouldIgnoreInventoryOpen(player)) {
            return;
        }
        if (!PlayerManager.isSpectator(player)) {
            return;
        }
        openInventory(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void ignoreInventoryWhenRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        Player player = event.getPlayer();
        if (!PlayerManager.isSpectator(player)) {
            return;
        }
        ignoreInventoryOpen(player);
    }

    @EventHandler
    public void inventoryInteract(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!INVENTORY_TITLE.equals(title)) {
            return;
        }
        event.setCancelled(true);
        Inventory inventory = event.getView().getTopInventory();
        if (event.getClickedInventory() != inventory) {
            return;
        }
        ClickType clickType = event.getClick();
        if (!clickType.isLeftClick() && !clickType.isRightClick()) {
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        handleClick((Player) event.getWhoClicked(), item, clickType.isLeftClick());
    }

}
