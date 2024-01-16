package ru.greenbudgie.mutator;

import com.google.common.collect.Lists;
import ru.greenbudgie.UHC.ArenaManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.configuration.GameType;
import ru.greenbudgie.mutator.preference.MutatorPreferenceManager;
import ru.greenbudgie.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class MutatorManager {

	public static List<Mutator> activeMutators = new ArrayList<>();
	public static List<Mutator> mutators = new ArrayList<>();
	public static MutatorApples apples = new MutatorApples();
	public static MutatorGlowing glowing = new MutatorGlowing();
	public static MutatorHungerGames hungerGames = new MutatorHungerGames();
	public static MutatorMoreDrops moreDrops = new MutatorMoreDrops();
	public static MutatorNoShields noShields = new MutatorNoShields();
	public static MutatorDoubleDamage doubleDamage = new MutatorDoubleDamage();
	public static MutatorKitStart kitStart = new MutatorKitStart();
	public static MutatorEternalNight eternalNight = new MutatorEternalNight();
	public static MutatorEternalDay eternalDay = new MutatorEternalDay();
	public static MutatorDoubleArtifacts doubleArtifacts = new MutatorDoubleArtifacts();
	public static MutatorHaste haste = new MutatorHaste();
	public static MutatorSimpleRequests simpleRequests = new MutatorSimpleRequests();
	public static MutatorSmallMap smallMap = new MutatorSmallMap();
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
	public static MutatorSkeletons skeletons = new MutatorSkeletons();
	public static MutatorInteractiveArena interactiveArena = new MutatorInteractiveArena();
	public static MutatorLessHealth lessHealth = new MutatorLessHealth();
	public static MutatorDiamondLeather diamondLeather = new MutatorDiamondLeather();
	public static MutatorApocalypse apocalypse = new MutatorApocalypse();
	public static MutatorOneForAll oneForAll = new MutatorOneForAll();
	public static MutatorRestrictions restrictions = new MutatorRestrictions();
	public static MutatorTotems totems = new MutatorTotems();
	public static MutatorRequestAnywhere requestAnywhere = new MutatorRequestAnywhere();
	public static MutatorBinding binding = new MutatorBinding();
	public static MutatorDamageFly damageFly = new MutatorDamageFly();
	public static MutatorOverpoweredMobs overpoweredMobs = new MutatorOverpoweredMobs();
	public static MutatorOmniscient omniscient = new MutatorOmniscient();
	public static MutatorVegetarian vegetarian = new MutatorVegetarian();
	public static MutatorPhantomArtifacts artifacts = new MutatorPhantomArtifacts();
	public static MutatorRandomEffects randomEffects = new MutatorRandomEffects();
	public static MutatorMoreRegen moreRegen = new MutatorMoreRegen();
	public static MutatorGamesWithFire gamesWithFire = new MutatorGamesWithFire();
	public static MutatorHalfDamage halfDamage = new MutatorHalfDamage();
	public static MutatorDeathCoordinates deathCoordinates = new MutatorDeathCoordinates();
	public static MutatorImmunity immunity = new MutatorImmunity();
	public static MutatorHyperExplosions hyperExplosions = new MutatorHyperExplosions();
	public static MutatorLowMeleeDamage lowMeleeDamage = new MutatorLowMeleeDamage();
	public static MutatorNetherGames netherGames = new MutatorNetherGames();
	public static MutatorHealthUnion healthUnion = new MutatorHealthUnion();
	public static MutatorFriendsForever friendsForever = new MutatorFriendsForever();

	public static void init() {
		MutatorPreferenceManager.init();
	}

	public static void updateMutators() {
		// A copy prevents concurrent modifications
		List<Mutator> activeMutatorsCopy = Lists.newArrayList(activeMutators);
		for (Mutator mutator : activeMutatorsCopy) {
			// Some mutators might be deactivated by other mutators in the update process,
			// so we need to consider it here and do not update them
			if (!mutator.isActive()) {
				continue;
			}
			mutator.update();
		}
	}

	public static boolean doesMutatorConflictWithActive(Mutator mutator) {
		return activeMutators.stream().anyMatch(mutator::conflictsWith);
	}

	public static List<Mutator> getMutatorsAvailableForActivation() {
		List<Mutator> availableMutators = Lists.newArrayList(mutators);
		availableMutators.removeAll(activeMutators);
		availableMutators.removeIf(MutatorManager::doesMutatorConflictWithActive);
		if(GameType.getType().allowsClasses()) {
			availableMutators.removeIf(Mutator::conflictsWithClasses);
		}
		if (!UHC.isDuo) {
			availableMutators.removeIf(Mutator::isDuoOnly);
		}
		if(!ArenaManager.getCurrentArena().isOpen()) {
			availableMutators.removeIf(mutator -> !mutator.canWorkIfArenaIsClosed());
		}
		return availableMutators;
	}

	public static Mutator getRandomAvailableMutator() {
		List<Mutator> availableMutators = getMutatorsAvailableForActivation();
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

	public static Mutator byClassName(String name) {
		return mutators.stream()
				.filter(mutator -> mutator.getClass().getSimpleName().endsWith(name))
				.findFirst()
				.orElse(null);
	}

	public static Mutator activateRandomMutator(boolean applyHiding, boolean applyPreferences) {
		Mutator mutator;
		String preferencePlayerName = null;
		if (applyPreferences) {
			mutator = MutatorPreferenceManager.getRandomAvailableMutatorWeighted();
			preferencePlayerName = MutatorPreferenceManager.getRandomOnlinePlayerNameWithPreferenceOf(mutator);
		} else {
			mutator = getRandomAvailableMutator();
		}
		mutator.activate(applyHiding, preferencePlayerName);
		return mutator;
	}

	public static void activateRandomArtifactMutator() {
		List<Mutator> artifactMutators = getMutatorsAvailableForActivation().stream().filter(Mutator::canBeAddedFromArtifact).toList();
		Mutator mutator = MathUtils.choose(artifactMutators);
		mutator.activate(false, null);
	}

	public static void deactivateMutators() {
		List<Mutator> copy = Lists.newArrayList(activeMutators); //Prevents concurrent modifications
		for(Mutator mutator : copy) {
			mutator.deactivate();
		}
	}

	public static String getMessageFromCurrentMutators() {
		ThreatStatus average = ThreatStatus.getAverageStatus(
				activeMutators.stream().map(Mutator::getThreatStatus).toArray(ThreatStatus[]::new)
		);
		if(average != null) {
			return average.getRandomMessage();
		}
		return null;
	}

	public static Mutator getMutatorByConfigName(String configName) {
		for(Mutator mutator : mutators) {
			if(mutator.getConfigName().equals(configName)) return mutator;
		}
		return null;
	}

}
