package ru.lobby.sign;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.UHC.UHC;
import ru.lobby.LobbyTeamBuilder;
import ru.mutator.InventoryBuilderMutator;

public class LobbySignMutatorSelect extends LobbySign {

    @Override
    public String getConfigName() {
        return "MUTATOR_SELECT";
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
        InventoryBuilderMutator builder = InventoryBuilderMutator.getBuilder(clicker);
        builder.setOP(false);
        builder.openInventory();
    }

    @Override
    public void updateText(Sign sign) {
        sign.setLine(1, ChatColor.DARK_PURPLE + "Мутаторы");
        sign.setLine(2, ChatColor.GRAY + "/mutator");
    }

}
