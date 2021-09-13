package ru.mutator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.UHC.ArenaManager;
import ru.UHC.GameType;
import ru.UHC.PlayerOptionHolder;
import ru.lobby.Lobby;
import ru.main.UHCPlugin;
import ru.util.MathUtils;

import java.util.*;

public class MutatorManager {

	public static Map<String, Set<Mutator>> preferredMutators = new HashMap<>();
	public static List<Mutator> activeMutators = new ArrayList<>();
	public static List<Mutator> mutators = new ArrayList<>();
	public static MutatorApples apples = new MutatorApples();
	public static MutatorGlowing glowing = new MutatorGlowing();
	public static MutatorHungerGames hungerGames = new MutatorHungerGames();
	public static MutatorDrop airdrop = new MutatorDrop();
	public static MutatorNoShields noShields = new MutatorNoShields();
	public static MutatorDoubleDamage doubleDamage = new MutatorDoubleDamage();
	public static MutatorKitStart kitStart = new MutatorKitStart();
	public static MutatorEternalNight eternalNight = new MutatorEternalNight();
	public static MutatorEternalDay eternalDay = new MutatorEternalDay();
	public static MutatorDoubleArtifacts doubleArtifacts = new MutatorDoubleArtifacts();
	public static MutatorHaste haste = new MutatorHaste();
	public static MutatorSimpleRequests simpleRequests = new MutatorSimpleRequests();
	public static MutatorSmallMap smallMap = new MutatorSmallMap();
	public static MutatorFlameFist flameFist = new MutatorFlameFist();
	public static MutatorNoHunger noHunger = new MutatorNoHunger();
	public static MutatorDeathTnt deathTnt = new MutatorDeathTnt();
	public static MutatorSupply supply = new MutatorSupply();
	public static MutatorWizardBattle wizardBattle = new MutatorWizardBattle();
	public static MutatorKnockback knockback = new MutatorKnockback();
	public static MutatorDamageBound damageBound = new MutatorDamageBound();
	public static MutatorHealthDisplay healthDisplay = new MutatorHealthDisplay();
	public static MutatorChemistBattle chemistBattle = new MutatorChemistBattle();
	public static MutatorDangerWater dangerWater = new MutatorDangerWater();
	public static MutatorBabyZombies babyZombies = new MutatorBabyZombies();
	public static MutatorMeetingPlace meetingPlace = new MutatorMeetingPlace();
	public static MutatorInvisible invisible = new MutatorInvisible();
	public static MutatorElytra elytra = new MutatorElytra();
	public static MutatorGoodDeath goodDeath = new MutatorGoodDeath();
	public static MutatorJump jump = new MutatorJump();
	public static MutatorUnexpectedRequests unexpectedRequests = new MutatorUnexpectedRequests();
	public static MutatorInfiniteBows infiniteBows = new MutatorInfiniteBows();
	public static MutatorChorusDamage chorusDamage = new MutatorChorusDamage();
	public static MutatorSkeletons skeletons = new MutatorSkeletons();
	public static MutatorInteractiveArena interactiveArena = new MutatorInteractiveArena();
	public static MutatorLessHealth lessHealth = new MutatorLessHealth();
	public static MutatorDiamondLeather diamondLeather = new MutatorDiamondLeather();
	public static MutatorApocalypse apocalypse = new MutatorApocalypse();
	public static MutatorOneForAll oneForAll = new MutatorOneForAll();
	public static MutatorRestrictions restrictions = new MutatorRestrictions();
	public static MutatorTotems totems = new MutatorTotems();
	public static MutatorRequestAnywhere requestAnywhere = new MutatorRequestAnywhere();
	public static MutatorNoKnockback noKnockback = new MutatorNoKnockback();
	public static MutatorBinding binding = new MutatorBinding();
	public static MutatorDamageFly damageFly = new MutatorDamageFly();
	public static MutatorOverpoweredMobs overpoweredMobs = new MutatorOverpoweredMobs();
	public static MutatorOmniscient omniscient = new MutatorOmniscient();
	public static MutatorVegetarian vegetarian = new MutatorVegetarian();
	public static MutatorPhantomArtifacts artifacts = new MutatorPhantomArtifacts();
	public static MutatorRandomEffects randomEffects = new MutatorRandomEffects();
	public static MutatorMoreRegen moreRegen = new MutatorMoreRegen();
	public static MutatorGamesWithFire gamesWithFire = new MutatorGamesWithFire();
	public static MutatorAttackWeakness attackWeakness = new MutatorAttackWeakness();
	public static MutatorHalfDamage halfDamage = new MutatorHalfDamage();
	public static MutatorIncreasingDamage increasingDamage = new MutatorIncreasingDamage();
	public static MutatorDeathCoordinates deathCoordinates = new MutatorDeathCoordinates();

