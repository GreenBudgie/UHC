package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.mutator.manager.MutatorManager;
import ru.greenbudgie.requester.ItemRequester;
import ru.greenbudgie.util.InventoryHelper;
import ru.greenbudgie.util.item.ItemInfo;

public abstract class RequesterCustomItem extends CustomItem {

	public abstract ItemInfo getDescription();

	public abstract int getRedstonePrice();

	public abstract int getLapisPrice();

	public boolean canRequest(Player p) {
		var priceData = getActualPriceData(p);
		return priceData.isEnoughRedstone() && priceData.isEnoughLapis();
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
	 * @param player Player to generate an info item for
	 * @return Informational ItemStack
	 */
	public ItemStack getInGameItemStack(Player player) {
		ItemStack item = getItemStack();
		var priceData = getActualPriceData(player);
		getDescription().applyToItem(item);
		if(priceData.redstonePrice() > 0) {
			if (priceData.hasDiscount()) {
				InventoryHelper.addLore(item, ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + getRedstonePrice() + ChatColor.RESET + " " + ChatColor.AQUA + "" + priceData.redstonePrice() + ChatColor.RED + " " + ItemRequester.REDSTONE_CASES.byNumber(priceData.redstonePrice()));
			} else {
				InventoryHelper.addLore(item, ChatColor.AQUA + "" + priceData.redstonePrice() + ChatColor.RED + " " + ItemRequester.REDSTONE_CASES.byNumber(priceData.redstonePrice()));
			}
		}
		if(priceData.lapisPrice() > 0) {
			InventoryHelper.addLore(item, ChatColor.AQUA + "" + priceData.lapisPrice() + ChatColor.BLUE + " " + ItemRequester.LAPIS_CASES.byNumber(priceData.lapisPrice()));
		}
		if(priceData.isEnoughLapis() && priceData.isEnoughRedstone()) {
			InventoryHelper.addLore(item, ChatColor.GREEN + "Нажми, чтобы купить");
		} else if(!priceData.isEnoughRedstone()) {
			InventoryHelper.addLore(item, ChatColor.RED + "Недостаточно редстоуна");
		} else {
			InventoryHelper.addLore(item, ChatColor.RED + "Недостаточно лазурита");
		}
		return item;
	}

	/**
	 * Gets an item stack to show in lobby preview inventory
	 */
	public ItemStack getPreviewItemStack() {
		ItemStack item = getItemStack();
		getDescription().applyToItem(item);
		if(getRedstonePrice() > 0) {
			InventoryHelper.addLore(item, ChatColor.AQUA + "" + getRedstonePrice() + ChatColor.RED + " " + ItemRequester.REDSTONE_CASES.byNumber(getRedstonePrice()));
		}
		if(getLapisPrice() > 0) {
			InventoryHelper.addLore(item, ChatColor.AQUA + "" + getLapisPrice() + ChatColor.BLUE + " " + ItemRequester.LAPIS_CASES.byNumber(getLapisPrice()));
		}
		return item;
	}

	public ActualPriceData getActualPriceData(Player player) {
		var isCheapRequests = MutatorManager.cheapRequests.isActive();
		int lapisPrice = isCheapRequests ? 0 : getLapisPrice();
		int redstonePrice = isCheapRequests ? getRedstonePrice() / 2 : getRedstonePrice();
		boolean enoughRedstone = ItemRequester.getRedstone(player) >= redstonePrice;
		boolean enoughLapis = ItemRequester.getLapis(player) >= lapisPrice;

		return new ActualPriceData(isCheapRequests, lapisPrice, redstonePrice, enoughLapis, enoughRedstone);
	}

	public record ActualPriceData(boolean hasDiscount, int lapisPrice, int redstonePrice, boolean isEnoughLapis, boolean isEnoughRedstone) {
	}

}
