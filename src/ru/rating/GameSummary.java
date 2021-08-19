package ru.rating;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import ru.UHC.GameState;
import ru.util.ItemUtils;
import ru.util.MathUtils;
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
            summary.getPlayerSummaries().add(PlayerSummary.deserialize(summary, (Map<String, Object>) serializedSummary));
        }
        return summary;
    }

    /**
     * Generates new player summary with randomized fields.
     * For testing purposes
     */
    public static GameSummary generateRandomSummary(int minPlayers, int maxPlayers) {
        GameSummary summary = new GameSummary();
        summary.setDate(new Date(new Date().getTime() + MathUtils.randomRange(-999999999, 999999999)));
        summary.setDuo(Math.random() < 0.5);
        summary.setRatingGame(Math.random() < 0.5);
        summary.setDurationMinutes(MathUtils.randomRange(20, 80));
        int playerNumber = MathUtils.randomRange(minPlayers, maxPlayers);
        List<String> realPlayerNames = Lists.newArrayList(
                "Forest_engine",
                "Forest_engine2",
                "Forest_engine3",
                "nikki39",
                "danch");
        List<Integer> winningPlaces = new ArrayList<>();
        for(int winningPlace = 1; winningPlace < playerNumber + 1; winningPlace++) {
            winningPlaces.add(winningPlace);
        }
        Collections.shuffle(winningPlaces);
        for(int i = 0; i < playerNumber; i++) {
            String playerName;
            if(realPlayerNames.isEmpty() || Math.random() > (5.0 / playerNumber))
                playerName = MathUtils.getRandomSequence(8);
            else
                playerName = MathUtils.choose(realPlayerNames);
            realPlayerNames.remove(playerName);
            PlayerSummary playerSummary = new PlayerSummary(summary, playerName);
            playerSummary.setGameKills(MathUtils.randomRange(0, 3));
            playerSummary.setDeathmatchKills(MathUtils.randomRange(0, 5));
            playerSummary.setDeathState(MathUtils.choose(GameState.values()));
            Integer winningPlace = MathUtils.choose(winningPlaces);
            playerSummary.setWinningPlace(winningPlace);
            winningPlaces.remove(winningPlace);
            if(Math.random() < 0.5 && !summary.getPlayerSummaries().isEmpty()) {
                String killerName = MathUtils.choose(summary.getPlayerSummaries()).getPlayerName();
                playerSummary.setKillerName(killerName);
            }
            if(Math.random() < 0.5 && !summary.getPlayerSummaries().isEmpty() && summary.isDuo()) {
                String teammateName = MathUtils.choose(summary.getPlayerSummaries()).getPlayerName();
                playerSummary.setTeammateName(teammateName);
            }
            summary.getPlayerSummaries().add(playerSummary);
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
                byNumber(getPlayerNumber());
        String humanText = new NumericalCases(
                "человек",
                "человека",
                "человек").
                byNumber(getPlayerNumber());
        return ChatColor.GRAY + participateText + " участие " +
                ChatColor.DARK_AQUA + ChatColor.BOLD + getPlayerNumber() + " " +
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

    protected double getHighestRawGamePerformance() {
        double highest = 0;
        for(PlayerSummary summary : getPlayerSummaries()) {
            double rawPerformance = summary.getRawGamePerformance();
            if(rawPerformance > highest) highest = rawPerformance;
        }
        return highest;
    }

    protected double getLowestRawGamePerformance() {
        double lowest = Double.MAX_VALUE;
        for(PlayerSummary summary : getPlayerSummaries()) {
            double rawPerformance = summary.getRawGamePerformance();
            if(rawPerformance < lowest) lowest = rawPerformance;
        }
        return lowest;
    }

    /**
     * Gets the complete amount of kills every player have taken throughout the game
     */
    public int getOverallKillsNumber() {
        return getGameKillsNumber() + getDeathmatchKillsNumber();
    }

    /**
     * Gets the complete amount of kills every player have taken before deathmatch
     */
    public int getGameKillsNumber() {
        int number = 0;
        for(PlayerSummary summary : getPlayerSummaries()) {
            number += summary.getGameKills();
        }
        return number;
    }

    /**
     * Gets the complete amount of kills every player have taken on deathmatch
     */
    public int getDeathmatchKillsNumber() {
        int number = 0;
        for(PlayerSummary summary : getPlayerSummaries()) {
            number += summary.getDeathmatchKills();
        }
        return number;
    }

    public int getPlayerNumber() {
        return getPlayerSummaries().size();
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
