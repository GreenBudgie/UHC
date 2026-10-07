package ru.greenbudgie.mutator.competition;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.mutator.manager.MutatorManager;

import java.util.HashMap;
import java.util.Map;

public class CompetitionTaskLoseHealth extends CompetitionTask {

    private final Map<UHCPlayer, Double> actualDamage = new HashMap<>();

    @Override
    public String getName() {
        return "Мазохисты";
    }

    @Override
    public String getDescription() {
        return "Кто больше всех надамажится? Регенерация НЕ уменьшает твой прогресс! " +
                "Потеря желтых сердец полгощения не считается за урон! ";
    }

    @Override
    public void onEnd() {
        actualDamage.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void takeDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        var uhcPlayer = PlayerManager.asUHCPlayer(player);
        if (uhcPlayer == null || !uhcPlayer.isAlive()) {
            return;
        }

        var previousDamage = actualDamage.getOrDefault(uhcPlayer, 0d);
        var newFinalDamage = event.getFinalDamage() + previousDamage;
        actualDamage.put(uhcPlayer, newFinalDamage);

        var previousVisibleDamage = (int)Math.round(previousDamage);
        var visibleDamage = (int)Math.round(newFinalDamage);
        if (previousVisibleDamage != visibleDamage) {
            MutatorManager.competition.setPlayerScore(uhcPlayer, (int) Math.round(newFinalDamage));
        }
    }

}
