package ru.items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import ru.main.UHCPlugin;
import ru.util.InventoryHelper;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomItem {

	public CustomItem() {
		CustomItems.getItems().add(this);
	}

	public abstract String getName();

	public abstract Material getMaterial();

	public void onUseRight(Player p, ItemStack item, PlayerInteractEvent e) {
	}

	public void onUseLeft(Player p, ItemStack item, PlayerInteractEvent e) {
	}

	public void onUseRightAir(Player p, ItemStack item, PlayerInteractEvent e) {
	}

	public void onUseLeftAir(Player p, ItemStack item, PlayerInteractEvent e) {
	}

	public void onUseRightBlock(Player p, ItemStack item, Block b, PlayerInteractEvent e) {
	}

	public void onUseLeftBlock(Player p, ItemStack item, Block b, PlayerInteractEvent e) {
	}

	public void onBreak(Player p, ItemStack item, BlockBreakEvent e) {
	}

	public void onPlace(Player p, Block b, ItemStack item, BlockPlaceEvent e) {
	}

	public boolean isStackable() {
		return true;
	}

	public Map<String, Object> getFields() {
		return new HashMap<>();
	}

	public final boolean isEquals(ItemStack item) {
		if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return false;
		return getName().equals(item.getItemMeta().getDisplayName());
	}

	private static String a(String s) {
		char[] arr = s.toCharArray();
		String str = "";
		for(char c : arr) {
			str += " " + c;
		}
		return str;
	}

	public boolean isGlowing() {
		return true;
	}

	public ItemStack getItemStack() {
		ItemStack item = new ItemStack(getMaterial());
		if(getName() != null) {
			InventoryHelper.setName(item, getName());
		}
		for(String key : getFields().keySet()) {
			InventoryHelper.setValue(item, key, getFields().get(key).toString(), false);
		}
		if(!isStackable()) {
			InventoryHelper.setUnstackable(item);
		}
		if(isGlowing()) {
			InventoryHelper.setItemGlowing(item);
		}
		return item;
	}

}