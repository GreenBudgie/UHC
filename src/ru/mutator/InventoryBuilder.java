package ru.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.util.ItemUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryBuilder {

	public static final String inventoryName = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Мутаторы";
	private static Set<InventoryBuilder> builders = new HashSet<>();

	//Some inner inventory constants
	private static final int MUTATOR_ROWS = 4;
	private static final int LINE_ROW = MUTATOR_ROWS + 1;
	private static final int OPTION_ROW = LINE_ROW + 1;
	private static final int INV_SIZE = OPTION_ROW * 9;
	private static final int SORT_SLOT = INV_SIZE - 5;
	private static final int FILTER_SLOT = INV_SIZE - 3;
	private static final int PAGE_NEXT_SLOT = INV_SIZE - 1;
	private static final int PAGE_PREV_SLOT = INV_SIZE - 2;
	private static final int RESET_SLOT = INV_SIZE - 9;
	private static final int RESET_PREFERRED_SLOT = INV_SIZE - 8;

	private Player player;
	private Sort sort = Sort.DEFAULT;
	private Filter filter = Filter.NONE;
	private int page;

	private static Set<InventoryBuilder> getOpenedInventories() {
		Set<InventoryBuilder> viewers = new HashSet<>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getOpenInventory().getTitle().equals(inventoryName)) viewers.add(getBuilder(player));
		}
		return viewers;
	}

	public static void reopenAll() {
		getOpenedInventories().forEach(InventoryBuilder::openInventory);
	}

	public static InventoryBuilder getBuilder(Player player) {
		InventoryBuilder builder = builders.stream().filter(b -> b.player == player).findFirst().orElse(null);
		if(builder != null) return builder;
		return new InventoryBuilder(player);
	}

	public InventoryBuilder(Player player) {
		this.player = player;
	}

	/**
	 * Filters a list of mutators based on the current builder options
	 * @param mutators Current list of mutators
	 */
	private void filter(List<Mutator> mutators) {

	}

	/**
	 * Sorts a list of mutators based on the current builder options
	 * @param mutators Current list of mutators
	 */
	private void sort(List<Mutator> mutators) {

	}

	/**
	 * Generates a list of mutators to place in inventory using the current builder options
	 * @return List of mutators
	 */
	private List<Mutator> getMutators() {
		List<Mutator> mutators = Lists.newArrayList(MutatorManager.mutators);
		sort(mutators);
		filter(mutators);
		return mutators;
	}

	private void placeOptionalItems(Inventory inventory) {
		ItemStack sortItem = ItemUtils.builder(sort.itemToShow)
				.withName(ChatColor.GOLD + "Сортировка")
				.withLore(ChatColor.YELLOW + "" + ChatColor.BOLD + sort.description)
				.build();
		inventory.setItem(SORT_SLOT, sortItem);

		ItemStack filterItem = ItemUtils.builder(filter.itemToShow)
				.withName(ChatColor.DARK_PURPLE + "Фильтр")
				.withLore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + filter.description)
				.build();
		inventory.setItem(FILTER_SLOT, filterItem);

		//TODO Add others
	}

	public void openInventory() {

	}

	public enum Sort {
		DEFAULT("Обычная", Material.COMPARATOR),
		THREAT("По сложности", Material.CREEPER_HEAD),
		PERCENT("По проценту предпочтений", Material.PLAYER_HEAD);

		private String description;
		private Material itemToShow;

		Sort(String description, Material itemToShow) {
			this.description = description;
			this.itemToShow = itemToShow;
		}

	}

	public enum Filter {
		NONE("Нет", Material.PAPER),
		PREFERRED_SELF("Предпочитаемые тобой", Material.BOOK),
		PREFERRED_ALL("Предпочитаемые всеми", Material.WRITABLE_BOOK),
		THREAT_SUPPORTING("Вспомогательные", Material.LIGHT_BLUE_DYE, ThreatStatus.SUPPORTING),
		THREAT_INNOCENT("Безобидные", Material.LIME_DYE, ThreatStatus.INNOCENT),
		THREAT_DANGEROUS("Опасные", Material.RED_DYE, ThreatStatus.DANGEROUS),
		THREAT_CRITICAL("Дикие", Material.WEEPING_VINES, ThreatStatus.CRITICAL);

		private String description;
		private ThreatStatus boundThreat;
		private Material itemToShow;

		Filter(String description, Material itemToShow) {
			this(description, itemToShow, null);
		}

		Filter(String description, Material itemToShow, ThreatStatus boundThreat) {
			this.description = description;
			this.itemToShow = itemToShow;
			this.boundThreat = boundThreat;
		}

	}

}
