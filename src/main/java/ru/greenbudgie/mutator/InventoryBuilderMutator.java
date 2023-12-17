package ru.greenbudgie.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.InventoryHelper;
import ru.greenbudgie.util.NumericalCases;
import ru.greenbudgie.util.item.ItemUtils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.bukkit.ChatColor.*;

public class InventoryBuilderMutator {

	private static final String ARROW_RIGHT_HEAD_LINK = "http://textures.minecraft.net/texture/4ef356ad2aa7b1678aecb88290e5fa5a3427e5e456ff42fb515690c67517b8";
	private static final String ARROW_LEFT_HEAD_LINK = "http://textures.minecraft.net/texture/6e8c3ce2aee6cf2faade7db37bbae73a36627ac1473fef75b410a0af97659f";

	public static final String inventoryName = Mutator.MUTATOR_NAME_COLOR + "Мутаторы";
	private static final Set<InventoryBuilderMutator> builders = new HashSet<>();

	//Some inner inventory constants
	private static final int MUTATOR_ROWS = 4;
	private static final int LINE_ROW = MUTATOR_ROWS;
	private static final int OPTION_ROW = MUTATOR_ROWS + 2;
	private static final int INV_SIZE = OPTION_ROW * 9;
	private static final int SORT_SLOT = INV_SIZE - 6;
	private static final int FILTER_SLOT = INV_SIZE - 4;
	private static final int PAGE_NEXT_SLOT = INV_SIZE - 1;
	private static final int PAGE_PREV_SLOT = INV_SIZE - 2;
	private static final int RESET_SLOT = INV_SIZE - 9;
	private static final int RESET_PREFERRED_SLOT = INV_SIZE - 8;

	private Player player;
	private Sort sort = Sort.DEFAULT;
	private Filter filter = Filter.NONE;
	private boolean op = false;
	private int page = 1; //Starting from 1, not 0

	public static void registerListener() {
		Listener listener = new Listener() {

			@EventHandler
			public void inventoryClick(InventoryClickEvent event) {
				if(InventoryBuilderMutator.checkInventory(event.getView().getTitle()) && event.getCurrentItem() != null) {
					Player player = (Player) event.getWhoClicked();
					InventoryBuilderMutator builder = InventoryBuilderMutator.getBuilder(player);
					builder.handleClick(event);
					event.setCancelled(true);
				}
			}

		};
		Bukkit.getPluginManager().registerEvents(listener, UHCPlugin.instance);
	}

	private int getPages() {
		int maxPageCount = MUTATOR_ROWS * 9;
		return getMutators().size() / maxPageCount + 1;
	}

	public static boolean checkInventory(String title) {
		return title.startsWith(inventoryName);
	}

