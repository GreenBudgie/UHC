package ru.lobby;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ru.UHC.UHC;
import ru.UHC.WorldManager;
import ru.main.UHCPlugin;
import ru.util.InventoryHelper;
import ru.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class LobbyTeamBuilder implements Listener {

    public static final String PREFIX =    ChatColor.DARK_GRAY + "[" +
                                            ChatColor.DARK_PURPLE + ChatColor.BOLD + "Дуо" +
                                            ChatColor.RESET + ChatColor.DARK_GRAY + "] ";
    private static List<Request> requests = new ArrayList<>();
    private static List<Team> teams = new ArrayList<>();

    private static final String requestSendInventoryName = PREFIX + ChatColor.GOLD + "Отправить запрос";
    private static final String requestAcceptInventoryName = PREFIX + ChatColor.GREEN + "Принять запрос";

    private static final ItemStack requestSendSwitchItem =
            ItemUtils.builder(Material.WRITABLE_BOOK).withName(ChatColor.GOLD + "Отправить запрос").build();
    private static final ItemStack requestAcceptSwitchItem =
            ItemUtils.builder(Material.BELL).withName(ChatColor.GREEN + "Посмотреть запросы").build();
    private static final ItemStack teamDisbandItem =
            ItemUtils.builder(Material.BARRIER).withName(ChatColor.RED + "" + ChatColor.BOLD + "Покинуть команду").build();

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
                    disbandTeam(player);
                }
            }
            event.setCancelled(true);
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
                    disbandTeam(player);
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void updateOnQuit(PlayerQuitEvent event) {
        disbandTeam(event.getPlayer());
    }

    @EventHandler
    public void updateOnJoin(PlayerJoinEvent event) {
        reopenInventories();
    }

    public static void openRequestAcceptInventory(Player player) {
        if(UHC.playing) return;
        List<Player> incomingRequests = getIncomingRequests(player);
        int inventorySize = (((incomingRequests.size() - 1) / 9) + 1) * 9 + 9;
        Inventory inventory = Bukkit.createInventory(player, inventorySize, requestAcceptInventoryName);
        for(Player sender : incomingRequests) {
            ItemStack head = ItemUtils.getHead(sender);
            ItemUtils.setName(head, ChatColor.GOLD + sender.getName());
            ItemUtils.addLore(head,
                    ChatColor.GRAY + "<" +
                            ChatColor.YELLOW + ChatColor.BOLD + "ЛКМ" +
                            ChatColor.RESET + ChatColor.GRAY + "> " +
                            ChatColor.GREEN + "Принять",

                            ChatColor.GRAY + "<" +
                            ChatColor.YELLOW + ChatColor.BOLD + "ПКМ" +
                            ChatColor.RESET + ChatColor.GRAY + "> " +
                            ChatColor.RED + "Отклонить");
            inventory.addItem(head);
        }
        inventory.setItem(inventorySize - 4, requestSendSwitchItem);
        inventory.setItem(inventorySize - 6, ItemUtils.addGlow(requestAcceptSwitchItem.clone()));
        placeAdditionalItems(player, inventorySize, inventory);
        player.openInventory(inventory);
    }

    public static void openRequestSendInventory(Player player) {
        if(UHC.playing) return;
        List<Player> players = Lobby.getLobby().getPlayers();
        players.removeIf(currentPlayer -> currentPlayer == player || hasActiveRequestTo(player, currentPlayer));
        int inventorySize = (((players.size() - 1) / 9) + 1) * 9 + 9;
        Inventory inventory = Bukkit.createInventory(player, inventorySize, requestSendInventoryName);
        for(Player currentPlayer : players) {
            ItemStack head = ItemUtils.getHead(currentPlayer);
            ItemUtils.setName(head, ChatColor.GOLD + currentPlayer.getName());
            ItemUtils.addLore(head,
                    ChatColor.GRAY + "<" +
                            ChatColor.YELLOW + ChatColor.BOLD + "ЛКМ" +
                            ChatColor.RESET + ChatColor.GRAY + "> " +
                            ChatColor.GREEN + "Отправить запрос");
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
            ItemUtils.setName(teammateHead, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Тиммейт: " +
                    ChatColor.RESET + ChatColor.GOLD + teammate.getName());
            teammateHead = ItemUtils.setCustomValue(teammateHead, "teammate", "true");
            inventory.setItem(inventorySize - 5, teammateHead);
        }
    }

    public static void reopenInventory(Player player) {
        if(player.getOpenInventory().getTitle().equals(requestSendInventoryName)) openRequestSendInventory(player);
        if(player.getOpenInventory().getTitle().equals(requestAcceptInventoryName)) openRequestAcceptInventory(player);
    }

    public static void reopenInventories() {
        for(Player player : Lobby.getLobby().getPlayers()) {
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
                    ChatColor.DARK_GREEN + ChatColor.BOLD + "Запрос отправлен " +
                    ChatColor.RESET + ChatColor.GOLD + receiver.getName());
            sender.playSound(sender.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5F, 1.5F);

            TextComponent acceptButton = new TextComponent(ChatColor.GRAY + "<" +
                            ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + "Принять - ЛКМ" +
                            ChatColor.RESET + ChatColor.GRAY + ">");
            acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teammate accept " + sender.getName()));
            acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GOLD + "Нажми, чтобы принять")));

            receiver.spigot().sendMessage(new TextComponent(PREFIX +
                    ChatColor.DARK_GREEN + ChatColor.BOLD + "Запрос от " +
                    ChatColor.RESET + ChatColor.GOLD + sender.getName() + " "),
                    acceptButton);
            receiver.playSound(receiver.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.7F, 1F);
            reopenInventory(sender);
            reopenInventory(receiver);
        } else {
            sender.sendMessage(PREFIX +
                    ChatColor.RED + "Запрос к " +
                    ChatColor.GOLD + receiver.getName() +
                    ChatColor.RED + " уже отправлен!");
            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 1F);
        }
    }

    public static void acceptIncomingRequest(Player receiver, Player toAccept) {
        if(UHC.playing) return;
        if(hasActiveRequestTo(toAccept, receiver)) {
            receiver.sendMessage(PREFIX +
                    ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Теперь ты союзник с " +
                    ChatColor.RESET + ChatColor.GOLD + toAccept.getName());
            toAccept.sendMessage(PREFIX +
                    ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Теперь ты союзник с " +
                    ChatColor.RESET + ChatColor.GOLD + receiver.getName());
            receiver.playSound(receiver.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.5F, 1F);
            toAccept.playSound(toAccept.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.5F, 1F);
            disbandTeam(toAccept);
            disbandTeam(receiver);
            teams.add(new Team(toAccept, receiver));
            requests.removeIf(request ->
                            request.receiver() == receiver ||
                            request.sender() == toAccept ||
                            request.receiver() == toAccept ||
                            request.sender() == receiver);
            reopenInventory(toAccept);
            reopenInventory(receiver);
            UHC.refreshLobbyScoreboard();
        }
    }

    public static void declineIncomingRequest(Player receiver, Player toDecline) {
        if(UHC.playing) return;
        if(hasActiveRequestTo(toDecline, receiver)) {
            receiver.sendMessage(PREFIX +
                    ChatColor.YELLOW + "Запрос от " +
                    ChatColor.GOLD + toDecline.getName() +
                    ChatColor.YELLOW + " отклонен");
            toDecline.sendMessage(PREFIX +
                    ChatColor.GOLD + receiver.getName() +
                    ChatColor.RED + " отклонил запрос");
            toDecline.playSound(toDecline.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
            requests.removeIf(request -> request.receiver() == receiver && request.sender() == toDecline);
            reopenInventory(toDecline);
            reopenInventory(receiver);
        }
    }

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

    public static void disbandTeam(Player member) {
        if(UHC.playing) return;
        for(Team team : teams) {
            if(team.player1() == member || team.player2() == member) {
                team.player1().sendMessage(PREFIX +
                        ChatColor.DARK_RED + ChatColor.BOLD + "Ты и " +
                        ChatColor.RESET + ChatColor.GOLD + team.player2().getName() +
                        ChatColor.DARK_RED + ChatColor.BOLD + " больше не союзники");
                team.player2().sendMessage(PREFIX +
                        ChatColor.DARK_RED + ChatColor.BOLD + "Ты и " +
                        ChatColor.RESET + ChatColor.GOLD + team.player1().getName() +
                        ChatColor.DARK_RED + ChatColor.BOLD + " больше не союзники");
                team.player1().playSound(team.player1().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
                team.player2().playSound(team.player2().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
                break;
            }
        }
        teams.removeIf(team -> team.player1() == member || team.player2() == member);
        reopenInventories();
        UHC.refreshLobbyScoreboard();
    }

    public static boolean isTeammates(Player player1, Player player2) {
        for(Team team : teams) {
            if((team.player1() == player1 && team.player1() == player2) ||
                    (team.player1() == player2 && team.player1() == player1)) return true;
        }
        return false;
    }

    private record Request(Player sender, Player receiver) {}
    private record Team(Player player1, Player player2) {}

}
