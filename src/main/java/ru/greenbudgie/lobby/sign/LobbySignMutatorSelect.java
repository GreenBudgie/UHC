package ru.greenbudgie.lobby.sign;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.mutator.InventoryBuilderMutator;

import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.GRAY;

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
        var side = sign.getSide(Side.FRONT);
        side.setLine(1, DARK_PURPLE + "Мутаторы");
        side.setLine(2, GRAY + "/mutator");
    }

}
