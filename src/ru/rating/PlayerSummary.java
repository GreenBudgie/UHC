package ru.rating;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import ru.UHC.GameState;
import ru.UHC.UHC;
import ru.classes.ClassManager;
import ru.classes.UHCClass;
import ru.util.ItemUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
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
    private UHCClass uhcClass = null;

    /**
     * Game performance must be calculated once after all players are present in game summary
     */
    private double gamePerformance = -1;
    private ItemStack representingItem = null;

    public PlayerSummary(GameSummary summary, String playerName) {
        this.playerName = playerName;
        this.gameSummary = summary;
    }

    public double getGamePerformance() {
        return gamePerformance;
    }

    public void postSetup() {
        calculateGamePerformance();
        generateRepresentingItem();
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
    public void calculateGamePerformance() {
        double highestRawPerformance = getGameSummary().getHighestRawGamePerformance();
        double lowestRawPerformance = getGameSummary().getLowestRawGamePerformance();
        double denominator = highestRawPerformance - lowestRawPerformance;
        if(denominator == 0) {
            gamePerformance = 0;
            return;
        }
        gamePerformance = (getRawGamePerformance() - lowestRawPerformance) / denominator;

    }

    protected double getRawGamePerformance() {
        return getPlacePerformanceFactorScaled() +
                getGameKillsPerformanceFactorScaled() +
                getDeathmatchKillsPerformanceFactorScaled();
    }

    protected double getDeathmatchKillsPerformanceFactorScaled() {
        return getDeathmatchKillsPerformanceFactor() * getDeathmatchKillsPerformanceScalar();
    }

    protected double getGameKillsPerformanceFactorScaled() {
        return getGameKillsPerformanceFactor() * getGameKillsPerformanceScalar();
    }

    protected double getPlacePerformanceFactorScaled() {
        return getPlacePerformanceFactor() * getPlacePerformanceScalar();
    }

    /**
     * Scalar influences how the factor affects overall game performance.
     * In-game kills are more valuable than deathmatch kills, so its scalar is greater.
     */
    protected double getGameKillsPerformanceScalar() {
        return 1.1;
    }

    /**
     * Gets the in-game kills factor, a value from 0 to 1
     */
    protected double getGameKillsPerformanceFactor() {
        int allKills = getGameSummary().getGameKillsNumber();
        if(allKills == 0) return 0;
        return getGameKills() / (double) allKills;
    }

    /**
     * Scalar influences how the factor affects overall game performance.
     * Deathmatch kills are less valuable than in-game kills, so its scalar is smaller.
     */
    protected double getDeathmatchKillsPerformanceScalar() {
        return 1;
    }

    /**
     * Gets the deathmatch kills factor, a value from 0 to 1
     */
    protected double getDeathmatchKillsPerformanceFactor() {
        int allKills = getGameSummary().getDeathmatchKillsNumber();
        if(allKills == 0) return 0;
        return getDeathmatchKills() / (double) allKills;
    }

    /**
     * Scalar influences how the factor affects overall game performance.
     * Place is the most valuable factor, so its scalar is the greatest.
     */
    protected double getPlacePerformanceScalar() {
        return 1.6;
    }

    /**
     * Gets the place factor, a value from 0 to 1.
     */
    protected double getPlacePerformanceFactor() {
        int players = getGameSummary().getPlayerNumber();
        if(players == 0) return 0;
        return 1 - ((getWinningPlace() - 1) / (double) players);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("playerName", getPlayerName());
        serialized.put("winningPlace", getWinningPlace());
        serialized.put("gameKills", getGameKills());
        serialized.put("deathmatchKills", getDeathmatchKills());
        if(getKillerName() != null) serialized.put("killerName", getKillerName());
        if(getTeammateName() != null) serialized.put("teammateName", getTeammateName());
        if(getDeathState() != null) serialized.put("deathState", getDeathState().name());
        if(getUHClass() != null) serialized.put("class", getUHClass().getConfigName());
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
        if(input.containsKey("class")) {
            summary.setUHCClass(ClassManager.getClassByConfigName((String) input.get("class")));
        }
        return summary;
    }

    public boolean isWinner() {
        return winningPlace == 1;
    }

    public void generateRepresentingItem() {
        ItemUtils.Builder builder = ItemUtils.builder(ItemUtils.getHead(playerName));
        builder.withName(formatTitle());
        builder.withLore(formatWinningPlace(), formatPerformanceResult());
        if(getTeammateName() != null) builder.withLore(formatTeammateName());
        if(getKillerName() != null) builder.withLore(formatKiller());
        builder.withLore(formatOverallKills(), formatGameKills(), formatDeathmatchKills());
        if(getDeathState() != null) builder.withLore(formatDeathState());
        builder.withLore(formatUHCClass());
        representingItem = builder.build();
    }

    public ItemStack getRepresentingItem() {
        return representingItem;
    }

    public String formatUHCClass() {
        if(uhcClass == null) {
            return ChatColor.GRAY + "Играл без класса";
        }
        return ChatColor.GRAY + "Класс: " + uhcClass.getName();
    }

    public String formatPerformanceResult() {
        int performancePercentage = (int) (getGamePerformance() * 100);
        return ChatColor.GRAY + "Эффективность в игре: " +
                ChatColor.AQUA + ChatColor.BOLD + performancePercentage +
                ChatColor.RESET + ChatColor.GRAY + "%";
    }

    public String formatDeathState() {
        if(deathState == null) return null;
        return switch(deathState) {
            case PREPARING, VOTE -> ChatColor.GRAY + "Вышел до начала игры";
            case OUTBREAK -> ChatColor.GRAY + "Погиб до начала ПВП";
            case GAME -> ChatColor.GRAY + "Погиб во время игры";
            case DEATHMATCH -> ChatColor.GRAY + "Погиб на арене";
            default -> ChatColor.GRAY + "Судьба неизвестна";
        };
    }

    public String formatDeathmatchKills() {
        return ChatColor.GRAY + "- " +
                ChatColor.RED + "На арене" +
                ChatColor.GRAY + ": " +
                ChatColor.DARK_RED + ChatColor.BOLD + getDeathmatchKills();
    }

    public String formatGameKills() {
        return ChatColor.GRAY + "- " +
                ChatColor.RED + "Во время игры" +
                ChatColor.GRAY + ": " +
                ChatColor.DARK_RED + ChatColor.BOLD + getGameKills();
    }

    public String formatOverallKills() {
        return ChatColor.RED + "Убийства" +
                ChatColor.GRAY + ": " +
                ChatColor.DARK_RED + ChatColor.BOLD + getOverallKills();
    }

    @Nullable
    public String formatKiller() {
        if(getKillerName() == null) return null;
        return ChatColor.GRAY + "Погиб из-за " + ChatColor.GOLD + getKillerName();
    }

    @Nullable
    public String formatTeammateName() {
        if(getTeammateName() == null) return null;
        return ChatColor.GRAY + "Был в команде с " + ChatColor.GOLD + getTeammateName();
    }

    public String formatWinningPlace() {
        String placeText = ChatColor.RESET + "" + ChatColor.DARK_AQUA + " место";
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

    public UHCClass getUHClass() {
        return uhcClass;
    }

    public void setUHCClass(UHCClass uhcClass) {
        this.uhcClass = uhcClass;
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

    /**
     * Increases kill count based on current game state
     */
    public void increaseKills() {
        if(UHC.state == GameState.DEATHMATCH) {
            increaseDeathmatchKills();
        } else {
            increaseGameKills();
        }
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
