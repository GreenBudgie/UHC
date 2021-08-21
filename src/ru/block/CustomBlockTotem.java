package ru.block;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.UHC.PlayerManager;
import ru.UHC.UHCPlayer;

public abstract class CustomBlockTotem extends CustomBlockItem {

    private UHCPlayer owner;

    public CustomBlockTotem(Location location, Player owner) {
        super(location);
        this.owner = PlayerManager.asUHCPlayer(owner);
    }

}
