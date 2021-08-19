package ru.rating;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import ru.UHC.GameState;
import ru.util.ItemUtils;
import ru.util.MathUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerSummary implements ConfigurationSerializable {

    private final String playerName;
    private final GameSummary gameSummary;
    private int winningPlace = 0;
    private int gameKills = 0;
    private int deathmatchKills = 0;
    private String killerName = null;
    private String teammateName = null;
    private GameState deathState = null;

    public PlayerSummary(GameSummary summary, String playerName) {
        this.playerName = playerName;
        this.gameSummary = summary;
    }

    /**
     * Gets game performance, a value between 0 and 1.
     * The game performance represents the player achievements throughout the game.
     * It is based on place and number of kills, their factors and scalars.
     *
     * Performance is relative to other player's performances.
     *
     * The worst player will always have the performance value of 0, and the best will have 1.
     */
    public double getGamePerformance() {
        double highestRawPerformance = getGameSummary().getHighestRawGamePerformance();
        double lowestRawPerformance = getGameSummary().getLowestRawGamePerformance();
        double denominator = highestRawPerformance - lowestRawPerformance;
        if(denominator == 0) return 0;
        return (getRawGamePerformance() - lowestRawPerformance) / denominator;
    }

    protected double getRawGamePerformance() {
        return getPlacePerformanceFactorScaled() +
                getGameKillsPerformanceFactorScaled() +
                getDeathmatchKillsPerformanceFactorScaled();
    }

    public double getDeathmatchKillsPerformanceFactorScaled() {
        return getDeathmatchKillsPerformanceFactor() * getDeathmatchKillsPerformanceScalar();
    }

    public double getGameKillsPerformanceFactorScaled() {
        return getGameKillsPerformanceFactor() * getGameKillsPerformanceScalar();
    }

    public double getPlacePerformanceFactorScaled() {
        return getPlacePerformanceFactor() * getPlacePerformanceScalar();
    }

    /**
     * Scalar influences how the factor affects overall game performance.
     * In-game kills are more valuable than deathmatch kills, so its scalar is greater.
     */
    public double getGameKillsPerformanceScalar() {
        return 1.2;
    }

    /**
     * Gets the in-game kills factor, a value from 0 to 1
     */
    public double getGameKillsPerformanceFactor() {
        int allKills = getGameSummary().getGameKillsNumber();
        return getGameKills() / (double) allKills;
    }

    /**
     * Scalar influences how the factor affects overall game performance.
     * Deathmatch kills are less valuable than in-game kills, so its scalar is smaller.
     */
    public double getDeathmatchKillsPerformanceScalar() {
        return 1;
    }

    /**
     * Gets the deathmatch kills factor, a value from 0 to 1
     */
    public double getDeathmatchKillsPerformanceFactor() {
        int allKills = getGameSummary().getDeathmatchKillsNumber();
        return getDeathmatchKills() / (double) allKills;
    }

    /**
     * Scalar influences how the factor affects overall game performance.
     * Place is the most valuable factor, so its scalar is the greatest.
     */
    public double getPlacePerformanceScalar() {
        return 1.3;
    }

    /**
     * Gets the place factor, a value from 0 to 1.
     */
    public double getPlacePerformanceFactor() {
        int players = getGameSummary().getPlayerNumber();
        return 1 - ((getWinningPlace() - 1) / (double) players);
    }

    public int convertPerformanceFactor(double performanceFactor) {
        return (int) (performanceFactor * 100);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("playerName", getPlayerName());
        serialized.put("winningPlace", getWinningPlace());
        serialized.put("gameKills", getGameKills());
        serialized.put("deathmatchKills", getGameKills());
        if(getKillerName() != null) serialized.put("killerName", getKillerName());
        if(getTeammateName() != null) serialized.put("teammateName", getTeammateName());
        if(getDeathState() != null) serialized.put("deathState", getDeathState().name());
        return serialized;
    }

    public static PlayerSummary deserialize(GameSummary gameSummary, Map<String, Object> input) {
        PlayerSummary summary = new PlayerSummary(gameSummary,
                (String) input.getOrDefault("playerName", "Unknown player"));
        summary.setWinningPlace((int) input.getOrDefault("winningPlace", 0));
        summary.setGameKills((int) input.getOrDefault("gameKills", 0));
        summary.setDeathmatchKills((int) input.getOrDefault("deathmatchKills", 0));
        if(input.containsKey("killerName"))
            summary.setKillerName((String) input.get("killerName"));
        if(input.containsKey("teammateName"))
            summary.setTeammateName((String) input.get("teammateName"));
        if(input.containsKey("deathState"))
            summary.setDeathState(GameState.valueOf((String) input.get("deathState")));
        return summary;
    }

    public boolean isWinner() {
        return winningPlace == 1;
    }

    public ItemStack getRepresentingItem() {
        ItemStack item = ItemUtils.getHead(playerName);
        ItemUtils.setName(item, formatTitle());
        ItemUtils.addLore(item, formatWinningPlace());
        if(getTeammateName() != null) ItemUtils.addLore(item, formatTeammateName());
        ItemUtils.addLore(item,
                formatOverallKills(),
                formatGameKills(),
                formatDeathmatchKills());
        if(getKillerName() != null) ItemUtils.addLore(item, formatKiller());
        ItemUtils.addLore(item, formatDeathState());
        ItemUtils.addLore(item, formatPerformanceSummary());
        ItemUtils.addLore(item, formatPerformanceResult());
        return item;
    }

    public String formatPerformanceResult() {
        int performancePercentage = (int) (getGamePerformance() * 100);
        return ChatColor.GREEN + "Эффективность в игре" +
                ChatColor.GRAY + ": " +
                ChatColor.LIGHT_PURPLE + ChatColor.BOLD + performancePercentage +
                ChatColor.RESET + ChatColor.GRAY + "%";
    }

    public String formatPerformanceSummary() {
        int placeFactor = convertPerformanceFactor(getPlacePerformanceFactorScaled());
        int gameKillsFactor = convertPerformanceFactor(getGameKillsPerformanceFactorScaled());
        int deathmatchKillsFactor = convertPerformanceFactor(getDeathmatchKillsPerformanceFactorScaled());
        int result = placeFactor + gameKillsFactor + deathmatchKillsFactor;
        return ChatColor.DARK_GREEN + "Итог" +
                ChatColor.GRAY + ": " +
                ChatColor.DARK_AQUA + ChatColor.BOLD + placeFactor +
                ChatColor.RESET + ChatColor.GRAY + " + " +
                ChatColor.DARK_AQUA + ChatColor.BOLD + gameKillsFactor +
                ChatColor.RESET + ChatColor.GRAY + " + " +
                ChatColor.DARK_AQUA + ChatColor.BOLD + deathmatchKillsFactor +
                ChatColor.RESET + ChatColor.GRAY + " = " +
                ChatColor.AQUA + ChatColor.BOLD + result +
                ChatColor.RESET + ChatColor.GRAY + "pts.";
    }

    public String formatDeathState() {
        if(deathState == null) return ChatColor.DARK_AQUA + "Судьба неизвестна";
        return switch(deathState) {
            case PREPARING, VOTE -> ChatColor.AQUA + "Вышел до начала игры";
            case OUTBREAK -> ChatColor.AQUA + "Погиб до начала ПВП";
            case GAME -> ChatColor.AQUA + "Погиб во время игры";
            case DEATHMATCH -> ChatColor.AQUA + "Погиб на арене";
            default -> ChatColor.DARK_AQUA + "Судьба неизвестна";
        };
    }

    public String formatDeathmatchKills() {
        return ChatColor.GRAY + "- " +
                ChatColor.RED + "На арене" +
                ChatColor.GRAY + ": " +
                ChatColor.DARK_RED + ChatColor.BOLD + getDeathmatchKills() + " " +
                formatPerformancePoints(getDeathmatchKillsPerformanceFactorScaled());
    }

    public String formatGameKills() {
        return ChatColor.GRAY + "- " +
                ChatColor.RED + "Во время игры" +
                ChatColor.GRAY + ": " +
                ChatColor.DARK_RED + ChatColor.BOLD + getGameKills() + " " +
                formatPerformancePoints(getGameKillsPerformanceFactorScaled());
    }

    public String formatOverallKills() {
        return ChatColor.RED + "Убийства" +
                ChatColor.GRAY + ": " +
                ChatColor.DARK_RED + ChatColor.BOLD + getOverallKills();
    }

    @Nullable
    public String formatKiller() {
        if(getKillerName() == null) return null;
        return ChatColor.DARK_GREEN + "Погиб из-за " + ChatColor.GOLD + getKillerName();
    }

    @Nullable
    public String formatTeammateName() {
        if(getTeammateName() == null) return null;
        return ChatColor.LIGHT_PURPLE + "Был в команде с " + ChatColor.GOLD + getTeammateName();
    }

    public String formatWinningPlace() {
        String placeText = ChatColor.RESET + "" +
                ChatColor.DARK_AQUA + " место " +
                formatPerformancePoints(getPlacePerformanceFactorScaled());
        return switch(winningPlace) {
            case 1 -> ChatColor.YELLOW + "" + ChatColor.BOLD + "Первое" + placeText;
            case 2 -> ChatColor.GRAY + "" + ChatColor.BOLD + "Второе" + placeText;
            case 3 -> ChatColor.GOLD + "" + ChatColor.BOLD + "Третье" + placeText;
            default -> ChatColor.AQUA + "" + ChatColor.BOLD + winningPlace + "-е" + placeText;
        };
    }

    public String formatTitle() {
        return ChatColor.GOLD + playerName;
    }

    public String formatPerformancePoints(double performanceFactor) {
        int scaledPoints = convertPerformanceFactor(performanceFactor);
        return ChatColor.DARK_GRAY + "+" +
                ChatColor.DARK_AQUA + ChatColor.BOLD + scaledPoints +
                ChatColor.RESET + ChatColor.GRAY + "pts.";
    }

    public String getPlayerName() {
        return playerName;
    }

    public GameSummary getGameSummary() {
        return gameSummary;
    }

    public int getWinningPlace() {
        return winningPlace;
    }

    public void setWinningPlace(int winningPlace) {
        this.winningPlace = winningPlace;
    }

    public int getOverallKills() {
        return gameKills + deathmatchKills;
    }

    public void increaseGameKills() {
        this.gameKills++;
    }

    public int getGameKills() {
        return gameKills;
    }

    public void setGameKills(int gameKills) {
        this.gameKills = gameKills;
    }

    public void increaseDeathmatchKills() {
        this.deathmatchKills++;
    }

    public int getDeathmatchKills() {
        return deathmatchKills;
    }

    public void setDeathmatchKills(int deathmatchKills) {
        this.deathmatchKills = deathmatchKills;
    }

    public String getKillerName() {
        return killerName;
    }

    public void setKillerName(String killerName) {
        this.killerName = killerName;
    }

    public String getTeammateName() {
        return teammateName;
    }

    public void setTeammateName(String teammateName) {
        this.teammateName = teammateName;
    }

    public GameState getDeathState() {
        return deathState;
    }

    public void setDeathState(GameState deathState) {
        this.deathState = deathState;
    }

}
