package ru.greenbudgie.drop;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scoreboard.Scoreboard;
import ru.greenbudgie.drop.marker.DropMarker;
import ru.greenbudgie.mutator.MutatorManager;
import ru.greenbudgie.util.LocationFormatter;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public abstract class Drop {

    protected int timer;
    protected Location location;

    @Nullable
    protected DropMarker<?> currentMarker = null;
    protected final Set<DropMarker<?>> markers = new HashSet<>();

    public Drop() {
        Drops.DROPS.add(this);
    }

    public abstract String getName();
    public abstract int getDefaultDropDelay();
    public abstract void drop();
    public abstract Location getRandomLocation();
    public abstract ChatColor getMarkerColor();
    public abstract DropMarker<?> createMarker();

    public void setup() {
        timer = getDefaultDropDelay();
        if(MutatorManager.moreDrops.isActive())
            timer /= 2;
        location = getRandomLocation();
        currentMarker = createMarker();
        markers.add(currentMarker);
    }

    public World.Environment getSpawnEnvironment() {
        return World.Environment.NORMAL;
    }

    public void update() {}

    public String getCoordinatesInfo(@Nullable Location playerLocation) {
        if (playerLocation == null) {
            return LocationFormatter.format(location, DARK_AQUA, WHITE);
        }
        return LocationFormatter.formatToWithDistanceAndArrow(
                playerLocation,
                location,
                DARK_AQUA,
                WHITE,
                AQUA,
                DARK_GRAY,
                AQUA,
                false
        );
    }

    public String getSpawnMessage() {
        return DARK_GRAY + "" + BOLD + "∫ " + RESET +
                getName() + DARK_AQUA + " заспавнен!" +
                DARK_GRAY + "" + BOLD + " ∫";
    }

    public String getChatDropCoordinatesInfo() {
        String vertLine = DARK_GRAY + "" + BOLD + "∫" + RESET;
        return vertLine + " " + getName() +
                AQUA + " заспавнен на: " +
                getCoordinatesInfo(null) + " " + vertLine;
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

    public void updateMarkerTeams(Scoreboard scoreboard) {
        for (DropMarker<?> marker : markers) {
            marker.updateTeam(scoreboard);
        }
    }

    public void removeMarker(DropMarker<?> marker) {
        markers.remove(marker);
    }

}
