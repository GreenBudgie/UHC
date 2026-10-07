package ru.greenbudgie.mutator.competition;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import ru.greenbudgie.main.UHCPlugin;

public abstract class CompetitionTask implements Listener {

    public abstract String getName();

    public abstract String getDescription();

    public final void start() {
        Bukkit.getPluginManager().registerEvents(this, UHCPlugin.instance);
    }

    public final void end() {
        HandlerList.unregisterAll(this);
        onEnd();
    }

    public void onEnd() {
    }

}
