package ru.greenbudgie.mutator.competition;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.mutator.manager.MutatorManager;

import java.util.Set;

public class CompetitionTaskKillSkeletons extends CompetitionTask {

    private static final Set<EntityType> supportedTypes = Set.of(EntityType.SKELETON, EntityType.STRAY);

    @Override
    public String getName() {
        return "Ненависть";
    }

    @Override
    public String getDescription() {
        return "Кто убьет больше всех скелетов? Только не визер-скелетов!";
    }

    @EventHandler
    public void eatGoldenApple(EntityDeathEvent event) {
        if (!supportedTypes.contains(event.getEntityType())) {
            return;
        }

        var killer = event.getEntity().getKiller();
        if (killer == null || !PlayerManager.isPlaying(killer)) {
            return;
        }

        MutatorManager.competition.addPlayerScore(killer, 1);
    }
}
