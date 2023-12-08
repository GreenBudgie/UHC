package ru.greenbudgie.lobby.game.parkour;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
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
import static ru.greenbudgie.lobby.game.parkour.LobbyGameParkour.BEST_TIME_METADATA_KEY;

public class ParkourSession {

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
        Block sign = findSign();
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

    private void updateBestTime(Block sign) {
        sign.setMetadata(BEST_TIME_METADATA_KEY, new FixedMetadataValue(UHCPlugin.instance, timer));
        Sign signState = (Sign) sign.getState();
        SignSide side = signState.getSide(Side.FRONT);
        side.setLine(0, GRAY + "Лучшее время");
        side.setLine(1, GOLD + player.getName());
        side.setLine(2, GRAY + "< " + AQUA + BOLD + formatTime() + RESET + GRAY + " >");
        signState.update();
    }

    private boolean isBestTime(Block sign) {
        Optional<Long> previousBestTime = sign.getMetadata(BEST_TIME_METADATA_KEY).stream()
                .findFirst()
                .map(MetadataValue::asLong);
        return previousBestTime.map(prevBestTime -> timer < prevBestTime).orElse(true);
    }

    @Nullable
    private Block findSign() {
        Set<Block> blocksAround = new Region(
                startLocation.clone().add(-1, 0, -1),
                startLocation.clone().add(1, 0, 1)
        ).getBlocksInside();
        List<Block> signsAround = blocksAround.stream()
                .filter(block -> block.getState() instanceof Sign)
                .toList();
        Optional<Block> signWithBestTime = signsAround.stream()
                .filter(sign -> sign.hasMetadata(BEST_TIME_METADATA_KEY))
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
