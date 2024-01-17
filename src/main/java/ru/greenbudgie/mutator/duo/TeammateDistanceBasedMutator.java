package ru.greenbudgie.mutator.duo;

import org.bukkit.Location;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.PlayerTeam;
import ru.greenbudgie.mutator.Mutator;
import ru.greenbudgie.util.TaskManager;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class TeammateDistanceBasedMutator<T extends TeammateDistanceEffect> extends Mutator {

    @Nonnull
    protected final TeammateDistanceEffectManager<T> manager;

    public TeammateDistanceBasedMutator(@Nonnull TeammateDistanceEffectManager<T> manager) {
        super();
        this.manager = manager;
    }

    @Override
    public boolean isDuoOnly() {
        return true;
    }

    @Override
    public boolean canBeHidden() {
        return false;
    }

    @Override
    public void update() {
        if (!TaskManager.isSecUpdated()) {
            return;
        }
        for (PlayerTeam team : PlayerManager.getAliveTeams()) {
            if (team.isOneOrNoneAlive()) {
                continue;
            }
            List<T> effectsForTeam = manager.getEffectsForTeam(team);
            if (effectsForTeam.isEmpty()) {
                continue;
            }
            manager.applyEffects(team, effectsForTeam);
        }
    }

    @Override
    public boolean conflictsWith(Mutator another) {
        if (super.conflictsWith(another)) {
            return true;
        }
        return another instanceof TeammateDistanceBasedMutator;
    }

    public String getAdditionalActionBarInfo(Location player1Location, Location player2Location) {
        if (!isActive()) {
            return "";
        }
        double distanceSquared = player1Location.distanceSquared(player2Location);
        String distanceInfo = manager.getDistanceInfoFor(distanceSquared);
        if (distanceInfo == null) {
            return "";
        }
        return " " + distanceInfo;
    }

}
