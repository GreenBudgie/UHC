package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.artifact.ArtifactManager;
import ru.artifact.ArtifactRequest;
import ru.requester.ItemRequester;

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
        sign.setLine(1, ChatColor.RED + "Артефакты");
        sign.setLine(2, ChatColor.GRAY + "/artifacts");
    }

}
