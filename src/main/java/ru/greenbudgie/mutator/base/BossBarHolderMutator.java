package ru.greenbudgie.mutator.base;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.event.SpectatorJoinEvent;
import ru.greenbudgie.event.SpectatorLeaveEvent;
import ru.greenbudgie.event.UHCPlayerLeaveEvent;
import ru.greenbudgie.event.UHCPlayerRejoinEvent;

import javax.annotation.Nonnull;

/**
 * Base class for mutators that display a boss bar that is shown for all in-game players.
 */
public abstract class BossBarHolderMutator extends Mutator implements Listener {

    @Nonnull
    protected final BossBar bar;

    public BossBarHolderMutator(@Nonnull BossBar bar) {
        this.bar = bar;
    }

    @Override
    public boolean containsBossBar() {
        return true;
    }

    @Override
    public boolean canBeHidden() {
        return false;
    }

    @Override
    public void onChoose() {
        bar.setVisible(true);
        for(Player player : PlayerManager.getInGamePlayersAndSpectators()) {
            bar.addPlayer(player);
        }
    }

    @Override
    public void onDeactivate() {
        bar.removeAll();
        bar.setVisible(false);
    }

    @EventHandler
    public void playerLeave(UHCPlayerLeaveEvent event) {
        bar.removePlayer(event.getUHCPlayer().getPlayer());
    }

    @EventHandler
    public void playerRejoin(UHCPlayerRejoinEvent event) {
        bar.addPlayer(event.getUHCPlayer().getPlayer());
    }

    @EventHandler
    public void spectatorJoin(SpectatorJoinEvent event) {
        bar.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void spectatorLeave(SpectatorLeaveEvent event) {
        bar.removePlayer(event.getPlayer());
    }

}
