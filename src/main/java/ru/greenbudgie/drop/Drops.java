package ru.greenbudgie.drop;

import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class Drops {

    public static final List<Drop> DROPS = new ArrayList<>();

    public static final AirDrop AIRDROP = new AirDrop();
    public static final CaveDrop CAVEDROP = new CaveDrop();
    public static final NetherDrop NETHERDROP = new NetherDrop();

    public static void update() {
        DROPS.forEach(Drop::update);
    }

    public static void firstSetup() {
        for(Drop drop : DROPS) {
            drop.setup();
            drop.timer += drop.getFirstDropDelay();
        }
    }

    public static void updateScoreboard(Scoreboard scoreboard) {
        for (Drop drop : DROPS) {
            drop.updateMarkerTeams(scoreboard);
        }
    }

}
