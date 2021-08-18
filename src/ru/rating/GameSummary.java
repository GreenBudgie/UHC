package ru.rating;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.util.ItemUtils;
import ru.util.NumericalCases;

import java.text.SimpleDateFormat;
import java.util.*;

public class GameSummary implements ConfigurationSerializable {

    private Date date = new Date();
    private boolean isRatingGame = false;
    private boolean isDuo = false;
    private int durationMinutes = 0;
    private List<PlayerSummary> playerSummaries = new ArrayList<>();

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("date", getDate().getTime());
        serialized.put("isRatingGame", isRatingGame());
        serialized.put("isDuo", isDuo());
        serialized.put("durationMinutes", getDurationMinutes());
        List<Map<String, Object>> summaries = new ArrayList<>();
        for(PlayerSummary summary : getPlayerSummaries()) {
            summaries.add(summary.serialize());
        }
        serialized.put("players", summaries);
        return serialized;
    }

    @SuppressWarnings("unchecked")
    public static GameSummary deserialize(Map<String, Object> input) {
        GameSummary summary = new GameSummary();
        summary.setDate(new Date((long) input.getOrDefault("date", 0)));
        summary.setRatingGame((boolean) input.getOrDefault("isRatingGame", false));
        summary.setDuo((boolean) input.getOrDefault("isDuo", false));
        summary.setDurationMinutes((int) input.getOrDefault("durationMinutes", 0));
        List<Map<?, ?>> summaries = (List<Map<?, ?>>) input.getOrDefault("players", new ArrayList<>());
        for(Map<?, ?> serializedSummary : summaries) {
            summary.getPlayerSummaries().add(PlayerSummary.deserialize((Map<String, Object>) serializedSummary));
        }
        return summary;
    }

    public ItemStack getRepresentingItem() {
        return ItemUtils.builder(Material.WRITABLE_BOOK).
                withName(formatTitle()).
                withLore(
                        formatIsRatingGame(),
                        formatIsDuo(),
                        formatWinners(),
                        formatDuration(),
                        formatPlayerNumber()).
                withValue("date", String.valueOf(getDate().getTime())).
                build();
    }

    public String formatPlayerNumber() {
        String participateText = new NumericalCases(
                "Принимал",
                "Принимало",
                "Принимало").
                byNumber(getPlayerSummaries().size());
        String humanText = new NumericalCases(
                "человек",
                "человека",
                "человек").
                byNumber(getPlayerSummaries().size());
        return ChatColor.GRAY + participateText + " участие " +
                ChatColor.DARK_AQUA + ChatColor.BOLD + getPlayerSummaries().size() + " " +
                ChatColor.RESET + ChatColor.GRAY + humanText;
    }

    public String formatDuration() {
        String minutesText = new NumericalCases(
                "минуту",
                "минуты",
                "минут").
                byNumber(getDurationMinutes());
        return ChatColor.GRAY + "Игра длилась " +
                ChatColor.DARK_AQUA + ChatColor.BOLD + getDurationMinutes() + " " +
                ChatColor.RESET + ChatColor.GRAY + minutesText;
    }

    public String formatWinners() {
        List<PlayerSummary> winners = getWinnersSummaries();
        if(winners.isEmpty()) return ChatColor.RED + "Нет победителей";
        if(winners.size() == 1) return
                ChatColor.YELLOW + "Победитель" +
                ChatColor.GRAY + ": " +
                ChatColor.GOLD + winners.get(0).getPlayerName();
        if(winners.size() == 2) return
                ChatColor.YELLOW + "Победители" +
                ChatColor.GRAY + ": " +
                ChatColor.GOLD + winners.get(0).getPlayerName() +
                ChatColor.YELLOW + " и " +
                ChatColor.GOLD + winners.get(1).getPlayerName();
        return null;
     }

    public String formatIsDuo() {
        if(isDuo()) {
            return ChatColor.GOLD + "Режим" + ChatColor.GRAY + ": " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Дуо";
        } else {
            return ChatColor.GOLD + "Режим" + ChatColor.GRAY + ": " + ChatColor.DARK_AQUA + ChatColor.BOLD + "Соло";
        }
    }

    public String formatIsRatingGame() {
        if(isRatingGame()) {
            return ChatColor.GREEN + "" + ChatColor.ITALIC + "Рейтинговая игра";
        } else {
            return ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "Тренировочная игра";
        }
    }

    public String formatTitle() {
        String formattedDate = new SimpleDateFormat("dd.MM.yy HH:mm").format(getDate());
        return ChatColor.AQUA + "Игра от " + ChatColor.DARK_AQUA + formattedDate;
    }

    public boolean hasWon(String playerName) {
        for(PlayerSummary summary : getWinnersSummaries()) {
            if(summary.getPlayerName().equals(playerName)) return true;
        }
        return false;
    }

    public boolean hasParticipated(String playerName) {
        for(PlayerSummary summary : playerSummaries) {
            if(summary.getPlayerName().equals(playerName)) return true;
        }
        return false;
    }

    public List<PlayerSummary> getWinnersSummaries() {
        return getPlayerSummaries().stream().filter(PlayerSummary::isWinner).toList();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isRatingGame() {
        return isRatingGame;
    }

    public void setRatingGame(boolean ratingGame) {
        this.isRatingGame = ratingGame;
    }

    public boolean isDuo() {
        return isDuo;
    }

    public void setDuo(boolean duo) {
        isDuo = duo;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public List<PlayerSummary> getPlayerSummaries() {
        return playerSummaries;
    }

    public void setPlayerSummaries(List<PlayerSummary> playerSummaries) {
        this.playerSummaries = playerSummaries;
    }

}
