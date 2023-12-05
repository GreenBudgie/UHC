package ru.greenbudgie.classes;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.event.*;

import java.util.HashMap;
import java.util.Map;

public abstract class BarHolderUHCClass extends UHCClass {

    protected Map<UHCPlayer, BossBar> bars = new HashMap<>();

    public abstract String getBarTitle();
    public abstract BarStyle getBarStyle();
    public abstract BarColor getBarColor();

    public BossBar getBar(UHCPlayer uhcPlayer) {
        return bars.getOrDefault(uhcPlayer, null);
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        for(UHCPlayer uhcPlayer : getAliveOnlinePlayersWithClass()) {
            BossBar bar = Bukkit.createBossBar(getBarTitle(), getBarColor(), getBarStyle());
            bar.setVisible(true);
            bar.setProgress(0);
            bar.addPlayer(uhcPlayer.getPlayer());
            bars.put(uhcPlayer, bar);
        }
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        for(UHCPlayer uhcPlayer : getAliveOnlinePlayersWithClass()) {
            Player player = uhcPlayer.getPlayer();
            BossBar bar = getBar(uhcPlayer);
            if(player != null && bar != null) {
                bar.removePlayer(player);
                bar.setVisible(false);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(UHCPlayerDeathEvent event) {
        UHCPlayer uhcPlayer = event.getUHCPlayer();
        Player player = uhcPlayer.getPlayer();
        BossBar bar = getBar(uhcPlayer);
        if(player != null && bar != null) {
            bar.removePlayer(player);
            bar.setVisible(false);
        }
    }

    @EventHandler
    public void onPlayerLeave(UHCPlayerLeaveEvent event) {
        UHCPlayer uhcPlayer = event.getUHCPlayer();
        Player player = uhcPlayer.getPlayer();
        BossBar bar = getBar(uhcPlayer);
        if(player != null && bar != null) {
            bar.removePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerRejoin(UHCPlayerRejoinEvent event) {
        UHCPlayer uhcPlayer = event.getUHCPlayer();
        Player player = uhcPlayer.getPlayer();
        BossBar bar = getBar(uhcPlayer);
        if(player != null && bar != null) {
            bar.addPlayer(player);
        }
    }

}
