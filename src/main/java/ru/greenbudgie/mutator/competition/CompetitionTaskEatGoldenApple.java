package ru.greenbudgie.mutator.competition;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import ru.greenbudgie.mutator.manager.MutatorManager;

public class CompetitionTaskEatGoldenApple extends CompetitionTask {

    @Override
    public String getName() {
        return "Яблочная Гонка";
    }

    @Override
    public String getDescription() {
        return "Кто съест больше всех золотых яблок?";
    }

    @EventHandler
    public void eatGoldenApple(PlayerItemConsumeEvent event) {
        var type = event.getItem().getType();
        if (type != Material.GOLDEN_APPLE && type != Material.ENCHANTED_GOLDEN_APPLE) {
            return;
        }

        MutatorManager.competition.addPlayerScore(event.getPlayer());
    }
}
