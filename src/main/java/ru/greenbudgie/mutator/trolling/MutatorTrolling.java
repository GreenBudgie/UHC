package ru.greenbudgie.mutator.trolling;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.TaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class MutatorTrolling extends Mutator {

    public static final List<TrollingEvent> events = new ArrayList<>();

    static {
        new TrollingEventSpawnMobs();
        new TrollingEventClearEffects();
        new TrollingEventRemoveTerrain();
        new TrollingEventLevitation();
        new TrollingEventLavaRain();
        new TrollingEventRandomRequest();
        new TrollingEventSpawnTnt();
        new TrollingEventNoMoving();
        new TrollingEventFloorIsMagma();
        new TrollingEventFlood();
        new TrollingEventGlowing();
        new TrollingEventTunnel();
        new TrollingEventTeleport();
    }

    private static final int MIN_TIME_TO_TROLL = 5 * 60;
    private static final int MAX_TIME_TO_TROLL = 10 * 60;
    private static final int TIME_TO_START_SELECTING = 2;
    private static final int SELECTION_EFFECTS = 8;
    private static final int TICKS_PER_SELECTION_EFFECT = 4;
    private static final float SELECTION_EFFECT_MAX_PITCH = 1.5F;
    private static final float SELECTION_EFFECT_MIN_PITCH = 0.8F;

    private int timeToTroll;
    private boolean isSelectingEvent;
    private int timeToStartSelecting;
    private int selectionEffectNumber;
    private TrollingEvent previouslySelectedEventEffect;

    public static void register(TrollingEvent event) {
        events.add(event);
    }

    @Override
    public ThreatStatus getThreatStatus() {
        return ThreatStatus.CRITICAL;
    }

    @Override
    public Material getItemToShow() {
        return Material.SADDLE;
    }

    @Override
    public String getName() {
        return "Егор Ратчин";
    }

    @Override
    public String getDescription() {
        return "Все игроки будут затролены. Это очень смешной мутатор!";
    }

    @Override
    public void onChoose() {
        reset();
    }

    @Override
    public void update() {
        if (timeToTroll > 0) {
            if (TaskManager.isSecUpdated()) {
                timeToTroll--;
            }
            return;
        }
        if (isSelectingEvent) {
            updateEventSelection();
            return;
        }
        initiateTrolling();
    }

    private void updateEventSelection() {
        if (timeToStartSelecting > 0) {
            if (TaskManager.isSecUpdated()) {
                timeToStartSelecting--;
            }
            return;
        }
        if (TaskManager.ticksPassed(TICKS_PER_SELECTION_EFFECT)) {
            if (selectionEffectNumber >= SELECTION_EFFECTS) {
                executeRandomEvent();
                return;
            }
            showRandomSelectionEffect();
            selectionEffectNumber++;
        }
    }

    private void initiateTrolling() {
        isSelectingEvent = true;
        timeToStartSelecting = TIME_TO_START_SELECTING;
        selectionEffectNumber = 0;
        for (Player player : PlayerManager.getInGamePlayersAndSpectators()) {
            player.playSound(player, Sound.ENTITY_PILLAGER_AMBIENT, 1, 2);
            player.sendTitle(
                    "",
                    GRAY + "☠ " + BOLD + DARK_RED + "Приготовься к троллингу" + GRAY + " ☠",
                    5,
                    20,
                    10
            );
        }
    }

    private void showRandomSelectionEffect() {
        List<TrollingEvent> availableEvents = events.stream()
                .filter(event -> event != previouslySelectedEventEffect)
                .toList();
        TrollingEvent randomEvent = MathUtils.choose(availableEvents);
        previouslySelectedEventEffect = randomEvent;
        String randomEventName = randomEvent.getFormattedName();
        float selectionFactor = selectionEffectNumber / (float) SELECTION_EFFECTS;
        float soundPitch = (SELECTION_EFFECT_MAX_PITCH - SELECTION_EFFECT_MIN_PITCH) * selectionFactor +
                SELECTION_EFFECT_MIN_PITCH;
        for (Player player : PlayerManager.getInGamePlayersAndSpectators()) {
            player.playSound(player, Sound.BLOCK_COMPARATOR_CLICK, 0.8F, soundPitch);
            player.sendTitle(
                    "",
                    randomEventName,
                    0,
                    10,
                    10
            );
        }
    }

    private List<TrollingEvent> getAvailableEvents() {
        if (!UHC.state.isDeathmatch()) {
            return events;
        }
        return events.stream().filter(TrollingEvent::canWorkOnArena).toList();
    }

    private void executeRandomEvent() {
        TrollingEvent event = MathUtils.choose(getAvailableEvents());
        event.execute();
        String message = event.getEventExecuteMessage();
        String formattedName = event.getFormattedName();
        for (Player player : PlayerManager.getInGamePlayersAndSpectators()) {
            player.playSound(player, Sound.BLOCK_ANVIL_LAND, 0.5F, 0.8F);
            player.sendMessage(message);
            player.sendTitle(
                    "",
                    GRAY + "☠ " + formattedName + GRAY + " ☠",
                    0,
                    60,
                    20
            );
        }
        reset();
    }

    private void reset() {
        timeToTroll = MathUtils.randomRange(MIN_TIME_TO_TROLL, MAX_TIME_TO_TROLL);
        isSelectingEvent = false;
    }

}
