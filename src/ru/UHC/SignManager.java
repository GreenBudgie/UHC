package ru.UHC;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.mutator.MutatorManager;
import ru.pvparena.PvpArena;
import ru.util.InventoryHelper;
import ru.util.WorldHelper;

import java.util.ArrayList;
import java.util.List;

public class SignManager implements Listener {

	public static List<LobbySign> signs = new ArrayList<>();

	public static void init() {
		signs.add(new LobbySign(-1, 5, -24, SignType.GAME_START));
		signs.add(new LobbySign(-2, 5, -24, SignType.DUO));
		signs.add(new LobbySign(0, 5, -24, SignType.STATS));
		signs.add(new LobbySign(-3, 5, -24, SignType.SIZE));
		signs.add(new LobbySign(1, 5, -24, SignType.DURATION));
		signs.add(new LobbySign(2, 5, -24, SignType.FAST_START));
		signs.add(new LobbySign(-1, 6, -24, SignType.REGEN));
		signs.add(new LobbySign(-25, 5, -1, SignType.TRIDENT_TP));
		signs.add(new LobbySign(-22, 5, -7, SignType.RETURN_LOBBY));
		signs.add(new LobbySign(-12, 5, -26, SignType.RETURN_LOBBY));
		signs.add(new LobbySign(-50, 8, -22, SignType.RETURN_LOBBY));
		signs.add(new LobbySign(-25, 5, 1, SignType.ARENA_TP));
		signs.add(new LobbySign(-48, 8, -22, SignType.NEXT_KIT));
		updateSigns();
	}

	public static LobbySign getSignAt(Location l) {
		for(LobbySign sign : signs) {
			if(WorldHelper.compareLocations(l, sign.getLocation())) return sign;
		}
		return null;
	}

	public static void clearSign(Sign sign) {
		for(int i = 0; i < 4; i++) {
			sign.setLine(i, "");
		}
	}

	public static void updateSigns() {
		for(LobbySign sign : signs) {
			Sign block = sign.getSign();
			clearSign(block);
			ChatColor defCol = ChatColor.DARK_BLUE;
			switch(sign.getType()) {
			case GAME_START:
				if(UHC.playing) {
					block.setLine(1, defCol + "���� ����...");
					block.setLine(2, ChatColor.DARK_AQUA + "<���������>");
				} else {
					block.setLine(1, (WorldManager.hasMap() ? ChatColor.DARK_GREEN : ChatColor.DARK_GRAY) + "������ ����");
				}
				break;
			case REGEN:
				if(WorldManager.hasMap()) {
					block.setLine(1, ChatColor.DARK_GREEN + "��� ������");
					block.setLine(2, defCol + "<�����������>");
				} else {
					block.setLine(1, ChatColor.DARK_RED + "��� �� ������");
					block.setLine(2, defCol + "<�������������>");
				}
				break;
			case SIZE:
				block.setLine(1, defCol + "������ �����:");
				block.setLine(2, UHC.mapSize == 0 ?
						(ChatColor.DARK_GREEN + "���������") :
						(UHC.mapSize == 1 ?
								(ChatColor.DARK_AQUA + "�������") :
								(UHC.mapSize == 2 ? (ChatColor.DARK_RED + "�������") : (ChatColor.LIGHT_PURPLE + "�������������"))));
				if(UHC.mapSize != 3) {
					block.setLine(3, ChatColor.DARK_AQUA + String.valueOf(UHC.getMapSize()) + ChatColor.GOLD + " ��. �� ������");
				} else {
					block.setLine(3, ChatColor.DARK_AQUA + String.valueOf(UHC.getMapSize()) + ChatColor.GOLD + " ��.");
				}
				break;
			case DURATION:
				block.setLine(0, defCol + "����. ����:");
				block.setLine(1, (UHC.gameDuration == 0 ?
						(ChatColor.DARK_GREEN + "��������") :
						(UHC.gameDuration == 1 ? (ChatColor.DARK_AQUA + "�������") : (ChatColor.DARK_RED + "������"))) + defCol + ", " + String
						.valueOf(UHC.getNoPVPDuration() + UHC.getGameDuration()) + "���");
				block.setLine(2, ChatColor.DARK_AQUA + String.valueOf(UHC.getNoPVPDuration()) + defCol + " ����� ��� ���");
				block.setLine(3, ChatColor.DARK_AQUA + String.valueOf(UHC.getGameDuration()) + defCol + " ����� �� ��");
				break;
			case STATS:
				block.setLine(1, defCol + "����������:");
				block.setLine(2, UHC.stats ? (ChatColor.DARK_GREEN + "��������") : (ChatColor.DARK_RED + "���������"));
				break;
			case DUO:
				block.setLine(1, defCol + "�����:");
				if(!UHC.isDuo) {
					block.setLine(2, ChatColor.DARK_AQUA + "����");
				} else {
					block.setLine(2, ChatColor.DARK_PURPLE + "���");
				}
				break;
			case RETURN_LOBBY:
				block.setLine(1, defCol + "�����");
				break;
			case TRIDENT_TP:
				block.setLine(1, defCol + "������");
				block.setLine(2, ChatColor.DARK_PURPLE + "<��������>");
				break;
			case ARENA_TP:
				block.setLine(1, ChatColor.DARK_RED + "�����");
				block.setLine(2, ChatColor.DARK_PURPLE + "<��������>");
				break;
			case NEXT_KIT:
				block.setLine(0, defCol + "������� ��");
				block.setLine(1, defCol + "����. ������:");
				block.setLine(2, ChatColor.DARK_AQUA + "" + PvpArena.killsToNextKit);
				block.setLine(3, ChatColor.DARK_GREEN + "<�������>");
				break;
			case FAST_START:
				block.setLine(1, defCol + "������� �����");
				block.setLine(2, UHC.fastStart > 0 ? (ChatColor.DARK_GREEN + "�������") : (ChatColor.DARK_GRAY + "��������"));
				block.setLine(3, UHC.fastStart == 0 ? "" : (UHC.fastStart == 2 ? (ChatColor.DARK_AQUA + "� ����������") : (ChatColor.DARK_RED + "��� ���������")));
				break;
			}
			block.update();
		}
	}

