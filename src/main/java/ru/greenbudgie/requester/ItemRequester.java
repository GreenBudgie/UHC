package ru.greenbudgie.requester;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.items.RequesterCustomItem;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.mutator.MutatorManager;
import ru.greenbudgie.util.InventoryHelper;
import ru.greenbudgie.util.NumericalCases;
import ru.greenbudgie.util.ParticleUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.ChatColor.*;

public class ItemRequester implements Listener {

	public static final String REQUEST_SYMBOL = DARK_AQUA + "" + BOLD + "☁";
	
	public static final NumericalCases REDSTONE_CASES = new NumericalCases("редстоун", "редстоуна", "редстоуна");
	public static final NumericalCases LAPIS_CASES = new NumericalCases("лазурит", "лазурита", "лазурита");
	public static List<RequestedItem> requestedItems = new ArrayList<>();
	public static Map<Integer, RequesterCustomItem> requesterCustomItems = new HashMap<>();
	private static final String INVENTORY_NAME = padSymbols(DARK_AQUA + "Запросы");

	public static void init() {
		putItem(CustomItems.shulkerBox, 11);
		putItem(CustomItems.iceball, 12);
		putItem(CustomItems.highlighter, 13);
		putItem(CustomItems.creatureHighlighter, 14);
		putItem(CustomItems.infernalLead, 15);

		putItem(CustomItems.pearl, 19);
		putItem(CustomItems.booster, 20);
		putItem(CustomItems.pulsatingTotem, 21);
		putItem(CustomItems.knockoutTotem, 23);
		putItem(CustomItems.landmine, 24);
		putItem(CustomItems.soulscriber, 25);

		putItem(CustomItems.shieldBreaker, 30);
		putItem(CustomItems.terraDrill, 31);
		putItem(CustomItems.allurementStone, 32);

		putItem(CustomItems.tnt, 39);
		putItem(CustomItems.heavenMembrane, 40);
		putItem(CustomItems.tracker, 41);
	}

	public static String padSymbols(String input) {
		return REQUEST_SYMBOL + " " + input + RESET + " " + REQUEST_SYMBOL;
	}

	private static void putItem(RequesterCustomItem item, int slot) {
		requesterCustomItems.put(slot, item);
	}

	private static final int[] redstoneDecorativeSlots = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26};
	private static final int[] lapisDecorativeSlots = new int[] {27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};

	private static boolean isPreview(Player player) {
		return !PlayerManager.isPlaying(player) || !UHC.state.isInGame();
	}

	public static void openRequesterInventory(Player player) {
		boolean preview = isPreview(player);
		Inventory inventory;
		if(preview) {
			inventory = Bukkit.createInventory(player, 54, INVENTORY_NAME);
		} else {
			inventory = Bukkit.createInventory(player, 54,
					INVENTORY_NAME + DARK_GRAY + " (" +
							RED + getRedstone(player) +
							DARK_GRAY + " / " +
							BLUE + getLapis(player) +
							DARK_GRAY + ")");
		}
		ItemStack redstone = new ItemStack(Material.REDSTONE);
		for(int redstoneSlot : redstoneDecorativeSlots) {
			inventory.setItem(redstoneSlot, redstone);
		}
		ItemStack lapis = new ItemStack(Material.LAPIS_LAZULI);
		for(int lapisSlot : lapisDecorativeSlots) {
			inventory.setItem(lapisSlot, lapis);
		}
		for(int slot : requesterCustomItems.keySet()) {
			RequesterCustomItem item = requesterCustomItems.get(slot);
			inventory.setItem(slot, preview ? item.getPreviewItemStack() : item.getInGameItemStack(player));
		}
		player.openInventory(inventory);
	}

	public static int getRedstone(Player p) {
		return InventoryHelper.getItemCount(p, Material.REDSTONE);
	}

	public static int getLapis(Player p) {
		return InventoryHelper.getItemCount(p, Material.LAPIS_LAZULI);
	}

	public static void removeMaterials(Player p, int redstone, int lapis) {
		InventoryHelper.removeItemsLimited(p, Material.REDSTONE, redstone);
		InventoryHelper.removeItemsLimited(p, Material.LAPIS_LAZULI, lapis);
	}

	public static void request(Player requester, ItemStack item) {
		CustomItem customItem = CustomItems.getCustomItem(item);
		if(customItem instanceof RequesterCustomItem requesterItem) {
			if(requesterItem.canRequest(requester)) {
				requester.getWorld().playSound(requester.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1F, 0.5F);
				if(!MutatorManager.requestAnywhere.isActive()) {
					Firework firework = (Firework) requester.getWorld().spawnEntity(requester.getLocation(), EntityType.FIREWORK);
					FireworkMeta meta = firework.getFireworkMeta();
					meta.setPower(2);
					meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withFade(Color.BLACK).build());
					firework.setFireworkMeta(meta);
					firework.setMetadata("request", new FixedMetadataValue(UHCPlugin.instance, true));
				}
				ParticleUtils.createParticlesInsideSphere(requester.getLocation(), 3, Particle.TOTEM, null, 30);
				int lapisPrice = MutatorManager.simpleRequests.isActive() ? 0 : requesterItem.getLapisPrice();
				removeMaterials(requester, requesterItem.getRedstonePrice(), lapisPrice);
				RequestedItem requestedItem = new RequestedItem(requester.getLocation(), requesterItem.getItemStack());
				requestedItem.announce(requester);
				requestedItems.add(requestedItem);
				requester.closeInventory();
			} else {
				requester.playSound(requester.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 0.5F);
			}
		}
	}

	public static void updateItems() {
		requestedItems.removeIf(RequestedItem::isDone);
		for(RequestedItem item : requestedItems) {
			item.update();
		}
	}

	@EventHandler
	public void openInventory(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack item = e.getItem();
		if(PlayerManager.isPlaying(player) && UHC.state.isInGame() && item != null && item.getType() == Material.REDSTONE && (e.getAction() == Action.RIGHT_CLICK_BLOCK
				|| e.getAction() == Action.RIGHT_CLICK_AIR)) {
			openRequesterInventory(player);
			player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.5F, 1.5F);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if(e.getView().getTitle().startsWith(INVENTORY_NAME)) {
			if(!isPreview(player) && e.getClickedInventory() != null && e.getClickedInventory() == e.getView().getTopInventory()) {
				ItemStack item = e.getCurrentItem();
				if(item != null && item.getType() != Material.AIR) {
					request(player, item);
				}
			}
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void noRocketDamage(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Firework rocket && event.getEntity() instanceof Player player) {
			if(PlayerManager.isPlaying(player)) {
				if(rocket.hasMetadata("request")) event.setCancelled(true);
			}
		}
	}

}
