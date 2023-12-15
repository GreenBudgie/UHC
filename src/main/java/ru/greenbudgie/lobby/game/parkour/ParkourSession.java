package ru.greenbudgie.lobby.game.parkour;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.InventoryHelper;
import ru.greenbudgie.util.Region;

import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public class ParkourSession {

    private static final NamespacedKey BEST_TIME_KEY = new NamespacedKey(UHCPlugin.instance, "best_time");

    /**
     * How far from the center of the start block a player should be away to start the timer
     */
    private static final int PARKOUR_START_DISTANCE = 1;

    private static final int PARKOUR_START_DISTANCE_SQ = PARKOUR_START_DISTANCE * PARKOUR_START_DISTANCE;

    private final Player player;

    private final Location startLocation;

    private boolean isRunning = false;

    private long timer = 0;

    public ParkourSession(Player player, Block startBlock) {
        this.player = player;
        Location playerLocation = player.getLocation();
        Location centerStartLocation = startBlock.getLocation().add(0.5, 0, 0.5);
        centerStartLocation.setYaw(playerLocation.getYaw());
        centerStartLocation.setPitch(playerLocation.getPitch());
        this.startLocation = centerStartLocation;
    }

    public void start() {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6F, 1.5F);
        player.getInventory().clear();
        player.getInventory().addItem(
                ParkourItems.RESET_ITEM.clone(),
                ParkourItems.END_ITEM.clone()
        );
        InventoryHelper.sendActionBarMessage(player, GRAY + "< " + AQUA + BOLD + "Ты зашел на паркур" + GRAY + " >");
    }

    public void restart() {
        player.teleport(startLocation);
        player.playSound(player.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 0.4F, 2F);
        isRunning = false;
        timer = 0;
    }

    public void complete() {
        removeItems();
        Sign sign = findSign();
        if (sign == null) {
            endNoBestTime();
            return;
        }
        if (isBestTime(sign)) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1.5F);
            updateBestTime(sign);
            InventoryHelper.sendActionBarMessage(
                    player,
                    GRAY + "< " + DARK_AQUA + BOLD + "Ты поставил новый рекорд: " + AQUA + BOLD + formatTime() + GRAY + " >"
            );
            return;
        }
        endNoBestTime();
    }

    public void end() {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.6F, 1F);
        removeItems();
        InventoryHelper.sendActionBarMessage(player, GRAY + "< " + DARK_AQUA + BOLD + "Ты вышел с паркура" + GRAY + " >");
    }

    public void update() {
        if (!isRunning) {
            Location playerLocation = player.getLocation();
            double distanceFromStartSq = playerLocation.distanceSquared(startLocation);
            if (distanceFromStartSq > PARKOUR_START_DISTANCE_SQ) {
                isRunning = true;
            } else {
                return;
            }
        }
        String formattedTime = formatTime();
        InventoryHelper.sendActionBarMessage(player, GRAY + "" + BOLD + formattedTime);
        timer++;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    private void endNoBestTime() {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.6F, 1.2F);
        InventoryHelper.sendActionBarMessage(
                player,
                GRAY + "< " + DARK_AQUA + "Ты прошел паркур за " + AQUA + BOLD + formatTime() + GRAY + " >"
        );
    }

    private void removeItems() {
        player.getInventory().removeItem(
                ParkourItems.RESET_ITEM,
                ParkourItems.END_ITEM
        );
    }

    private void updateBestTime(Sign sign) {
        sign.getPersistentDataContainer().set(
                BEST_TIME_KEY,
                PersistentDataType.LONG,
                timer
        );
        SignSide side = sign.getSide(Side.FRONT);
        side.setLine(0, GRAY + "Лучшее время");
        side.setLine(1, GOLD + player.getName());
        side.setLine(2, GRAY + "< " + AQUA + BOLD + formatTime() + RESET + GRAY + " >");
        sign.update();
    }

    private boolean isBestTime(Sign sign) {
        Long previousBestTime = sign.getPersistentDataContainer().get(BEST_TIME_KEY, PersistentDataType.LONG);
        if (previousBestTime == null) {
            return true;
        }
        return timer < previousBestTime;
    }

    @Nullable
    private Sign findSign() {
        Set<Block> blocksAround = new Region(
                startLocation.clone().add(-1, 0, -1),
                startLocation.clone().add(1, 0, 1)
        ).getBlocksInside();
        List<Sign> signsAround = blocksAround.stream()
                .filter(block -> block.getState() instanceof Sign)
                .map(sign -> (Sign) sign.getState())
                .toList();
        Optional<Sign> signWithBestTime = signsAround.stream()
                .filter(sign -> sign.getPersistentDataContainer().has(BEST_TIME_KEY, PersistentDataType.LONG))
                .findFirst();
        return signWithBestTime.orElseGet(() ->
                signsAround.stream().findFirst().orElse(null)
        );
    }

    private String formatTime() {
        DecimalFormat format = new DecimalFormat("0.00");
        format.setRoundingMode(RoundingMode.FLOOR);
        double seconds = timer / 20.0;
        return format.format(seconds);
    }

}