	@EventHandler
	public void signClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(!UHC.generating && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			LobbySign sign = getSignAt(b.getLocation());
			if(sign != null && (!UHC.playing || sign.getType() == SignType.GAME_START) && (p.isOp() || (UHC.playing && sign.getType() == SignType.GAME_START) || sign.getType()
					.canAnyoneUse())) {
				b.getWorld().playSound(b.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.05F, 1.5F);
				switch(sign.getType()) {
				case GAME_START:
					if(!UHC.playing) {
						UHC.startGame();
					} else {
						UHC.spectators.add(p);
						UHC.resetPlayer(p);
						p.setGameMode(GameMode.SPECTATOR);
						p.teleport(UHC.state == GameState.DEATHMATCH ? WorldManager.getArena().getSpawnLocation() : WorldManager.spawnLocation);
						UHC.refreshScoreboards();
						p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
						if(MutatorManager.isActive(MutatorManager.meetingPlace)) {
							MutatorManager.meetingPlace.bar.addPlayer(p);
						}
						for(Player pl : UHC.getInGamePlayers()) {
							pl.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.AQUA + " ������������� � ������������");
						}
					}
					break;
				case REGEN:
					if(!WorldManager.hasMap() || p.isSneaking()) {
						WorldManager.regenMap();
					}
					break;
				case SIZE:
					if(UHC.mapSize >= 3) UHC.mapSize = 0;
					else UHC.mapSize++;
					break;
				case DURATION:
					if(UHC.gameDuration >= 2) UHC.gameDuration = 0;
					else UHC.gameDuration++;
					break;
				case STATS:
					UHC.stats = !UHC.stats;
					break;
				case DUO:
					UHC.isDuo = !UHC.isDuo;
					List<Player> players = WorldManager.getLobby().getPlayers();
					players.forEach(Player::closeInventory);
					if(UHC.isDuo) {
						for(Player pl : players) {
							pl.getInventory().setItem(4, UHC.getTeammateChooseItem());
							UHC.updateLobbyScoreboard(pl);
						}
					} else {
						for(Player pl : players) {
							UHC.leaveTeam(pl);
							InventoryHelper.getItems(pl.getInventory(), Material.REDSTONE).forEach(item -> item.setAmount(0));
							InventoryHelper.getItems(pl.getInventory(), Material.PLAYER_HEAD).forEach(item -> item.setAmount(0));
							UHC.updateLobbyScoreboard(pl);
						}
					}
					break;
				case RETURN_LOBBY:
					InventoryHelper.removeItemsExcept(p.getInventory(), Material.REDSTONE, Material.PLAYER_HEAD);
					if(UHC.isDuo) {
						UHC.updateInventoryTeamItemFor(p);
					}
					p.teleport(WorldManager.getLobby().getSpawnLocation());
					break;
				case TRIDENT_TP:
					ItemStack trident = new ItemStack(Material.TRIDENT);
					trident.addEnchantment(Enchantment.RIPTIDE, 1);
					p.getInventory().addItem(trident);
					p.teleport(new Location(p.getWorld(), -25, 4, -9, 180, 0));
					break;
				case NEXT_KIT:
					PvpArena.killsToNextKit = 8;
					PvpArena.currentKit = PvpArena.getRandomKit();
					String text = ChatColor.GREEN + "����� �����: " + ChatColor.LIGHT_PURPLE + PvpArena.currentKit.getName();
					InventoryHelper.sendActionBarMessage(p, text);
					for(Player player : PvpArena.onArena) {
						InventoryHelper.sendActionBarMessage(player, text);
					}
					break;
				case ARENA_TP:
					p.teleport(PvpArena.arenaSpawnLocation);
					break;
				case FAST_START:
					UHC.fastStart = UHC.fastStart == 2 ? 0 : UHC.fastStart + 1;
					break;
				}
				updateSigns();
			}
		}
	}

}
