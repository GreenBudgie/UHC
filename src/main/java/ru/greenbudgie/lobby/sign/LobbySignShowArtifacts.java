package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.artifact.ArtifactManager;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;

public class LobbySignShowArtifacts extends LobbySign {

    @Override
    public String getConfigName() {
        return "SHOW_ARTIFACTS";
    }

    @Override
    public boolean canBeUsedByAnyone() {
        return true;
    }

    @Override
    public boolean canUseWhilePlaying() {
        return true;
    }

    @Override
    public void onClick(Player clicker, Sign sign, PlayerInteractEvent event) {
        ArtifactManager.openArtifactInventory(clicker);
    }

    @Override
    public void updateText(Sign sign) {
        var side = sign.getSide(Side.FRONT);
        side.setLine(1, RED + "Артефакты");
        side.setLine(2, GRAY + "/artifacts");
    }

}
