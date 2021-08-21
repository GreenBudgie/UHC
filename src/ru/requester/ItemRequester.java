package ru.requester;

import com.google.common.collect.Lists;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.items.CustomItem;
import ru.items.CustomItems;
import ru.items.RequesterCustomItem;
import ru.main.UHCPlugin;
import ru.mutator.MutatorManager;
import ru.util.InventoryHelper;
import ru.util.NumericalCases;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemRequester implements Listener {

	public static final NumericalCases REDSTONE_CASES = new NumericalCases("редстоун", "редстоуна", "редстоуна");
	public static final NumericalCases LAPIS_CASES = new NumericalCases("лазурит", "лазурита", "лазурита");
	public static List<RequestedItem> requestedItems = new ArrayList<>();
	public static List<RequesterCustomItem> requesterCustomItems = new ArrayList<>();
	private static String name = ChatColor.DARK_AQUA + "Запросы";

	public static void init() {
		requesterCustomItems.addAll(Lists.newArrayList(
				CustomItems.shulkerBox,
				CustomItems.highlighter,
				CustomItems.creatureHighlighter,
				CustomItems.booster,
				CustomItems.pearl,
				CustomItems.infernalLead,
				CustomItems.landmine,
				CustomItems.soulscriber,
				CustomItems.shieldBreaker,
				CustomItems.knockoutTotem,
				CustomItems.tnt,
				CustomItems.heavenMembrane,
				CustomItems.tracker,
				CustomItems.terraDrill));
	}

	public static void openRequesterInventory(Player p) {
		Inventory inv = Bukkit.createInventory(p, 18,
				name + ChatColor.DARK_GRAY + " (" +
						ChatColor.RED + getRedstone(p) +
						ChatColor.DARK_GRAY + " / " +
						ChatColor.BLUE + getLapis(p) +
						ChatColor.DARK_GRAY + ")");
		requesterCustomItems.forEach(item -> inv.addItem(item.getInfoItemStack(p)));
		p.openInventory(inv);
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
		if(customItem instanceof RequesterCustomItem) {
			RequesterCustomItem requesterItem = (RequesterCustomItem) customItem;
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
				for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
					Location requesterLocation = requester.getLocation();
					inGamePlayer.sendMessage(
							ChatColor.LIGHT_PURPLE + "Был сделан запрос: " +
									ChatColor.DARK_AQUA + requesterLocation.getBlockX() +
									ChatColor.WHITE + ", " + ChatColor.DARK_AQUA +
									requesterLocation.getBlockZ() +
									(inGamePlayer == requester ? "" : ChatColor.WHITE + " (" + (requesterLocation.getWorld() == inGamePlayer.getWorld() ?
											(ChatColor.AQUA + String.valueOf((int) requesterLocation.distance(inGamePlayer.getLocation()))) :
											WorldHelper.getEnvironmentNamePrepositional(requesterLocation.getWorld().getEnvironment(), ChatColor.AQUA)) + ChatColor.WHITE + ")"));
				}
				requestedItems.add(new RequestedItem(requester.getLocation(), requesterItem.getItemStack()));
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
	public void openInv(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		if(PlayerManager.isPlaying(p) && UHC.state.isInGame() && item != null && item.getType() == Material.REDSTONE && (e.getAction() == Action.RIGHT_CLICK_BLOCK
				|| e.getAction() == Action.RIGHT_CLICK_AIR)) {
			openRequesterInventory(p);
			p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.5F, 1.5F);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void invClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(UHC.state.isInGame() && e.getView().getTitle().startsWith(name)) {
			if(e.getClickedInventory() != null && e.getClickedInventory() == e.getView().getTopInventory()) {
				ItemStack item = e.getCurrentItem();
				if(item != null && item.getType() != Material.AIR) {
					request(p, item);
				}
			}
			e.setCancelled(true);
		}
	}

}
