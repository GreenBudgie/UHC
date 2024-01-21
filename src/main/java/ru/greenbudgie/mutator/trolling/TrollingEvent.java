package ru.greenbudgie.mutator.trolling;

import static org.bukkit.ChatColor.*;

public abstract class TrollingEvent {

    public TrollingEvent() {
        MutatorTrolling.register(this);
    }

    public abstract String getName();

    public abstract void execute();

    public boolean canWorkOnArena() {
        return true;
    }

    public String getFormattedName() {
        return LIGHT_PURPLE + "" + BOLD + getName();
    }

    public String getEventExecuteMessage() {
        return GRAY + "☠ " +
                RED + "Троллинг" + GRAY + ": " +
                getFormattedName() + GRAY + ". " + RED + "Все были затролены" +
                GRAY + "! ☠";
    }

}
