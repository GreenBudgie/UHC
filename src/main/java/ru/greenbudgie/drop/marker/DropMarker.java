package ru.greenbudgie.drop.marker;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.drop.Drop;
import ru.greenbudgie.event.BeforeGameEndEvent;
import ru.greenbudgie.main.UHCPlugin;

import java.util.Objects;

public abstract class DropMarker<T extends Drop> implements Listener {

    protected final Location location;
    protected final T drop;
    protected final ArmorStand marker;
    protected boolean isDropped = false;

    public DropMarker(T drop) {
        this.drop = drop;
        Location location = drop.getLocation().clone().add(getLocationShift());
        Objects.requireNonNull(location.getWorld());
        this.location = location;
        marker = (ArmorStand) location.getWorld().spawnEntity(
                location,
                EntityType.ARMOR_STAND
        );
        marker.setMarker(true);
        marker.setInvisible(true);
        marker.setInvulnerable(true);
        marker.setGlowing(true);
        marker.setSmall(true);
        marker.setBasePlate(false);
        updateTeamForEveryPlayer();
        Bukkit.getPluginManager().registerEvents(this, UHCPlugin.instance);
    }

    public Vector getLocationShift() {
        return new Vector(0, 0, 0);
    }

    public void remove() {
        marker.remove();
        drop.removeMarker(this);
        HandlerList.unregisterAll(this);
    }

    public void updateTeamForEveryPlayer() {
        for (Player player : PlayerManager.getInGamePlayersAndSpectators()) {
            Scoreboard scoreboard = player.getScoreboard();
            updateTeam(scoreboard);
        }
    }

    public void updateTeam(Scoreboard scoreboard) {
        Team team = scoreboard.getTeam(drop.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(drop.getName());
            team.setColor(drop.getMarkerColor());
        }
        team.addEntry(marker.getUniqueId().toString());
    }

    public void setDropped() {
        this.isDropped = true;
        onDrop();
    }

    public void onDrop() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DropMarker<?> that = (DropMarker<?>) o;
        return Objects.equals(location, that.location) && Objects.equals(drop, that.drop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, drop);
    }

    @EventHandler
    public void removeOnGameEnd(BeforeGameEndEvent event) {
        remove();
    }

}
