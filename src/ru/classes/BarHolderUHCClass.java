package ru.classes;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import ru.UHC.UHCPlayer;

import java.util.HashMap;
import java.util.Map;

public abstract class BarHolderUHCClass extends UHCClass {

    protected Map<UHCPlayer, BossBar> bars = new HashMap<>();

    public abstract String getBarName();
    public abstract BarStyle getBarStyle();
    public abstract BarColor getBarColor();

    public BossBar getBar(UHCPlayer uhcPlayer) {
        return bars.getOrDefault(uhcPlayer, null);
    }

    @Override
    public void onGameStart(UHCPlayer uhcPlayer) {
        if(uhcPlayer.isAliveAndOnline()) {
            BossBar bar = Bukkit.createBossBar(getBarName(), getBarColor(), getBarStyle());
            bar.setVisible(true);
            bar.setProgress(0);
            bar.addPlayer(uhcPlayer.getPlayer());
            bars.put(uhcPlayer, bar);
        }
    }

    @Override
    public void onGameEnd(UHCPlayer uhcPlayer) {
        Player player = uhcPlayer.getPlayer();
        BossBar bar = getBar(uhcPlayer);
        if(player != null && bar != null) {
            bar.removePlayer(player);
            bar.setVisible(false);
        }
    }

    @Override
    public void onPlayerDeath(UHCPlayer uhcPlayer) {
        Player player = uhcPlayer.getPlayer();
        BossBar bar = getBar(uhcPlayer);
        if(player != null && bar != null) {
            bar.removePlayer(player);
            bar.setVisible(false);
        }
    }

    @Override
    public void onPlayerLeave(UHCPlayer uhcPlayer) {
        Player player = uhcPlayer.getPlayer();
        BossBar bar = getBar(uhcPlayer);
        if(player != null && bar != null) {
            bar.removePlayer(player);
        }
    }

    @Override
    public void onPlayerRejoin(UHCPlayer uhcPlayer) {
        Player player = uhcPlayer.getPlayer();
        BossBar bar = getBar(uhcPlayer);
        if(player != null && bar != null) {
            bar.addPlayer(player);
        }
    }

}
