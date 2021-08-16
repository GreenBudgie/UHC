package ru.drop;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import ru.mutator.MutatorManager;

public abstract class Drop {

    protected int timer;
    protected Location location;

    public Drop() {
        Drops.DROPS.add(this);
    }

    public abstract String getName();
    public abstract int getDefaultDropDelay();
    public abstract void drop();
    public abstract Location getRandomLocation();

    public void setup() {
        timer = getDefaultDropDelay();
        if(MutatorManager.isActive(MutatorManager.airdrop))
            timer /= 2;
        location = getRandomLocation();
    }

    public World.Environment getSpawnEnvironment() {
        return World.Environment.NORMAL;
    }

    public void update() {}

    public String getCoordinatesInfo() {
        String comma = ChatColor.WHITE + ", ";
        return ChatColor.DARK_AQUA + "" + location.getBlockX() + comma +
                ChatColor.DARK_AQUA + location.getBlockY() + comma +
                ChatColor.DARK_AQUA + location.getBlockZ();
    }

    public String getSpawnMessage() {
        return ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "\u222B " + ChatColor.RESET +
                getName() + ChatColor.DARK_AQUA + " заспавнен!" +
                ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " \u222B";
    }

    public String getChatDropCoordinatesInfo() {
        String vertLine = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "\u222B" + ChatColor.RESET;
        return vertLine + " " + getName() +
                ChatColor.AQUA + " заспавнен на: " +
                getCoordinatesInfo() + " " + vertLine;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        if(timer >= 0 && timer < getDefaultDropDelay())
            this.timer = timer;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
