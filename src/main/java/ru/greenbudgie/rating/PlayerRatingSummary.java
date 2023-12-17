package ru.greenbudgie.rating;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.util.item.ItemUtils;

public class PlayerRatingSummary {

    private final String playerName;
    private int gamesPlayed;
    private int gamesWon;
    private double winRate;
    private int overallKills;
    private double averagePerformance;

    private int ratingPlace;
    private ItemStack representingItem;

    public PlayerRatingSummary(String playerName) {
        this.playerName = playerName;
    }

    public void generateRepresentingItem() {
        representingItem = ItemUtils.builder(ItemUtils.getHead(playerName)).
                withName(ChatColor.GOLD + playerName).
                withLore(
                        formatRatingPlace(),
                        formatAveragePerformance(),
                        formatGamesWins(),
                        formatWinRate(),
                        formatKills()
                ).build();
    }

    public String formatKills() {
        return ChatColor.RED + "Всего убийств" +
                ChatColor.GRAY + ": " +
                ChatColor.DARK_RED + ChatColor.BOLD + getOverallKills();
    }

    public String formatWinRate() {
        int winRate = (int) (getWinRate() * 100);
        return ChatColor.GRAY + "Винрейт: " +
                ChatColor.AQUA + ChatColor.BOLD + winRate +
                ChatColor.RESET + ChatColor.GRAY + "%";
    }

    public String formatGamesWins() {
        return ChatColor.GRAY + "Всего игр: " +
                ChatColor.DARK_AQUA + getGamesPlayed() +
                ChatColor.GRAY + ", побед: " +
                ChatColor.AQUA + ChatColor.BOLD + getGamesWon();
    }

    public String formatAveragePerformance() {
        int efficiency = (int) (getAveragePerformance() * 100);
        return ChatColor.GRAY + "Сред. эффективность: " +
                ChatColor.AQUA + ChatColor.BOLD + efficiency +
                ChatColor.RESET + ChatColor.GRAY + "%";
    }

    public String formatRatingPlace() {
        return ChatColor.GRAY + "Место в рейтинге: " + ChatColor.AQUA + ChatColor.BOLD + getRatingPlace();
    }

    public int getRatingPlace() {
        return ratingPlace;
    }

    public void setRatingPlace(int ratingPlace) {
        this.ratingPlace = ratingPlace;
    }

    public ItemStack getRepresentingItem() {
        return representingItem;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public int getOverallKills() {
        return overallKills;
    }

    public void setOverallKills(int overallKills) {
        this.overallKills = overallKills;
    }

    public double getAveragePerformance() {
        return averagePerformance;
    }

    public void setAveragePerformance(double averagePerformance) {
        this.averagePerformance = averagePerformance;
    }
}