	private static Set<InventoryBuilderMutator> getOpenedInventories() {
		Set<InventoryBuilderMutator> viewers = new HashSet<>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(checkInventory(player.getOpenInventory().getTitle())) viewers.add(getBuilder(player));
		}
		return viewers;
	}

	public static void reopenAll() {
		getOpenedInventories().forEach(InventoryBuilderMutator::openInventory);
	}

	public static InventoryBuilderMutator getBuilder(Player player) {
		InventoryBuilderMutator builder = builders.stream().filter(b -> b.player == player).findFirst().orElse(null);
		if(builder != null) return builder;
		InventoryBuilderMutator newBuilder = new InventoryBuilderMutator(player);
		builders.add(newBuilder);
		return newBuilder;
	}

	public InventoryBuilderMutator(Player player) {
		this.player = player;
	}

	/**
	 * Filters a list of mutators based on the current builder options
	 * @param mutators Current list of mutators
	 */
	private void filter(List<Mutator> mutators) {
		if(filter == Filter.NONE) return;
		if(filter == Filter.PREFERRED_SELF) {
			mutators.removeIf(mutator -> !mutator.isPreferredBy(player.getName()));
		}
		if(filter == Filter.PREFERRED_ALL) {
			mutators.removeIf(mutator -> !MutatorManager.hasPreferences(mutator));
		}
		if(filter.boundThreat != null) {
			mutators.removeIf(mutator -> mutator.getThreatStatus() != filter.boundThreat);
		}
	}

	/**
	 * Sorts a list of mutators based on the current builder options
	 * @param mutators Current list of mutators
	 */
	private void sort(List<Mutator> mutators) {
		if(sort == Sort.DEFAULT) return;
		if(sort == Sort.THREAT) {
			Comparator<Mutator> comparator = Comparator.comparingInt(mutator -> mutator.getThreatStatus().ordinal());
			mutators.sort(comparator);
		}
		if(sort == Sort.PERCENT) {
			Comparator<Mutator> comparator = Comparator.comparingInt(MutatorManager::getPreferencePercent).reversed();
			mutators.sort(comparator);
		}
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

	private String getPagesInfo(int pages) {
		return DARK_GREEN + "" + BOLD + page + DARK_GRAY + " / " + DARK_GREEN + pages;
	}

	private void placeOptionalItems(Inventory inventory) {
		ItemStack sortItem = ItemUtils.builder(sort.itemToShow)
				.withName(GOLD + "Сортировка")
				.withLore(YELLOW + "" + BOLD + sort.description)
				.build();
		inventory.setItem(SORT_SLOT, sortItem);

		ItemStack filterItem = ItemUtils.builder(filter.itemToShow)
				.withName(DARK_AQUA + "Фильтр")
				.withLore(AQUA + "" + BOLD + filter.description)
				.build();
		inventory.setItem(FILTER_SLOT, filterItem);

		ItemStack pgNextItem = InventoryHelper.generateHead(ARROW_RIGHT_HEAD_LINK);
		ItemUtils.setName(pgNextItem, DARK_GREEN + "" + BOLD + ">>> " + AQUA + "След. страница");
		inventory.setItem(PAGE_NEXT_SLOT, pgNextItem);

		ItemStack pgPrevItem = InventoryHelper.generateHead(ARROW_LEFT_HEAD_LINK);
		ItemUtils.setName(pgPrevItem, GOLD + "" + BOLD + "<<< " + DARK_AQUA + "Пред. страница");
		inventory.setItem(PAGE_PREV_SLOT, pgPrevItem);

		inventory.setItem(RESET_SLOT, ItemUtils.builder(Material.BARRIER).withName(DARK_RED + "" + BOLD + "Сбросить настройки").build());
		inventory.setItem(RESET_PREFERRED_SLOT, ItemUtils.builder(Material.RED_DYE).withName(RED + "" + BOLD + "Убрать предпочтения").build());
	}

	private ItemStack getMutatorItem(Mutator mutator) {
		ItemStack item = ItemUtils.builder(mutator.getItemToShow())
				.withName(Mutator.MUTATOR_NAME_COLOR + mutator.getName())
				.withSplittedLore(GRAY + mutator.getDescription())
				.build();
		if(mutator.canBeHidden()) ItemUtils.addLore(item, WHITE + "" + ITALIC + "Может быть скрыт");
		if(isOP()) {
			if(mutator.isActive()) {
				ItemUtils.addGlow(item);
				ItemUtils.addLore(item, false, RED + "" + BOLD + "<ДЕАКТИВИРОВАТЬ>");
			} else {
				ItemUtils.addLore(item, false, GREEN + "" + BOLD + "<АКТИВИРОВАТЬ>");
			}
		} else {
			if(mutator.isPreferredBy(player.getName())) {
				ItemUtils.addGlow(item);
				ItemUtils.addLore(item, false, DARK_PURPLE + "" + BOLD + "Предпочитаемый");
			} else {
				ItemUtils.addLore(item, false, DARK_AQUA + "<Сделать предпочитаемым>");
			}
			int preferenceCount = MutatorManager.getPlayersWhoPrefersMutator(mutator).size();
			if(preferenceCount == 0) {
				ItemUtils.addLore(item, false, GOLD + "Нет предпочтений");
			} else {
				String prefer = new NumericalCases("Предпочитает ", "Предпочитают ", "Предпочитают ").byNumber(preferenceCount);
				String player = new NumericalCases(" игрок", " игрока", " игроков").byNumber(preferenceCount);
				List<Mutator> otherMutators = MutatorManager.getAvailablePreferredMutatorsWeighted();
				otherMutators.removeIf(m -> mutator == m);
				double otherSize = otherMutators.size();
				int percent = (int) ((1 - (otherSize / MutatorManager.getAvailablePreferredMutatorsWeighted().size())) * 100);
				ItemUtils.addLore(item, false,
						GOLD + prefer + AQUA + BOLD + preferenceCount + RESET +
								GOLD + player + GRAY + ", " + GREEN + "шанс " + DARK_GREEN +
								BOLD + percent + RESET + GRAY + "%");
			}
		}
		if(mutator.conflictsWithClasses())
			ItemUtils.addLore(item, DARK_RED + "" + BOLD + "Недоступен при игре с классами");
		return item;
	}

	public boolean isOP() {
		return op;
	}

	public void setOP(boolean op) {
		this.op = op;
	}

	public void openInventory() {
		List<Mutator> mutators = getMutators();
		int pages = getPages();

		Inventory inventory = Bukkit.createInventory(player, INV_SIZE, inventoryName + " " + getPagesInfo(pages));
		int realPage = page - 1;
		int mutatorLength = MUTATOR_ROWS * 9;

		for(int index = realPage * mutatorLength, slot = 0; slot < mutatorLength && index < mutators.size(); index++, slot++) {
			Mutator mutator = mutators.get(index);
			inventory.setItem(slot, getMutatorItem(mutator));
		}

		ItemStack linePanel = ItemUtils.builder(Material.GRAY_STAINED_GLASS_PANE).withName(" ").build();
		IntStream.range(LINE_ROW * 9, (LINE_ROW + 1) * 9).forEach(sl -> inventory.setItem(sl, linePanel));

		placeOptionalItems(inventory);
		player.openInventory(inventory);
	}

	public void resetPage() {
		page = 1;
	}

	public void nextPage() {
		if(++page > getPages()) page = 1;
	}

	public void prevPage() {
		if(--page < 1) page = getPages();
	}

	public void handleClick(InventoryClickEvent event) {
		int slot = event.getRawSlot();
		ItemStack item = event.getCurrentItem();
		boolean clickedMutator = slot < MUTATOR_ROWS * 9;
		if(clickedMutator) {
			if(isOP()) {
				for(Mutator mutator : MutatorManager.mutators) {
					if(item.getType() == mutator.getItemToShow()) {
						if(mutator.isActive()) {
							player.sendMessage(GOLD + "Деактивирован мутатор: " + LIGHT_PURPLE + mutator.getName());
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
							mutator.deactivate();
							player.closeInventory();
						} else {
							if(MutatorManager.doesMutatorConflictsWithActive(mutator)) {
								player.sendMessage(RED + "Мутатор конфликтует с активными");
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
							} else {
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 1F);
								mutator.activate(false, null);
								player.closeInventory();
							}
						}
						break;
					}
				}
			} else {
				for(Mutator mutator : MutatorManager.mutators) {
					if(item.getType() == mutator.getItemToShow()) {
						if(mutator.isPreferredBy(player.getName())) {
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
							MutatorManager.setPreference(player.getName(), mutator, false);
							reopenAll();
						} else {
							if(MutatorManager.preferredMutators.getOrDefault(player.getName(), new HashSet<>()).size() >= 3) {
								InventoryHelper.sendActionBarMessage(player, DARK_RED + "" + BOLD + "Нельзя выбрать более трех предпочитаемых мутаторов");
								player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
							} else {
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 1F);
								MutatorManager.setPreference(player.getName(), mutator, true);
								reopenAll();
							}
						}
						break;
					}
				}
			}
		} else {
			boolean reopen = true;
			switch(slot) {
				case RESET_SLOT -> {
					sort = Sort.DEFAULT;
					filter = Filter.NONE;
					resetPage();
				}
				case RESET_PREFERRED_SLOT -> MutatorManager.clearPreferences(player.getName());
				case SORT_SLOT -> {
					int sortIndex = sort.ordinal() + 1;
					if(sortIndex >= Sort.values().length) sortIndex = 0;
					sort = Sort.values()[sortIndex];
					resetPage();
				}
				case FILTER_SLOT -> {
					int filterIndex = filter.ordinal() + 1;
					if(filterIndex >= Filter.values().length) filterIndex = 0;
					filter = Filter.values()[filterIndex];
					resetPage();
				}
				case PAGE_PREV_SLOT -> nextPage();
				case PAGE_NEXT_SLOT -> prevPage();
				default -> reopen = false;
			}
			if(reopen) {
				openInventory();
			}
		}
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
