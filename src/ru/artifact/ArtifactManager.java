package ru.artifact;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.WorldManager;
import ru.classes.ClassManager;
import ru.items.CustomItems;
import ru.mutator.MutatorManager;
import ru.util.InventoryHelper;
import ru.util.ItemUtils;
import ru.util.MathUtils;
import ru.util.NumericalCases;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArtifactManager implements Listener {

	public static List<Artifact> artifacts = new ArrayList<>();
	public static ArtifactTimeLeap timeLeap = new ArtifactTimeLeap();
	public static ArtifactTeleport teleport = new ArtifactTeleport();
	public static ArtifactHealth health = new ArtifactHealth();
	public static ArtifactDrop drop = new ArtifactDrop();
	public static ArtifactTime time = new ArtifactTime();
	public static ArtifactMutator mutator = new ArtifactMutator();
	public static ArtifactDamage damage = new ArtifactDamage();
	public static ArtifactDisableMutator disableMutator = new ArtifactDisableMutator();
	public static ArtifactRandom random = new ArtifactRandom();
	public static ArtifactHunger hunger = new ArtifactHunger();
	public static ArtifactRequest request = new ArtifactRequest();

	private static final Map<Integer, Artifact> inventoryArtifacts = new HashMap<>();

	private static String inventoryName = ChatColor.DARK_RED + "Призвать";

	public static void init() {
		putArtifact(timeLeap, 11);
		putArtifact(drop, 12);
		putArtifact(request, 13);
		putArtifact(teleport, 14);
		putArtifact(mutator, 15);

		putArtifact(disableMutator, 20);
		putArtifact(time, 21);
		putArtifact(hunger, 22);
		putArtifact(health, 23);
		putArtifact(damage, 24);

		putArtifact(random, 40);
	}

	private static void putArtifact(Artifact artifact, int slot) {
		inventoryArtifacts.put(slot, artifact);
	}

	public static void openArtifactInventory(Player player) {
		NumericalCases cases = new NumericalCases("артефакт", "артефакта", "артефактов");
		int count = getArtifactCount(player);
		Inventory inventory = Bukkit.createInventory(player, 54, inventoryName +
						ChatColor.DARK_GRAY + " (" +
						ChatColor.DARK_AQUA + ChatColor.BOLD + count +
						ChatColor.RED + " " + cases.byNumber(count) +
						ChatColor.DARK_GRAY + ")");
		ItemStack blackGlass = ItemUtils.builder(Material.BLACK_STAINED_GLASS_PANE).withName("").build();
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
			inventory.setItem(slot, inventoryArtifacts.get(slot).getItemFor(player));
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
				UHC.state.isInGame() &&
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
	public void invClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(e.getView().getTitle().startsWith(inventoryName)) {
			if(e.getClickedInventory() != null && e.getClickedInventory() == e.getView().getTopInventory()) {
				ItemStack item = e.getCurrentItem();
				if(item != null) {
					for(Artifact artifact : artifacts) {
						if(artifact.getType() == item.getType()) {
							if(getArtifactCount(p) >= artifact.getCurrentPrice()) {
								int priceBefore = artifact.getCurrentPrice();
								if(artifact.use(p)) {
									//Reopening other players' inventories to reset prices
									for(Player player : PlayerManager.getAliveOnlinePlayers()) {
										if(player != p) {
											InventoryView openInv = player.getOpenInventory();
											if(openInv.getTitle().startsWith(inventoryName)) {
												player.closeInventory();
												openArtifactInventory(player);
											}
										}
									}
									removeArtifacts(p, priceBefore);
								} else {
									p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 0.5F);
								}
								p.closeInventory();
								break;
							} else {
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 0.5F);
							}
						}
					}
				}
			}
			e.setCancelled(true);
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
				if(ClassManager.getInGameClass(entity.getKiller()) == ClassManager.DEMON &&
						entity.getWorld() == WorldManager.getGameMapNether()) return;
				ItemStack item = CustomItems.darkArtifact.getItemStack();
				item.setAmount(multiplier);
				e.getDrops().add(item);
			}
		}
	}

}
