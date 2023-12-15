package ru.greenbudgie.lobby;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.PlayerOptionHolder;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.event.AfterGameEndEvent;
import ru.greenbudgie.lobby.game.LobbyGameManager;
import ru.greenbudgie.lobby.game.arena.PvpArenaLeaveEvent;
import ru.greenbudgie.lobby.game.parkour.LobbyParkourLeaveEvent;
import ru.greenbudgie.util.ItemUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.bukkit.ChatColor.*;

public class LobbyTeamBuilder implements Listener {

    public static final String PREFIX =    DARK_GRAY + "[" +
                                            DARK_PURPLE + BOLD + "Дуо" +
                                            RESET + DARK_GRAY + "] ";
    private static final List<Request> requests = new ArrayList<>();
    private static final List<Team> teams = new ArrayList<>();

    private static final String requestSendInventoryName = PREFIX + GOLD + "Отправить запрос";
    private static final String requestAcceptInventoryName = PREFIX + GREEN + "Принять запрос";

    private static final ItemStack requestSendSwitchItem =
            ItemUtils.builder(Material.WRITABLE_BOOK).withName(GOLD + "Отправить запрос").build();
    private static final ItemStack requestAcceptSwitchItem =
            ItemUtils.builder(Material.BELL).withName(GREEN + "Посмотреть запросы").build();
    private static final ItemStack teamDisbandItem =
            ItemUtils.builder(Material.BARRIER).withName(RED + "" + BOLD + "Покинуть команду").build();

    private static final String TEAMMATE_NBT_KEY = "teammateSelector";
    private static final ItemStack selectTeammateItem = ItemUtils.builder(Material.BELL)
            .withName(LIGHT_PURPLE + "" + BOLD + "Выбрать тиммейта")
            .withValue(TEAMMATE_NBT_KEY, "true")
            .build();
    private static final int SELECT_TEAMMATE_ITEM_SLOT = 4;

