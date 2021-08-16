package ru.lobby;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LobbyTeamBuilder {

    private static final String PREFIX =    ChatColor.DARK_GRAY + "[" +
                                            ChatColor.DARK_PURPLE + ChatColor.BOLD + "Дуо" +
                                            ChatColor.RESET + ChatColor.DARK_GRAY + "] ";
    private static List<Request> requests = new ArrayList<>();
    private static List<Team> teams = new ArrayList<>();

    public static Player getOutgoingRequest(Player sender) {
        for(Request request : requests) {
            if(request.sender() == sender) return request.receiver();
        }
        return null;
    }

    public static boolean hasOutgoingRequest(Player sender) {
        return getOutgoingRequest(sender) != null;
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
        if(!hasActiveRequestTo(sender, receiver)) {
            requests.add(new Request(sender, receiver));
            sender.sendMessage(PREFIX +
                    ChatColor.DARK_GREEN + ChatColor.BOLD + "Запрос отправлен " +
                    ChatColor.RESET + ChatColor.GOLD + receiver.getName());
            sender.playSound(sender.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5F, 1.5F);
            receiver.sendMessage(PREFIX +
                    ChatColor.DARK_GREEN + ChatColor.BOLD + "Входящий запрос от " +
                    ChatColor.RESET + ChatColor.GOLD + sender.getName());
            receiver.playSound(receiver.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.7F, 1F);
        } else {
            sender.sendMessage(PREFIX +
                    ChatColor.RED + "Запрос к " +
                    ChatColor.GOLD + receiver.getName() +
                    ChatColor.RED + " уже отправлен!");
            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 1F);
        }
    }

    public static void cancelRequest(Player sender, Player receiver) {
        if(hasActiveRequestTo(sender, receiver)) {
            sender.sendMessage(PREFIX +
                    ChatColor.GREEN + "Запрос к " +
                    ChatColor.GOLD + receiver.getName() +
                    ChatColor.GREEN + " отменен");
            requests.removeIf(request -> request.receiver() == receiver && request.sender() == sender);
        }
    }

    public static void acceptIncomingRequest(Player receiver, Player toAccept) {
        if(hasActiveRequestTo(toAccept, receiver)) {
            receiver.sendMessage(PREFIX +
                    ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Теперь ты союзник с " +
                    ChatColor.RESET + ChatColor.GOLD + toAccept.getName());
            toAccept.sendMessage(PREFIX +
                    ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Теперь ты союзник с " +
                    ChatColor.RESET + ChatColor.GOLD + receiver.getName());
            disbandTeam(toAccept);
            disbandTeam(receiver);
            teams.add(new Team(toAccept, receiver));
            requests.removeIf(request -> request.receiver() == receiver && request.sender() == toAccept);
        }
    }

    public static void declineIncomingRequest(Player receiver, Player toDecline) {
        if(hasActiveRequestTo(toDecline, receiver)) {
            receiver.sendMessage(PREFIX +
                    ChatColor.YELLOW + "Запрос от " +
                    ChatColor.GOLD + toDecline.getName() +
                    ChatColor.YELLOW + " отклонен");
            toDecline.sendMessage(PREFIX +
                    ChatColor.GOLD + receiver.getName() +
                    ChatColor.RED + " отклонил запрос");
            requests.removeIf(request -> request.receiver() == receiver && request.sender() == toDecline);
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
