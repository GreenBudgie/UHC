package ru.greenbudgie.items;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomItems {

	public static List<CustomItem> items = new ArrayList<>();
	public static CustomItemHighlighter highlighter = new CustomItemHighlighter();
	public static CustomItemLandmine landmine = new CustomItemLandmine();
	public static CustomItemHarmlessPearl pearl = new CustomItemHarmlessPearl();
	public static CustomItemInstantTnt tnt = new CustomItemInstantTnt();
	public static CustomItemPlayerTracker tracker = new CustomItemPlayerTracker();
	public static CustomItemBooster booster = new CustomItemBooster();
	public static CustomItemSoulscriber soulscriber = new CustomItemSoulscriber();
	public static CustomItemShieldBreaker shieldBreaker = new CustomItemShieldBreaker();
	public static CustomItemDarkArtifact darkArtifact = new CustomItemDarkArtifact();
	public static CustomItemHeavenMembrane heavenMembrane = new CustomItemHeavenMembrane();
	public static CustomItemCreatureHighlighter creatureHighlighter = new CustomItemCreatureHighlighter();
	public static CustomItemShulkerBox shulkerBox = new CustomItemShulkerBox();
	public static CustomItemTerraDrill terraDrill = new CustomItemTerraDrill();
	public static CustomItemInfernalLead infernalLead = new CustomItemInfernalLead();
	public static CustomItemKnockoutTotem knockoutTotem = new CustomItemKnockoutTotem();
	public static CustomItemPulsatingTotem pulsatingTotem = new CustomItemPulsatingTotem();
	public static CustomItemInfernalTotem infernalTotem = new CustomItemInfernalTotem();
	public static CustomItemUnderworldEgg underworldEgg = new CustomItemUnderworldEgg();
	public static CustomItemTerraTracer terraTracer = new CustomItemTerraTracer();
	public static CustomItemAncientShard ancientShard = new CustomItemAncientShard();
	public static CustomItemIceball iceball = new CustomItemIceball();
	public static CustomItemAllurementStone allurementStone = new CustomItemAllurementStone();

	public static void init() {
		for(CustomItem item : items) {
			if(item instanceof Listener) {
				Bukkit.getPluginManager().registerEvents((Listener) item, UHCPlugin.instance);
			}
		}
	}

	public static List<CustomItem> getItems() {
		return items;
	}

	public static boolean isCustomItem(ItemStack stack) {
		return stack != null && ItemUtils.hasCustomValue(stack, "customitem");
	}

	public static CustomItem getCustomItem(ItemStack stack) {
		if(!isCustomItem(stack)) return null;
		String identifier = ItemUtils.getCustomValue(stack, "customitem");
		return getByIdentifier(identifier);
	}

	public static CustomItem getByIdentifier(String identifier) {
		for(CustomItem item : getItems()) {
			if(item.getIdentifier().equals(identifier)) {
				return item;
			}
		}
		return null;
	}

	public static CustomItem getByName(String name) {
		for(CustomItem item : getItems()) {
			if(ChatColor.stripColor(item.getName()).startsWith(name.replaceAll("_", " "))) {
				return item;
			}
		}
		return null;
	}

	public static List<String> getAllNames() {
		List<String> list = new ArrayList<>();
		for(CustomItem item : getItems()) {
			list.add(item.getName());
		}
		return list;
	}

}
