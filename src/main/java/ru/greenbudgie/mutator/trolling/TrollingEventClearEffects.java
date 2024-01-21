package ru.greenbudgie.mutator.trolling;

import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;

public class TrollingEventClearEffects extends TrollingEvent {

    @Override
    public String getName() {
        return "Очистка Эффектов";
    }

    @Override
    public void execute() {
        for (Player player : PlayerManager.getAliveOnlinePlayers()) {
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        }
    }

}
