package ru.greenbudgie.drop;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.scoreboard.Scoreboard;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.drop.marker.DropMarker;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.mutator.manager.MutatorManager;
import ru.greenbudgie.util.ChatHelper;
import ru.greenbudgie.util.LocationFormatter;
import ru.greenbudgie.util.item.ItemUtils;
import ru.greenbudgie.util.item.Localizer;
import ru.greenbudgie.util.weighted.WeightedItemList;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public abstract class Drop {

    protected int timer;
    protected Location location;
    protected ItemStack itemToDrop;
    protected boolean isAnnounced = false;

    @Nullable
    protected DropMarker<?> currentMarker = null;
    protected final Set<DropMarker<?>> markers = new HashSet<>();

    public Drop() {
        Drops.DROPS.add(this);
    }

    public abstract String getName();
    public abstract int getDefaultDropDelay();
    public abstract int getFirstDropDelay();
    public abstract void drop();
    public abstract Location getRandomLocation();
    public abstract ChatColor getMarkerColor();
    public abstract DropMarker<?> createMarker();
    public abstract WeightedItemList getWeightedItemList();
    public abstract Material getRepresentingItem();

    public void setup() {
        timer = getDefaultDropDelay();
        if(MutatorManager.moreDrops.isActive())
            timer /= 2;
        location = getRandomLocation();
        currentMarker = createMarker();
        markers.add(currentMarker);
        itemToDrop = getWeightedItemList().getRandomElementWeighted().getItem();
        isAnnounced = false;
    }

    public World.Environment getSpawnEnvironment() {
        return World.Environment.NORMAL;
    }

    public void update() {
        if (!isAnnounced && timer <= getDefaultDropDelay() / 2 && timer > 0) {
            announceItemInChat(true);
        }
    }

    public void announceItemInChat(boolean isDroppingSoon) {
        isAnnounced = true;

        var meta = itemToDrop.getItemMeta();
        if (meta == null) {
            UHCPlugin.error("Item to drop has no meta");
            return;
        }

        var vertLine = DARK_GRAY + "" + BOLD + "∫ " + RESET;
        var prefix = isDroppingSoon
                ? vertLine + getName() + WHITE + " скоро выпадет: "
                : vertLine;
        var amount = itemToDrop.getAmount() > 1
                ? AQUA + "" + BOLD + itemToDrop.getAmount() + RESET + " "
                : "";
        var name = ItemUtils.getLocalizedName(itemToDrop, GOLD);

        for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
            ChatHelper.send(p, prefix, amount, name);

            for (var enchantment : itemToDrop.getEnchantments().keySet()) {
                var level = itemToDrop.getEnchantmentLevel(enchantment);
                p.sendMessage(vertLine + " " + GRAY + Localizer.localize(enchantment, level));
            }

            if (meta instanceof PotionMeta potionMeta) {
                for (var effect : potionMeta.getCustomEffects()) {
                    p.sendMessage(vertLine + " " + Localizer.localizePotionEffect(effect));
                }
            }

            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 0.5F);
        }
    }

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
