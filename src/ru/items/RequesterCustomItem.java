package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.mutator.Mutator;
import ru.mutator.MutatorManager;
import ru.mutator.MutatorRequestAnywhere;
import ru.requester.ItemRequester;
import ru.util.InventoryHelper;

public abstract class RequesterCustomItem extends CustomItem {

	public abstract String getDescription();

	public abstract int getRedstonePrice();

	public abstract int getLapisPrice();

	@Override
	public boolean isStackable() {
		return true;
	}

	public boolean canRequest(Player p) {
		int lapisPrice = MutatorManager.simpleRequests.isActive() ? 0 : getLapisPrice();
		return (p.getLocation().getBlockY() >= p.getWorld().getHighestBlockYAt(p.getLocation()) || MutatorManager.requestAnywhere.isActive()) && ItemRequester.getRedstone(p) >= getRedstonePrice()
				&& ItemRequester.getLapis(p) >= lapisPrice;
	}

	/**
	 * Gets an ItemStack <b>without</b> description
	 * @return A pure ItemStack, ready to use
	 */
	@Override
	public ItemStack getItemStack() {
		return super.getItemStack();
	}

	/**
	 * Gets an ItemStack with information about price and description
	 * @param p Player to generate an info item for
	 * @return Informational ItemStack
	 */
	public ItemStack getInfoItemStack(Player p) {
		ItemStack item = getItemStack();
		int lapisPrice = MutatorManager.simpleRequests.isActive() ? 0 : getLapisPrice();
		boolean enoughRedstone = ItemRequester.getRedstone(p) >= getRedstonePrice();
		boolean enoughLapis = ItemRequester.getLapis(p) >= lapisPrice;
		boolean allowPos = p.getLocation().getBlockY() >= p.getWorld().getHighestBlockYAt(p.getLocation()) || MutatorManager.requestAnywhere.isActive();
		InventoryHelper.addSplittedLore(item, 25, getDescription(), ChatColor.YELLOW);
		if(getRedstonePrice() > 0) {
			InventoryHelper.addLore(item, ChatColor.AQUA + "" + getRedstonePrice() + ChatColor.RED + " " + ItemRequester.REDSTONE_CASES.byNumber(getRedstonePrice()));
		}
		if(lapisPrice > 0) {
			InventoryHelper.addLore(item, ChatColor.AQUA + "" + lapisPrice + ChatColor.BLUE + " " + ItemRequester.LAPIS_CASES.byNumber(lapisPrice));
		}
		if(enoughRedstone && enoughLapis && allowPos) {
			InventoryHelper.addLore(item, ChatColor.GREEN + "Нажми, чтобы создать запрос");
		} else if(!enoughRedstone) {
			InventoryHelper.addLore(item, ChatColor.RED + "Недостаточно редстоуна");
		} else if(!enoughLapis) {
			InventoryHelper.addLore(item, ChatColor.RED + "Недостаточно лазурита");
		} else {
			InventoryHelper.addLore(item, ChatColor.RED + "Закрытое помещение");
		}
		return item;
	}
}
