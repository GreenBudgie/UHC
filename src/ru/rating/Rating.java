package ru.rating;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.main.UHCPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rating {

    private static File ratingFile = new File(UHCPlugin.instance.getDataFolder() + File.separator + "rating.yml");
    private static YamlConfiguration rating = YamlConfiguration.loadConfiguration(ratingFile);

    private static List<GameSummary> gameSummaries = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static void loadFromConfig() {
        gameSummaries.clear();
        List<Map<?, ?>> serializedGameSummaryList = rating.getMapList("summaries");
        for(Map<?, ?> serializedSummary : serializedGameSummaryList) {
            gameSummaries.add(GameSummary.deserialize((Map<String, Object>) serializedSummary));
        }
    }

    public static GameSummary getSummaryByDate(long dateMillis) {
        for(GameSummary summary : gameSummaries) {
            if(summary.getDate().getTime() == dateMillis) return summary;
        }
        return null;
    }

    public static List<GameSummary> getGameSummaries() {
        return gameSummaries;
    }

    /**
     * Updates the config by the current gameSummaries list and saves it to file
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
