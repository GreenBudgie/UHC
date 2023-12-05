package ru.lobby;

import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.util.InventoryHelper;
import ru.util.ItemUtils;

import java.util.HashMap;
import java.util.Map;

public class Kit {

	private Map<Integer, ItemStack> itemMap = new HashMap<>();
	private int n = 0;
	private final String name;

	public Kit(String name) {
		this.name = name;
		LobbyGameManager.PVP_ARENA.getKits().add(this);
	}

	public String getName() {
		return name;
	}

	public void give(Player p) {
		for(Integer slot : itemMap.keySet()) {
			ItemStack item = itemMap.get(slot);
			p.getInventory().setItem(slot, item);
		}
	}
	
	public void addArmorSet(ArmorMaterial armor) {
		switch(armor) {
			case LEATHER -> {
				addHelmet(Material.LEATHER_HELMET);
				addChestplate(Material.LEATHER_CHESTPLATE);
				addLeggings(Material.LEATHER_LEGGINGS);
				addBoots(Material.LEATHER_BOOTS);
			}
			case CHAIN -> {
				addHelmet(Material.CHAINMAIL_HELMET);
				addChestplate(Material.CHAINMAIL_CHESTPLATE);
				addLeggings(Material.CHAINMAIL_LEGGINGS);
				addBoots(Material.CHAINMAIL_BOOTS);
			}
			case GOLD -> {
				addHelmet(Material.GOLDEN_HELMET);
				addChestplate(Material.GOLDEN_CHESTPLATE);
				addLeggings(Material.GOLDEN_LEGGINGS);
				addBoots(Material.GOLDEN_BOOTS);
			}
			case IRON -> {
				addHelmet(Material.IRON_HELMET);
				addChestplate(Material.IRON_CHESTPLATE);
				addLeggings(Material.IRON_LEGGINGS);
				addBoots(Material.IRON_BOOTS);
			}
			case DIAMOND -> {
				addHelmet(Material.DIAMOND_HELMET);
				addChestplate(Material.DIAMOND_CHESTPLATE);
				addLeggings(Material.DIAMOND_LEGGINGS);
				addBoots(Material.DIAMOND_BOOTS);
			}
			case NETHERITE -> {
				addHelmet(Material.NETHERITE_HELMET);
				addChestplate(Material.NETHERITE_CHESTPLATE);
				addLeggings(Material.NETHERITE_LEGGINGS);
				addBoots(Material.NETHERITE_BOOTS);
			}
		}
	}

	public void putItem(Material type, int slot) {
		if(EnchantmentTarget.BREAKABLE.includes(type)) {
			itemMap.put(slot, ItemUtils.setUnbreakable(new ItemStack(type)));
		} else {
			itemMap.put(slot, new ItemStack(type));
		}
	}

	public void addItem(Material type) {
		if(EnchantmentTarget.BREAKABLE.includes(type)) {
			itemMap.put(n++, ItemUtils.setUnbreakable(new ItemStack(type)));
		} else {
			itemMap.put(n++, new ItemStack(type));
		}
	}

	public void putItem(ItemStack item, int slot) {
		if(EnchantmentTarget.BREAKABLE.includes(item.getType())) {
			itemMap.put(slot, ItemUtils.setUnbreakable(item));
		} else {
			itemMap.put(slot, item);
		}
	}

	public void addItem(ItemStack item) {
		if(EnchantmentTarget.BREAKABLE.includes(item.getType())) {
			itemMap.put(n++, ItemUtils.setUnbreakable(item));
		} else {
			itemMap.put(n++, item);
		}
	}

	public void addHelmet(Material type) {
		itemMap.put(InventoryHelper.getHelmetSlot(), ItemUtils.setUnbreakable(new ItemStack(type)));
	}

	public void addChestplate(Material type) {
		itemMap.put(InventoryHelper.getChestplateSlot(), ItemUtils.setUnbreakable(new ItemStack(type)));
	}

	public void addLeggings(Material type) {
		itemMap.put(InventoryHelper.getLeggingsSlot(), ItemUtils.setUnbreakable(new ItemStack(type)));
	}

	public void addBoots(Material type) {
		itemMap.put(InventoryHelper.getBootsSlot(), ItemUtils.setUnbreakable(new ItemStack(type)));
	}

	public void withShield() {
		itemMap.put(InventoryHelper.getOffHandSlot(), ItemUtils.setUnbreakable(new ItemStack(Material.SHIELD)));
	}

	public enum ArmorMaterial {
		LEATHER, CHAIN, GOLD, IRON, DIAMOND, NETHERITE;
	}

}
