package ru.greenbudgie.mutator.trolling;

import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.requester.ItemRequester;
import ru.greenbudgie.requester.RequestedItem;
import ru.greenbudgie.util.MathUtils;

public class TrollingEventRandomRequest extends TrollingEvent {

    @Override
    public String getName() {
        return "Случайный Запрос";
    }

    @Override
    public void execute() {
        ItemStack randomItem = MathUtils.choose(ItemRequester.requesterCustomItems.values()).getItemStack();
        for (UHCPlayer player : PlayerManager.getAlivePlayers()) {
            RequestedItem requestedItem = new RequestedItem(
                    player.getLocation(),
                    randomItem
            );
            requestedItem.announce(null);
            ItemRequester.requestedItems.add(requestedItem);
        }
    }

}