    public static void init() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            restoreTeammate(player, false);
        }
    }

    private static void restoreTeammate(Player player, boolean message) {
        String savedTeammateName = PlayerOptionHolder.getLobbyTeammateName(player.getName());
        if(savedTeammateName != null) {
            Player teammate = Bukkit.getPlayer(savedTeammateName);
            if(teammate != null && teammate.isOnline() && !hasTeammate(teammate)) {
                makeTeam(player, teammate, false, message);
            } else {
                PlayerOptionHolder.removeLobbyTeammate(player.getName());
            }
        }
    }

    public static int getTeamNumber() {
        int teamNumber = teams.size();
        for(Player player : Lobby.getPlayersInLobbyAndArenas()) {
            if(!hasTeammate(player)) teamNumber++;
        }
        return teamNumber;
    }

    public static void giveOrRemoveTeammateSelectItems() {
        if (UHC.isDuo) {
            for (Player player : Lobby.getPlayersInLobbyAndArenas()) {
                giveTeammateSelectItemIfNeeded(player);
            }
            return;
        }
        for (Player player : Lobby.getPlayersInLobbyAndArenas()) {
            removeTeammateSelectItem(player);
        }
    }

    private static void updateTeammateSelectItemIfNeeded(Player player) {
        if (!UHC.isDuo) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (isTeammateSelector(item)) {
                inventory.setItem(i, getTeammateSelectItem(player));
                return;
            }
        }
    }

    private static void removeTeammateSelectItem(Player player) {
        PlayerInventory inventory = player.getInventory();
        for (ItemStack item : inventory) {
            if (isTeammateSelector(item)) {
                inventory.remove(item);
                return;
            }
        }
    }

    private static void giveTeammateSelectItemIfNeeded(Player player) {
        if (!UHC.isDuo) {
            return;
        }
        if (LobbyGameManager.isParticipating(player)) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        for (ItemStack item : inventory.getContents()) {
            if (isTeammateSelector(item)) {
                return;
            }
        }
        player.getInventory().setItem(SELECT_TEAMMATE_ITEM_SLOT, getTeammateSelectItem(player));
    }

    private static ItemStack getTeammateSelectItem(Player player) {
        Player teammate = getTeammate(player);
        if (teammate == null) {
            return selectTeammateItem;
        }
        return ItemUtils.builder(ItemUtils.getHead(teammate))
                .withName(LIGHT_PURPLE + "" + BOLD + "Тиммейт" + DARK_GRAY + ": " + GOLD + teammate.getName())
                .withValue(TEAMMATE_NBT_KEY, "true")
                .build();
    }

    private static boolean isTeammateSelector(ItemStack item) {
        if (item == null) return false;
        return ItemUtils.hasCustomValue(item, TEAMMATE_NBT_KEY);
    }

    @EventHandler
    public void giveItem(PvpArenaLeaveEvent event) {
        giveTeammateSelectItemIfNeeded(event.getPlayer());
    }

    @EventHandler
    public void giveItem(LobbyParkourLeaveEvent event) {
        giveTeammateSelectItemIfNeeded(event.getPlayer());
    }

    @EventHandler
    public void giveItem(SpectatorReturnToLobbyEvent event) {
        giveTeammateSelectItemIfNeeded(event.getPlayer());
    }

    @EventHandler
    public void giveItem(AfterGameEndEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            giveTeammateSelectItemIfNeeded(player);
        }
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if(event.getView().getTitle().equals(requestSendInventoryName)) {
            if(item != null) {
                if(item.getType() == Material.PLAYER_HEAD && !ItemUtils.hasCustomValue(item, "teammate")) {
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    if(meta != null && meta.getOwningPlayer() != null && meta.getOwningPlayer().getPlayer() != null) {
                        Player clickedPlayer = meta.getOwningPlayer().getPlayer();
                        makeRequest(player, clickedPlayer);
                    }
                }
                if(item.getType() == Material.BELL) {
                    openRequestAcceptInventory(player);
                }
                if(item.getType() == Material.BARRIER) {
                    disbandTeam(player, true, true);
                }
            }
            event.setCancelled(true);
            return;
        }
        if(event.getView().getTitle().equals(requestAcceptInventoryName)) {
            if(item != null) {
                if(item.getType() == Material.PLAYER_HEAD && !ItemUtils.hasCustomValue(item, "teammate")) {
                    SkullMeta meta = (SkullMeta) item.getItemMeta();
                    if(meta != null && meta.getOwningPlayer() != null && meta.getOwningPlayer().getPlayer() != null) {
                        Player clickedPlayer = meta.getOwningPlayer().getPlayer();
                        boolean accept = !event.isRightClick();
                        if(accept) {
                            acceptIncomingRequest(player, clickedPlayer);
                        } else {
                            declineIncomingRequest(player, clickedPlayer);
                        }
                    }
                }
                if(item.getType() == Material.WRITABLE_BOOK) {
                    openRequestSendInventory(player);
                }
                if(item.getType() == Material.BARRIER) {
                    disbandTeam(player, true, true);
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void updateOnQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        disbandTeam(player, false, false);
        requests.removeIf(request -> request.sender() == player || request.receiver() == player);
        teams.removeIf(team -> team.player1() == player || team.player2() == player);
    }

    @EventHandler
    public void updateOnJoin(PlayerJoinEvent event) {
        if (UHC.playing) {
            return;
        }
        Player player = event.getPlayer();
        restoreTeammate(player, true);
        reopenInventories();
        if (UHC.isDuo) {
            giveTeammateSelectItemIfNeeded(player);
        } else {
            removeTeammateSelectItem(player);
        }
    }

    @EventHandler
    public void useTeammateSelectItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!UHC.isDuo || !Lobby.isInLobbyOrWatchingArena(player)) {
            return;
        }
        if (isTeammateSelector(event.getItem())) {
            openRequestSendInventory(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void pluginDisable(PluginDisableEvent event) {
        for (Player player : Lobby.getPlayersInLobbyAndArenas()) {
            removeTeammateSelectItem(player);
        }
    }

    public static void openRequestAcceptInventory(Player player) {
        if(UHC.playing) return;
        List<Player> incomingRequests = getIncomingRequests(player);
        int inventorySize = (((incomingRequests.size() - 1) / 9) + 1) * 9 + 9;
        Inventory inventory = Bukkit.createInventory(player, inventorySize, requestAcceptInventoryName);
        for(Player sender : incomingRequests) {
            ItemStack head = ItemUtils.getHead(sender);
            ItemUtils.setName(head, GOLD + sender.getName());
            ItemUtils.addLore(head,
                    GRAY + "<" +
                            YELLOW + BOLD + "ЛКМ" +
                            RESET + GRAY + "> " +
                            GREEN + "Принять",

                            GRAY + "<" +
                            YELLOW + BOLD + "ПКМ" +
                            RESET + GRAY + "> " +
                            RED + "Отклонить");
            inventory.addItem(head);
        }
        inventory.setItem(inventorySize - 4, requestSendSwitchItem);
        inventory.setItem(inventorySize - 6, ItemUtils.addGlow(requestAcceptSwitchItem.clone()));
        placeAdditionalItems(player, inventorySize, inventory);
        player.openInventory(inventory);
    }

    public static void openRequestSendInventory(Player player) {
        if(UHC.playing) return;
        List<Player> players = Lobby.getPlayersInLobbyAndArenas();
        players.removeIf(currentPlayer -> currentPlayer == player || hasActiveRequestTo(player, currentPlayer));
        int inventorySize = (((players.size() - 1) / 9) + 1) * 9 + 9;
        Inventory inventory = Bukkit.createInventory(player, inventorySize, requestSendInventoryName);
        for(Player currentPlayer : players) {
            ItemStack head = ItemUtils.getHead(currentPlayer);
            ItemUtils.setName(head, GOLD + currentPlayer.getName());
            ItemUtils.addLore(head,
                    GRAY + "<" +
                            YELLOW + BOLD + "ЛКМ" +
                            RESET + GRAY + "> " +
                            GREEN + "Отправить запрос");
            inventory.addItem(head);
        }
        inventory.setItem(inventorySize - 4, ItemUtils.addGlow(requestSendSwitchItem.clone()));
        inventory.setItem(inventorySize - 6, requestAcceptSwitchItem);
        placeAdditionalItems(player, inventorySize, inventory);
        player.openInventory(inventory);
    }

    private static void placeAdditionalItems(Player player, int inventorySize, Inventory inventory) {
        if(hasTeammate(player)) {
            inventory.setItem(inventorySize - 1, teamDisbandItem);
            Player teammate = getTeammate(player);
            ItemStack teammateHead = ItemUtils.getHead(getTeammate(player));
            ItemUtils.setName(teammateHead, LIGHT_PURPLE + "" + BOLD + "Тиммейт " +
                    GRAY + ": " +
                    RESET + GOLD + teammate.getName());
            teammateHead = ItemUtils.setCustomValue(teammateHead, "teammate", "true");
            inventory.setItem(inventorySize - 5, teammateHead);
        }
    }

    public static void reopenInventory(Player player) {
        if(player.getOpenInventory().getTitle().equals(requestSendInventoryName)) openRequestSendInventory(player);
        if(player.getOpenInventory().getTitle().equals(requestAcceptInventoryName)) openRequestAcceptInventory(player);
    }

    public static void reopenInventories() {
        for(Player player : Lobby.getPlayersInLobbyAndArenas()) {
            reopenInventory(player);
        }
    }

    public static List<Player> getIncomingRequests(Player receiver) {
        List<Player> incoming = new ArrayList<>();
        for(Request request : requests) {
            if(request.receiver() == receiver) incoming.add(request.sender());
        }
        return incoming;
    }

    public static boolean hasActiveRequestTo(Player sender, Player receiver) {
        return getIncomingRequests(receiver).contains(sender);
    }

    public static void makeRequest(Player sender, Player receiver) {
        if(UHC.playing) return;
        if(!hasActiveRequestTo(sender, receiver)) {
            requests.add(new Request(sender, receiver));
            sender.sendMessage(PREFIX +
                    DARK_GREEN + BOLD + "Запрос отправлен " +
                    RESET + GOLD + receiver.getName());
            sender.playSound(sender.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5F, 1.5F);

            TextComponent acceptButton = new TextComponent(GRAY + "<" +
                            LIGHT_PURPLE + BOLD + "Принять - ЛКМ" +
                            RESET + GRAY + ">");
            acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teammate accept " + sender.getName()));
            acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(GOLD + "Нажми, чтобы принять")));

            receiver.spigot().sendMessage(new TextComponent(PREFIX +
                    DARK_GREEN + BOLD + "Запрос от " +
                    RESET + GOLD + sender.getName() + " "),
                    acceptButton);
            receiver.playSound(receiver.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.7F, 1F);
            reopenInventory(sender);
            reopenInventory(receiver);
        } else {
            sender.sendMessage(PREFIX +
                    RED + "Запрос к " +
                    GOLD + receiver.getName() +
                    RED + " уже отправлен!");
            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 1F);
        }
    }

    public static void acceptIncomingRequest(Player receiver, Player toAccept) {
        if(UHC.playing) return;
        if(hasActiveRequestTo(toAccept, receiver)) {
            makeTeam(toAccept, receiver, true, true);
        }
    }

    private static void makeTeam(Player player1, Player player2, boolean sound, boolean message) {
        if(player1 == player2) return;
        if(message && !PlayerManager.isInGame(player1) && !PlayerManager.isInGame(player2)) {
            player1.sendMessage(PREFIX +
                    LIGHT_PURPLE + BOLD + "Теперь ты союзник с " +
                    RESET + GOLD + player2.getName());
            player2.sendMessage(PREFIX +
                    LIGHT_PURPLE + BOLD + "Теперь ты союзник с " +
                    RESET + GOLD + player1.getName());
        }
        if(sound && !PlayerManager.isInGame(player1) && !PlayerManager.isInGame(player2)) {
            player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.5F, 1F);
            player2.playSound(player2.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.5F, 1F);
        }
        disbandTeam(player2, true, false);
        disbandTeam(player1, true, false);
        PlayerOptionHolder.saveLobbyTeammates(player1.getName(), player2.getName());
        teams.add(new Team(player2, player1));
        requests.removeIf(request ->
                request.receiver() == player1 ||
                        request.sender() == player2 ||
                        request.receiver() == player2 ||
                        request.sender() == player1);
        reopenInventory(player2);
        reopenInventory(player1);
        UHC.refreshLobbyScoreboard();
        updateTeammateSelectItemIfNeeded(player1);
        updateTeammateSelectItemIfNeeded(player2);
    }

    public static void declineIncomingRequest(Player receiver, Player toDecline) {
        if(UHC.playing) return;
        if(hasActiveRequestTo(toDecline, receiver)) {
            receiver.sendMessage(PREFIX +
                    YELLOW + "Запрос от " +
                    GOLD + toDecline.getName() +
                    YELLOW + " отклонен");
            toDecline.sendMessage(PREFIX +
                    GOLD + receiver.getName() +
                    RED + " отклонил запрос");
            toDecline.playSound(toDecline.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
            requests.removeIf(request -> request.receiver() == receiver && request.sender() == toDecline);
            reopenInventory(toDecline);
            reopenInventory(receiver);
        }
    }

    @Nullable
    public static Player getTeammate(Player player) {
        for(Team team : teams) {
            if(team.player1() == player) return team.player2();
            if(team.player2() == player) return team.player1();
        }
        return null;
    }

    public static boolean hasTeammate(Player player) {
        return getTeammate(player) != null;
    }

    public static void disbandTeam(Player member, boolean effect, boolean removeSavedOption) {
        if(UHC.playing) return;
        Optional<Team> teamOptional = teams.stream()
                .filter(team -> team.player1() == member || team.player2() == member)
                .findFirst();
        if (teamOptional.isEmpty()) {
            return;
        }
        Team team = teamOptional.get();
        Player player1 = team.player1();
        Player player2 = team.player2();
        if(effect && !PlayerManager.isInGame(player1) && !PlayerManager.isInGame(player2)) {
            player1.sendMessage(PREFIX +
                    DARK_RED + BOLD + "Ты и " +
                    RESET + GOLD + player2.getName() +
                    DARK_RED + BOLD + " больше не союзники");
            player2.sendMessage(PREFIX +
                    DARK_RED + BOLD + "Ты и " +
                    RESET + GOLD + player1.getName() +
                    DARK_RED + BOLD + " больше не союзники");
            player1.playSound(player1.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
            player2.playSound(player2.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
        }
        if(removeSavedOption) {
            PlayerOptionHolder.removeLobbyTeammate(player1.getName());
            PlayerOptionHolder.removeLobbyTeammate(player2.getName());
        }
        teams.remove(team);
        updateTeammateSelectItemIfNeeded(player1);
        updateTeammateSelectItemIfNeeded(player2);
        reopenInventories();
        UHC.refreshLobbyScoreboard();
    }

    private record Request(Player sender, Player receiver) {}
    private record Team(Player player1, Player player2) {}

}
