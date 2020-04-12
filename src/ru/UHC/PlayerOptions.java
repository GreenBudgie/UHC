package ru.UHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.util.ItemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum PlayerOptions {

	SHOW_TEAMS(true, Material.NAME_TAG, "Показывать тимы", "Отображать длинный список из тим справа экрана"),
	ONLY_LOCAL(false, Material.OAK_SIGN, "Только локальный чат", "Если включено, то все сообщения во время игры будут уходить в локальный чат");

	public static final String invName = ChatColor.GOLD + "Настройки";
	private Map<Player, Boolean> values = new HashMap<>();
	private boolean defaultValue;
	private Material item;
	private String name, description;

	public static void openInventory(Player p) {
		Inventory inv = Bukkit.createInventory(p, 9, invName);
		inv.addItem(Stream.of(PlayerOptions.values()).map(option -> option.getItemToShow(p)).toArray(ItemStack[]::new));
		p.openInventory(inv);
	}

	PlayerOptions(boolean defaultValue, Material item, String name, @Nullable String description) {
		this.defaultValue = defaultValue;
		this.item = item;
		this.name = name;
		this.description = description;
	}

	public boolean isActive(Player player) {
		return values.getOrDefault(player, defaultValue);
	}

	public void setActive(Player player, boolean value) {
		values.put(player, value);
	}

	public Material getItem() {
		return item;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ItemStack getItemToShow(Player player) {
		ItemStack item = new ItemStack(getItem());
		ItemUtils.setName(item, (isActive(player) ? ChatColor.AQUA : ChatColor.DARK_AQUA) + getName());
		if(getDescription() != null) ItemUtils.addSplittedLore(item, ChatColor.GOLD + getDescription());
		if(isActive(player)) {
			ItemUtils.addGlow(item);
			ItemUtils.addLore(item, ChatColor.GREEN + "Включено");
		} else {
			ItemUtils.addLore(item, ChatColor.RED + "Отключено");
		}
		return item;
	}

}
