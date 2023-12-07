package ru.greenbudgie.artifact;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.classes.ClassManager;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.mutator.MutatorManager;
import ru.greenbudgie.util.InventoryHelper;
import ru.greenbudgie.util.ItemUtils;
import ru.greenbudgie.util.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.bukkit.ChatColor.*;

public class ArtifactManager implements Listener {

	public static List<Artifact> artifacts = new ArrayList<>();
	public static ArtifactTimeLeap timeLeap = new ArtifactTimeLeap();
	public static ArtifactTeleport teleport = new ArtifactTeleport();
	public static ArtifactHealth health = new ArtifactHealth();
	public static ArtifactDrop drop = new ArtifactDrop();
	public static ArtifactRandomEffect randomEffect = new ArtifactRandomEffect();
	public static ArtifactMutator mutator = new ArtifactMutator();
	public static ArtifactDamage damage = new ArtifactDamage();
	public static ArtifactDisableMutator disableMutator = new ArtifactDisableMutator();
	public static ArtifactChaos chaos = new ArtifactChaos();
	public static ArtifactHunger hunger = new ArtifactHunger();
	public static ArtifactRequest request = new ArtifactRequest();

	private static final Map<Integer, Artifact> inventoryArtifacts = new HashMap<>();

	private static final String INVENTORY_NAME = Artifact.padSymbols(RED + "" + BOLD + "Силы артефактов");

	public static void init() {
		putArtifact(timeLeap, 11);
		putArtifact(drop, 12);
		putArtifact(request, 13);
		putArtifact(hunger, 14);
		putArtifact(mutator, 15);

		putArtifact(disableMutator, 20);
		putArtifact(randomEffect, 21);
		putArtifact(teleport, 22);
		putArtifact(health, 23);
		putArtifact(damage, 24);

		putArtifact(chaos, 40);
	}

	private static void putArtifact(Artifact artifact, int slot) {
		inventoryArtifacts.put(slot, artifact);
	}

	private static boolean isPreview(Player player) {
		return !PlayerManager.isPlaying(player) || !UHC.state.isGameActive();
	}

	public static void openArtifactInventory(Player player) {
		boolean preview = isPreview(player);
		Inventory inventory;
		if(preview) {
			inventory = Bukkit.createInventory(player, 54, INVENTORY_NAME);
		} else {
			int count = getArtifactCount(player);
			inventory = Bukkit.createInventory(player, 54, INVENTORY_NAME +
					DARK_GRAY + " (" +
					RED + BOLD + count +
					DARK_GRAY + ")");
		}
		ItemStack blackGlass = ItemUtils.builder(Material.BLACK_STAINED_GLASS_PANE).withName(" ").build();
		for(int slot = 0; slot < 54; slot++) {
			if(
				slot < 9 ||
				slot >= 45 ||
				slot % 9 == 0 ||
				slot % 9 == 8
			) {
				inventory.setItem(slot, blackGlass);
			}
		}
		for(int slot : inventoryArtifacts.keySet()) {
			Artifact artifact = inventoryArtifacts.get(slot);
			inventory.setItem(slot, preview ? artifact.getItem() : artifact.getItemFor(player));
		}
		player.openInventory(inventory);
	}

	public static int getArtifactCount(Player p) {
		return Stream.of(p.getInventory().getContents()).filter(CustomItems.darkArtifact::isEquals).mapToInt(ItemStack::getAmount).sum();
	}

	public static void removeArtifacts(Player p, int count) {
		Inventory inv = p.getInventory();
		List<ItemStack> artifacts = Stream.of(inv.getContents()).filter(CustomItems.darkArtifact::isEquals).collect(Collectors.toList());
		int c = 0;
		while(Stream.of(inv.getContents()).anyMatch(CustomItems.darkArtifact::isEquals)) {
			ItemStack s = InventoryHelper.getValidStackFromList(artifacts);
			s.setAmount(s.getAmount() - 1);
			c++;
			if(c >= count) break;
		}
	}

	@EventHandler
	public void click(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack item = e.getItem();
		if(PlayerManager.isPlaying(player) &&
				UHC.state.isGameActive() &&
				CustomItems.darkArtifact.isEquals(item) &&
				(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)) {
			openArtifactInventory(e.getPlayer());
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.5F, 1.5F);
			e.setCancelled(true);
		}
	}

	public static void resetPrices() {
		for(Artifact artifact : artifacts) {
			artifact.resetPrice();
		}
	}

	@EventHandler
	public void invClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (!event.getView().getTitle().startsWith(INVENTORY_NAME)) {
			return;
		}
		event.setCancelled(true);
		if (isPreview(player) || event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) {
			return;
		}
		ItemStack item = event.getCurrentItem();
		if (item == null) {
			return;
		}
		for(Artifact artifact : artifacts) {
			if (artifact.getType() != item.getType()) {
				continue;
			}
			if (!artifact.canUse(player)) {
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 0.5F);
				continue;
			}
			int priceBefore = artifact.getCurrentPrice();
			if(artifact.use(player)) {
				//Reopening other players' inventories to reset prices
				for(Player currentPlayer : PlayerManager.getAliveOnlinePlayers()) {
					if(currentPlayer != player) {
						InventoryView openInv = currentPlayer.getOpenInventory();
						if(openInv.getTitle().startsWith(INVENTORY_NAME)) {
							currentPlayer.closeInventory();
							openArtifactInventory(currentPlayer);
						}
					}
				}
				removeArtifacts(player, priceBefore);
			} else {
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 0.5F);
			}
			player.closeInventory();
			break;
		}
	}

	@EventHandler
	public void dropArtifact(EntityDeathEvent e) {
		LivingEntity entity = e.getEntity();
		if(entity.getKiller() != null) {
			int multiplier = MutatorManager.isActive(MutatorManager.doubleArtifacts) ? 2 : 1;
			if(entity instanceof Monster ||
					entity instanceof Slime ||
					entity instanceof Shulker ||
					entity instanceof Ghast ||
					entity instanceof Boss ||
					entity instanceof Hoglin) {
				if(entity instanceof Slime && !MathUtils.chance(20)) return;
				Player killer = entity.getKiller();
				if(ClassManager.DEMON.hasClass(killer) && entity.getWorld() == WorldManager.getGameMapNether()) return;
				if(ClassManager.NECROMANCER.hasClass(killer) && ClassManager.NECROMANCER.isFriendly(entity)) return;
				ItemStack item = CustomItems.darkArtifact.getItemStack();
				item.setAmount(multiplier);
				e.getDrops().add(item);
			}
		}
	}

}
