package ru.greenbudgie.mutator.preference;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.greenbudgie.UHC.PlayerOptionHolder;
import ru.greenbudgie.lobby.Lobby;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.mutator.InventoryBuilderMutator;
import ru.greenbudgie.mutator.Mutator;
import ru.greenbudgie.mutator.MutatorManager;
import ru.greenbudgie.util.MathUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class MutatorPreferenceManager implements Listener {

    private static final int MAX_PREFERENCES = 5;
    private static final Map<Player, Set<Mutator>> onlinePlayersPreferences = new HashMap<>();

    private static WeightedMutatorList weightedMutatorList;

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new MutatorPreferenceManager(), UHCPlugin.instance);
        for(Player player : Lobby.getPlayersInLobbyAndArenas()) {
            restorePreferences(player);
        }
        updateWeightedMutators();
    }

    /**
     * Gets a weighted list of mutators that are currently inactive and do not conflict with active
     */
    public static WeightedMutatorList getAvailableWeightedMutators() {
        List<Mutator> availableMutators = MutatorManager.getMutatorsAvailableForActivation();
        if(availableMutators.isEmpty()) {
            throw new IllegalArgumentException("Cannot choose a mutator from an empty list");
        }
        List<Mutator> initialWeightedMutators = weightedMutatorList.getInitialWeightedMutators();
        List<Mutator> availableWeightedMutators = initialWeightedMutators.stream()
                .filter(availableMutators::contains)
                .toList();
        return new WeightedMutatorList(availableWeightedMutators);
    }

    public static Mutator getRandomAvailableMutatorWeighted() {
        WeightedMutatorList weightedMutators = getAvailableWeightedMutators();
        return weightedMutators.getRandomElementWeighted().getElement();
    }

    /**
     * Registers a preferred mutator for the specified player if it is possible.
     * @return Whether this preference was added
     */
    public static boolean addPreference(Player player, Mutator mutator) {
        Set<Mutator> preferences = getPreferences(player);
        if (preferences.size() >= MAX_PREFERENCES) {
            return false;
        }
        if (preferences.contains(mutator)) {
            return false;
        }
        preferences.add(mutator);
        MutatorPreferenceManager.onlinePlayersPreferences.put(player, preferences);
        PlayerOptionHolder.saveMutatorPreferences(player.getName(), preferences);
        updateWeightedMutators();
        return true;
    }

    /**
     * Removes the player's preference if it is possible
     * @return Whether the player had the specified preference
     */
    public static boolean removePreference(Player player, Mutator mutator) {
        Set<Mutator> preferences = getPreferences(player);
        if (!preferences.contains(mutator)) {
            return false;
        }
        preferences.remove(mutator);
        if (preferences.isEmpty()) {
            clearPreferences(player);
        } else {
            MutatorPreferenceManager.onlinePlayersPreferences.put(player, preferences);
            PlayerOptionHolder.saveMutatorPreferences(player.getName(), preferences);
        }
        updateWeightedMutators();
        return true;
    }

    public static void clearPreferences(Player player) {
        onlinePlayersPreferences.remove(player);
        PlayerOptionHolder.saveMutatorPreferences(player.getName(), null);
        updateWeightedMutators();
    }

    /**
     * Gets a set of mutators that the player prefers, or empty set if nothing is preferred.
     */
    public static Set<Mutator> getPreferences(Player player) {
        return onlinePlayersPreferences.getOrDefault(player, new HashSet<>());
    }

    public static boolean hasPreference(Player player, Mutator mutator) {
        return getPreferences(player).contains(mutator);
    }

    public static boolean isPreferredBySomeone(Mutator mutator) {
        return onlinePlayersPreferences.values()
                .stream()
                .anyMatch(preferences -> preferences.contains(mutator));
    }

    @Nonnull
    public static WeightedMutator getWeightedMutator(Mutator mutator) {
        return weightedMutatorList.getElements().stream()
                .filter(weightedMutator -> weightedMutator.getElement() == mutator)
                .findFirst()
                .orElseThrow();
    }

    public static double getChance(Mutator mutator) {
        return getWeightedMutator(mutator).getChance();
    }

    public static List<Player> getOnlinePlayersWhoPreferMutator(Mutator mutator) {
        return onlinePlayersPreferences.entrySet().stream()
                .filter(entry -> entry.getValue().contains(mutator))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Nullable
    public static String getRandomOnlinePlayerNameWithPreferenceOf(Mutator mutator) {
        List<Player> playersWhoPreferMutator = getOnlinePlayersWhoPreferMutator(mutator);
        if (playersWhoPreferMutator.isEmpty()) {
            return null;
        }
        return MathUtils.choose(playersWhoPreferMutator).getName();
    }

    private static void restorePreferences(Player player) {
        if (onlinePlayersPreferences.containsKey(player)) {
            // Not required to restore preferences as they already exist
            return;
        }
        Set<Mutator> savedPreferences = PlayerOptionHolder.getMutatorPreferences(player.getName());
        if(!savedPreferences.isEmpty()) {
            onlinePlayersPreferences.put(player, savedPreferences);
        }
    }

    private static void updateWeightedMutators() {
        List<Mutator> preferredMutatorsWithRepeats = onlinePlayersPreferences.values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
        List<Mutator> allMutatorsWithRepeats = Stream.concat(
                preferredMutatorsWithRepeats.stream(),
                MutatorManager.mutators.stream()
        ).toList();
        weightedMutatorList = new WeightedMutatorList(allMutatorsWithRepeats);
    }

    @EventHandler
    public void restorePreferencesOnJoin(PlayerJoinEvent event) {
        restorePreferences(event.getPlayer());
        updateWeightedMutators();
        InventoryBuilderMutator.reopenAll();
    }

    @EventHandler
    public void updatePreferencesOnPlayerQuit(PlayerQuitEvent event) {
        onlinePlayersPreferences.remove(event.getPlayer());
        MutatorPreferenceManager.updateWeightedMutators();
        InventoryBuilderMutator.reopenAll();
    }

}
