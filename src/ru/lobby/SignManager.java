package ru.lobby;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.ArenaManager;
import ru.UHC.GameState;
import ru.UHC.UHC;
import ru.UHC.WorldManager;
import ru.main.UHCPlugin;
import ru.mutator.MutatorManager;
import ru.pvparena.PvpArena;
import ru.util.InventoryHelper;
import ru.util.WorldHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignManager implements Listener {

	public static List<LobbySign> signs = new ArrayList<>();

	protected static void init() {
		ConfigurationSection signsSection = Lobby.getLobbyConfig().getConfigurationSection("signs");
		Map<String, Object> map = signsSection.getValues(false);
		for(String typeName : map.keySet()) {
			SignType type;
			try {
				type = SignType.valueOf(typeName);
			} catch(IllegalArgumentException e) {
				UHCPlugin.warning("There are no such sign type " + typeName);
				continue;
			}
			List<String> locations = signsSection.getStringList(typeName);
			for(String locationString : locations) {
				Location location = WorldHelper.translateToLocation(Lobby.getLobby(), locationString);
				if(location == null) {
					UHCPlugin.warning("Illegal sign location notation: " + locationString);
					continue;
				}
				if(!(location.getBlock().getState() instanceof Sign)) {
					UHCPlugin.warning("No sign at specified location: " + locationString + "(" + typeName + ")");
					continue;
				}
				signs.add(new LobbySign(location, type));
			}
		}
		updateSigns();
	}

	public static LobbySign getSignAt(Location l) {
		for(LobbySign sign : signs) {
			if(WorldHelper.compareLocations(l, sign.location())) return sign;
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
			switch(sign.type()) {
			case GAME_START:
				if(UHC.playing) {
					block.setLine(1, defCol + "Игра идет...");
				} else {
					block.setLine(1, (WorldManager.hasMap() ? ChatColor.DARK_GREEN : ChatColor.GRAY) + "Начать игру");
					if(!WorldManager.hasMap()) block.setLine(2, ChatColor.RED + "Мир не создан");
				}
				break;
			case MAP_GENERATE:
				if(WorldManager.hasMap()) {
					block.setLine(1, ChatColor.DARK_GREEN + "Мир создан");
					block.setLine(2, defCol + "<Пересоздать>");
				} else {
					block.setLine(1, ChatColor.DARK_RED + "Мир не создан");
					block.setLine(2, defCol + "<Сгенерировать>");
				}
				break;
			case GAME_MAP_SIZE:
				block.setLine(1, defCol + "Размер карты:");
				block.setLine(2, UHC.mapSize == 0 ?
						(ChatColor.DARK_GREEN + "Маленький") :
						(UHC.mapSize == 1 ?
								(ChatColor.DARK_AQUA + "Обычный") :
								(UHC.mapSize == 2 ? (ChatColor.DARK_RED + "Большой") : (ChatColor.LIGHT_PURPLE + "Фиксированный"))));
				if(UHC.mapSize != 3) {
					block.setLine(3, ChatColor.DARK_AQUA + String.valueOf(UHC.getMapSize()) + ChatColor.GOLD + " бл. на игрока");
				} else {
					block.setLine(3, ChatColor.DARK_AQUA + String.valueOf(UHC.getMapSize()) + ChatColor.GOLD + " бл.");
				}
				break;
			case GAME_DURATION:
				block.setLine(0, defCol + "Длит. игры:");
				block.setLine(1, (UHC.gameDuration == 0 ?
						(ChatColor.DARK_GREEN + "Короткая") :
						(UHC.gameDuration == 1 ?
								(ChatColor.DARK_AQUA + "Обычная") :
								(ChatColor.DARK_RED + "Долгая"))) + defCol + ", " + (UHC.getNoPVPDuration() + UHC.getGameDuration()) + "мин");
				block.setLine(2, ChatColor.DARK_AQUA + String.valueOf(UHC.getNoPVPDuration()) + defCol + " минут без пвп");
				block.setLine(3, ChatColor.DARK_AQUA + String.valueOf(UHC.getGameDuration()) + defCol + " минут до ДМ");
				break;
			case GAME_STATS:
				block.setLine(1, defCol + "Статистика:");
				block.setLine(2, UHC.stats ? (ChatColor.DARK_GREEN + "Включена") : (ChatColor.DARK_RED + "Отключена"));
				break;
			case GAME_DUO:
				block.setLine(1, defCol + "Режим:");
				if(!UHC.isDuo) {
					block.setLine(2, ChatColor.DARK_AQUA + "Соло");
				} else {
					block.setLine(2, ChatColor.DARK_PURPLE + "Дуо");
				}
				break;
			case RETURN_LOBBY:
				block.setLine(1, defCol + "Назад");
				break;
			case ARENA_TP:
				block.setLine(1, ChatColor.DARK_RED + "Арена");
				block.setLine(2, ChatColor.DARK_PURPLE + "<Телепорт>");
				break;
			case ARENA_NEXT_KIT:
				block.setLine(0, defCol + "Убийств до");
				block.setLine(1, defCol + "след. набора:");
				block.setLine(2, ChatColor.DARK_AQUA + "" + PvpArena.killsToNextKit);
				block.setLine(3, ChatColor.DARK_GREEN + "<Сменить>");
				break;
			case GAME_FAST_START:
				block.setLine(1, defCol + "Быстрый старт");
				block.setLine(2, UHC.fastStart > 0 ? (ChatColor.DARK_GREEN + "Включен") : (ChatColor.DARK_GRAY + "Отключен"));
				block.setLine(3, UHC.fastStart == 0 ? "" : (UHC.fastStart == 2 ? (ChatColor.DARK_AQUA + "С мутаторами") : (ChatColor.DARK_RED + "Без мутаторов")));
				break;
			case SPECTATE:

				if(!UHC.playing) {
					block.setLine(1, ChatColor.GRAY + "<Наблюдать>");
					block.setLine(2, ChatColor.DARK_RED + "Игра не идет");
				} else {
					block.setLine(1, ChatColor.GRAY + "<" + ChatColor.AQUA + "Наблюдать" + ChatColor.GRAY + ">");
				}
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
			if(sign != null && (!UHC.playing || sign.type() == SignType.GAME_START) && (p.isOp() || (UHC.playing && sign.type() == SignType.GAME_START) || sign.type()
					.canAnyoneUse())) {
				b.getWorld().playSound(b.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.05F, 1.5F);
				switch(sign.type()) {
				case GAME_START:
					if(!UHC.playing) {
						UHC.startGame();
					}
					break;
				case SPECTATE:
					if(UHC.playing) {
						UHC.spectators.add(p);
						UHC.resetPlayer(p);
						p.setGameMode(GameMode.SPECTATOR);
						p.teleport(UHC.state == GameState.DEATHMATCH ? ArenaManager.getCurrentArena().world().getSpawnLocation() : WorldManager.spawnLocation);
						UHC.refreshScoreboards();
						p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
						if(MutatorManager.isActive(MutatorManager.meetingPlace)) {
							MutatorManager.meetingPlace.bar.addPlayer(p);
						}
						for(Player pl : UHC.getInGamePlayers()) {
							pl.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.AQUA + " присоединился к наблюдателям");
						}
					}
					break;
				case MAP_GENERATE:
					if(!WorldManager.hasMap() || p.isSneaking()) {
						WorldManager.regenMap();
					}
					break;
				case GAME_MAP_SIZE:
					if(UHC.mapSize >= 3) UHC.mapSize = 0;
					else UHC.mapSize++;
					break;
				case GAME_DURATION:
					if(UHC.gameDuration >= 2) UHC.gameDuration = 0;
					else UHC.gameDuration++;
					break;
				case GAME_STATS:
					UHC.stats = !UHC.stats;
					break;
				case GAME_DUO:
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
				case ARENA_NEXT_KIT:
					PvpArena.killsToNextKit = 8;
					PvpArena.currentKit = PvpArena.getRandomKit();
					String text = ChatColor.GREEN + "Новый набор: " + ChatColor.LIGHT_PURPLE + PvpArena.currentKit.getName();
					InventoryHelper.sendActionBarMessage(p, text);
					for(Player player : PvpArena.onArena) {
						InventoryHelper.sendActionBarMessage(player, text);
					}
					break;
				case ARENA_TP:
					p.teleport(PvpArena.arenaSpawnLocation);
					break;
				case GAME_FAST_START:
					UHC.fastStart = UHC.fastStart == 2 ? 0 : UHC.fastStart + 1;
					break;
				}
				updateSigns();
			}
		}
	}

}
