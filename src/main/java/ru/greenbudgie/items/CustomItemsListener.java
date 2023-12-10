package ru.greenbudgie.items;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.ArenaManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.util.Messages;

public class CustomItemsListener implements Listener {

	@EventHandler
	public void use(PlayerInteractEvent e) {
		if(e.getItem() != null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().hasDisplayName()) {
			for(CustomItem item : CustomItems.getItems()) {
				if(item.isEquals(e.getItem())) {
					boolean lAir = e.getAction() == Action.LEFT_CLICK_AIR;
					boolean rAir = e.getAction() == Action.RIGHT_CLICK_AIR;
					boolean lBlock = e.getAction() == Action.LEFT_CLICK_BLOCK;
					boolean rBlock = e.getAction() == Action.RIGHT_CLICK_BLOCK;
					if(lAir) {
						item.onUseLeftAir(e.getPlayer(), e.getItem(), e);
						item.onUseLeft(e.getPlayer(), e.getItem(), e);
					}
					if(rAir) {
						item.onUseRightAir(e.getPlayer(), e.getItem(), e);
						item.onUseRight(e.getPlayer(), e.getItem(), e);
					}
					if(lBlock) {
						item.onUseLeftBlock(e.getPlayer(), e.getItem(), e.getClickedBlock(), e);
						item.onUseLeft(e.getPlayer(), e.getItem(), e);
					}
					if(rBlock) {
						item.onUseRightBlock(e.getPlayer(), e.getItem(), e.getClickedBlock(), e);
						item.onUseRight(e.getPlayer(), e.getItem(), e);
					}
				}
			}
		}
	}

	@EventHandler
	public void breakBlock(BlockBreakEvent e) {
		ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
		if(stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			for(CustomItem item : CustomItems.getItems()) {
				if(item.isEquals(stack)) {
					item.onBreak(e.getPlayer(), stack, e);
				}
			}
		}
	}
	
	@EventHandler
	public void place(BlockPlaceEvent e) {
		ItemStack stack = e.getItemInHand();
		if(stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			for(CustomItem item : CustomItems.getItems()) {
				if(item.isEquals(stack)) {
					item.onPlace(e.getPlayer(), e.getBlock(), e.getItemInHand(), e);
					if(item instanceof BlockHolder holder) {
						boolean toCancel = true;
						boolean pvpIsOn = ArenaManager.getCurrentArena().getWorld().getPVP();
						boolean isDeathmatch = UHC.state.isDeathmatch();
						boolean canPlaceNow = UHC.state.isBeforeDeathmatch();
						if(canPlaceNow || (isDeathmatch && holder.canPlaceOnDeathmatch() && pvpIsOn)) {
							toCancel = !holder.placeBlock(e.getBlock().getLocation(), e.getPlayer());
						}
						if(toCancel) {
							e.getPlayer().sendMessage(Messages.CANNOT_INTERACT_WITH_ARENA_NOW);
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void noRename(PrepareAnvilEvent e) {
		ItemStack stack = e.getInventory().getItem(0);
		if(stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
			for(CustomItem item : CustomItems.getItems()) {
				if(item.isEquals(stack)) {
					e.setResult(null);
				}
			}
		}
	}

}
