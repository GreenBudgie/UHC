package ru.greenbudgie.rating;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.greenbudgie.main.UHCPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Rating {

    private static final File ratingFile = new File(UHCPlugin.instance.getDataFolder() + File.separator + "rating.yml");
    private static YamlConfiguration rating;

    private static final List<GameSummary> gameSummaries = new ArrayList<>();
    private static final List<PlayerRatingSummary> ratingSummaries = new ArrayList<>();

    private static GameSummary currentGameSummary;

    public static GameSummary setupCurrentGameSummary() {
        currentGameSummary = new GameSummary();
        return currentGameSummary;
    }

    public static GameSummary getCurrentGameSummary() {
        return currentGameSummary;
    }

    public static void dismissCurrentGameSummary() {
        currentGameSummary = null;
    }

    public static void saveCurrentGameSummary() {
        if(currentGameSummary != null) {
            currentGameSummary.postSetup();
            saveGameSummary(currentGameSummary);
            currentGameSummary = null;
        }
    }

    /**
     * Loads game summaries from config file and replaces the current contents.
     * In general, this method is only invoked once the plugin starts.
     */
    @SuppressWarnings("unchecked")
    public static void loadFromConfig() {
        gameSummaries.clear();
        rating = YamlConfiguration.loadConfiguration(ratingFile);
        List<Map<?, ?>> serializedGameSummaryList = rating.getMapList("summaries");
        for(Map<?, ?> serializedSummary : serializedGameSummaryList) {
            gameSummaries.add(GameSummary.deserialize((Map<String, Object>) serializedSummary));
        }
        updateRatingSummaries();
    }

    /**
     * Adds the given game summary to rating and saves it to config file,
     * updating side relations.
     * This method must ONLY be invoked once the game summary is completed
     * and is not about to change in the future.
     * DO NOT use this method for testing purposes, because it instantly
     * updates the config file.
     */
    public static void saveGameSummary(GameSummary summary) {
        gameSummaries.add(summary);
        updateRatingSummaries();
        updateConfig();
    }

    /**
     * Updates rating summaries for all registered players.
     * This method is very slow and must only be invoked if
     * the game summaries list or its contents have been changed.
     */
    public static void updateRatingSummaries() {
        ratingSummaries.clear();
        int ratingGamesCount = 0;
        //Collecting all rating summaries
        for(GameSummary gameSummary : gameSummaries) {
            if(gameSummary.isRatingGame()) {
                ratingGamesCount++;
                for(PlayerSummary playerSummary : gameSummary.getPlayerSummaries()) {
                    PlayerRatingSummary summary = getRatingSummaryByName(playerSummary.getPlayerName());
                    if(summary == null) {
                        summary = new PlayerRatingSummary(playerSummary.getPlayerName());
                        ratingSummaries.add(summary);
                    }
                }
            }
        }

        //Setting up summaries
        for(PlayerRatingSummary ratingSummary : ratingSummaries) {
            int games = 0;
            int kills = 0;
            int wins = 0;
            double performanceSum = 0;
            List<PlayerSummary> playerSummaries = getAllSummariesInRatingGames(ratingSummary.getPlayerName());
            for(PlayerSummary playerSummary : playerSummaries) {
                games++;
                if(playerSummary.isWinner()) wins++;
                kills += playerSummary.getOverallKills();
                performanceSum += playerSummary.getGamePerformance();
            }
            double scalar = getPlayedGamesScalar();
            double playedGamesFactor = (games / (double) ratingSummaries.size()) * scalar + (1 - scalar);
            double winRate = wins / (double) games;
            double averagePerformance = (performanceSum / games) * playedGamesFactor;
            ratingSummary.setGamesPlayed(games);
            ratingSummary.setGamesWon(wins);
            ratingSummary.setWinRate(winRate);
            ratingSummary.setOverallKills(kills);
            ratingSummary.setAveragePerformance(averagePerformance);
        }

        //Sorting summaries by average performance
        ratingSummaries.sort(Comparator.comparingDouble(PlayerRatingSummary::getAveragePerformance).reversed());

        //Setting up rating places and generating items
        for(int i = 0; i < ratingSummaries.size(); i++) {
            PlayerRatingSummary summary = ratingSummaries.get(i);
            summary.setRatingPlace(i + 1);
            summary.generateRepresentingItem();
        }
    }

    /**
     * This value represents how much overall played games count affects average efficiency.
     * Smaller values affects efficiency less.
     */
    public static double getPlayedGamesScalar() {
        return 0.5;
    }

    public static List<PlayerRatingSummary> getRatingSummaries() {
        return ratingSummaries;
    }

    private static List<PlayerSummary> getAllSummariesInRatingGames(String playerName) {
        List<PlayerSummary> summaries = new ArrayList<>();
        for(GameSummary gameSummary : gameSummaries) {
            if(gameSummary.isRatingGame()) {
                for(PlayerSummary playerSummary : gameSummary.getPlayerSummaries()) {
                    if(playerSummary.getPlayerName().equals(playerName)) {
                        summaries.add(playerSummary);
                    }
                }
            }
        }
        return summaries;
    }

    public static PlayerRatingSummary getRatingSummaryByName(String playerName) {
        for(PlayerRatingSummary summary : ratingSummaries) {
            if(summary.getPlayerName().equals(playerName)) return summary;
        }
        return null;
    }

    public static GameSummary getGameSummaryByDate(long dateMillis) {
        for(GameSummary summary : gameSummaries) {
            if(summary.getDate().getTime() == dateMillis) return summary;
        }
        return null;
    }

    public static List<GameSummary> getGameSummaries() {
        return gameSummaries;
    }

    /**
     * Updates the config by the current summaries list and saves it to file.
     */
    public static void updateConfig() {
        List<Map<String, Object>> list = new ArrayList<>();
        for(GameSummary summary : gameSummaries) {
            list.add(summary.serialize());
        }
        rating.set("summaries", list);
        save();
    }

    public static void save() {
        try {
            rating.save(ratingFile);
        } catch(Exception ignored) {
        }
    }

}