	public static void init() {
		Bukkit.getPluginManager().registerEvents(new Listener() {

			@EventHandler
			public void setPreferencesOnJoin(PlayerJoinEvent event) {
				String playerName = event.getPlayer().getName();
				restorePreferences(playerName);
			}

		}, UHCPlugin.instance);
		for(Player player : Lobby.getPlayersInLobbyAndArenas()) {
			restorePreferences(player.getName());
		}
	}

	private static void restorePreferences(String playerName) {
		if(!preferredMutators.containsKey(playerName)) {
			Set<Mutator> savedMutators = PlayerOptionHolder.getMutatorPreferences(playerName);
			if(!savedMutators.isEmpty()) {
				preferredMutators.put(playerName, savedMutators);
			}
		}
	}

	public static void updateMutators() {
		List<Mutator> copy = Lists.newArrayList(activeMutators); //Prevents concurrent modifications
		copy.forEach(Mutator::update);
	}

	public static boolean hasPreferences(Mutator mutator) {
		return getAvailablePreferences().values().stream().anyMatch(mutators -> mutators.contains(mutator));
	}

	public static int getPreferencePercent(Mutator mutator) {
		List<Mutator> otherMutators = getAvailablePreferredMutatorsWeighted();
		otherMutators.removeIf(m -> mutator == m);
		double otherSize = otherMutators.size();
		return (int) ((1 - (otherSize / getAvailablePreferredMutatorsWeighted().size())) * 100);
	}

	/**
	 * Gets mutator preferences for online players only
	 */
	public static Map<String, Set<Mutator>> getAvailablePreferences() {
		Map<String, Set<Mutator>> preferred = Maps.newHashMap(preferredMutators);
		Set<String> toRemove = new HashSet<>();
		for(String name : preferred.keySet()) {
			if(Bukkit.getOnlinePlayers().stream().noneMatch(player -> player.getName().equals(name))) {
				toRemove.add(name);
			}
		}
		toRemove.forEach(preferred::remove);
		return preferred;
	}

	public static Set<String> getPlayersWhoPrefersMutator(Mutator mutator) {
		Map<String, Set<Mutator>> avPreferences = getAvailablePreferences();
		Set<String> names = new HashSet<>();
		for(String name : avPreferences.keySet()) {
			if(mutator.isPreferredBy(name)) names.add(name);
		}
		return names;
	}

	public static List<Mutator> getAvailablePreferredMutatorsWeighted() {
		Map<String, Set<Mutator>> avPreferences = getAvailablePreferences();
		if(avPreferences.isEmpty()) return new ArrayList<>();
		List<Mutator> availableMutators = getNonConflictingInactiveMutators();
		if(availableMutators.isEmpty()) throw new IllegalArgumentException("Cannot choose a mutator from an empty list");
		List<Mutator> preferredWeighted = new ArrayList<>();
		for(String name : avPreferences.keySet()) {
			Set<Mutator> mutators = Sets.newHashSet(avPreferences.get(name));
			mutators.removeIf(mutator -> !availableMutators.contains(mutator));
			preferredWeighted.addAll(mutators);
		}
		return preferredWeighted;
	}

	public static boolean doesMutatorConflictsWithActive(Mutator mutator) {
		return activeMutators.stream().anyMatch(mutator::conflictsWith);
	}

