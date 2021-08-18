package ru.rating;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import ru.UHC.GameState;
import ru.util.ItemUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PlayerSummary implements ConfigurationSerializable {

    private final String playerName;
    private int winningPlace = 0;
    private int gameKills = 0;
    private int deathmatchKills = 0;
    private String killerName = null;
    private String teammateName = null;
    private GameState deathState = null;

    public PlayerSummary(String playerName) {
        this.playerName = playerName;
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

    public static PlayerSummary deserialize(Map<String, Object> input) {
        PlayerSummary summary = new PlayerSummary(
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
        return item;
    }

    @Nullable
    public String formatDeathState() {
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
                ChatColor.DARK_RED + ChatColor.BOLD + getGameKills();
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
        return ChatColor.DARK_GREEN + "Погиб из-за " + ChatColor.GOLD + getKillerName();
    }

    @Nullable
    public String formatTeammateName() {
        if(getTeammateName() == null) return null;
        return ChatColor.LIGHT_PURPLE + "Был в команде с " + ChatColor.GOLD + getTeammateName();
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

    public String getPlayerName() {
        return playerName;
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
