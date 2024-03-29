package ru.greenbudgie.block;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;

public abstract class CustomBlockTotem extends CustomBlockItem {

    protected final UHCPlayer owner;

    public CustomBlockTotem(Location location, Player owner) {
        super(location);
        this.owner = PlayerManager.asUHCPlayer(owner);
    }

    public abstract void produceEffect();
    public abstract int getEffectDuration();
    public abstract double getEffectRadius();

    public void onEffectStop() {}

    public boolean arePlayersImmuneWithNoPVP() {
        return true;
    }

    public boolean isImmune(Player player) {
        if(!player.getWorld().getPVP() && arePlayersImmuneWithNoPVP()) return true;
        UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
        if(uhcPlayer == null) return false;
        UHCPlayer teammate = uhcPlayer.getTeammate();
        if(teammate != null && teammate == owner) return true;
        return uhcPlayer == owner;
    }

    @Override
    public void onUpdate() {
        if(ticksPassed < getEffectDuration()) {
            produceEffect();
        } else {
            onEffectStop();
            remove();
        }
    }

    @Override
    public boolean isUnbreakable() {
        return true;
    }

}