	public static List<Mutator> getNonConflictingInactiveMutators() {
		List<Mutator> availableMutators = Lists.newArrayList(mutators);
		availableMutators.removeAll(activeMutators);
		availableMutators.removeIf(MutatorManager::doesMutatorConflictsWithActive);
		if(GameType.getType().allowsClasses()) availableMutators.removeIf(Mutator::conflictsWithClasses);
		if(!ArenaManager.getCurrentArena().isOpen()) availableMutators.removeIf(mutator -> !mutator.canWorkIfArenaIsClosed());
		return availableMutators;
	}

	public static Mutator getRandomAvailableMutator() {
		List<Mutator> availableMutators = getNonConflictingInactiveMutators();
		if(!availableMutators.isEmpty()) {
			return MathUtils.choose(availableMutators);
		}
		throw new IllegalArgumentException("Cannot choose a mutator from an empty list");
	}

	public static List<Mutator> getMutatorsForDeactivation() {
		List<Mutator> mutators = Lists.newArrayList(MutatorManager.activeMutators);
		mutators.removeIf(mutator -> !mutator.canBeDeactivatedByArtifact());
		return mutators;
	}

	public static Mutator getRandomMutatorExcept(List<Mutator> list) {
		List<Mutator> availableMutators = Lists.newArrayList(mutators);
		availableMutators.removeAll(list);
		if(!availableMutators.isEmpty()) {
			return MathUtils.choose(availableMutators);
		}
		throw new IllegalArgumentException("Cannot choose a mutator from an empty list");
	}

	public static boolean isActive(Mutator mutator) {
		return activeMutators.contains(mutator);
	}

	public static Mutator byClassName(String name) {
		return mutators.stream().filter(mutator -> mutator.getClass().getSimpleName().endsWith(name)).findFirst().orElse(null);
	}

	public static Mutator activateRandomMutator(boolean applyHiding, boolean applyPreferences) {
		Mutator mutator;
		String preference = null;
		if(applyPreferences && MathUtils.chance(40)) {
			List<Mutator> preferred = getAvailablePreferredMutatorsWeighted();
			if(!preferred.isEmpty()) {
				mutator = MathUtils.choose(preferred);
				preference = MathUtils.choose(getPlayersWhoPrefersMutator(mutator));
			} else {
				mutator = getRandomAvailableMutator();
			}
		} else {
			mutator = getRandomAvailableMutator();
		}
		mutator.activate(applyHiding, preference);
		return mutator;
	}

	public static Mutator activateRandomArtifactMutator() {
		List<Mutator> artifactMutators = getNonConflictingInactiveMutators().stream().filter(Mutator::canBeAddedFromArtifact).toList();
		Mutator mutator = MathUtils.choose(artifactMutators);
		mutator.activate(false, null);
		return mutator;
	}

	public static void deactivateMutators() {
		List<Mutator> copy = Lists.newArrayList(activeMutators); //Prevents concurrent modifications
		for(Mutator mutator : copy) {
			mutator.onDeactivate();
			if(mutator instanceof Listener) {
				HandlerList.unregisterAll((Listener) mutator);
			}
		}
		activeMutators.clear();
	}

	public static String getMessageFromCurrentMutators() {
		ThreatStatus average = ThreatStatus.getAverageStatus(activeMutators.stream().map(Mutator::getThreatStatus).toArray(ThreatStatus[]::new));
		if(average != null) {
			return average.getRandomMessage();
		}
		return null;
	}

	public static void setPreference(String name, Mutator mutator, boolean preferred) {
		Set<Mutator> pref = preferredMutators.getOrDefault(name, new HashSet<>());
		if(preferred) {
			pref.add(mutator);
		} else {
			pref.remove(mutator);
		}
		if(pref.isEmpty()) {
			preferredMutators.remove(name);
			PlayerOptionHolder.saveMutatorPreferences(name, null);
		} else {
			preferredMutators.put(name, pref);
			PlayerOptionHolder.saveMutatorPreferences(name, pref);
		}

	}

	public static void clearPreferences(String name) {
		Set<Mutator> preferredSet = Sets.newHashSet(preferredMutators.getOrDefault(name, new HashSet<>()));
		for(Mutator preferred : preferredSet) {
			setPreference(name, preferred, false);
		}
	}

	public static Mutator getMutatorByConfigName(String configName) {
		for(Mutator mutator : mutators) {
			if(mutator.getConfigName().equals(configName)) return mutator;
		}
		return null;
	}

}
