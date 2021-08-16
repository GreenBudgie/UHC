package ru.UHC;

import com.google.common.collect.Lists;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Lectern;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import ru.artifact.ArtifactManager;
import ru.drop.Drop;
import ru.items.CustomItems;
import ru.main.UHCPlugin;
import ru.mutator.ItemBasedMutator;
import ru.mutator.Mutator;
import ru.mutator.MutatorManager;
import ru.pvparena.PvpArena;
import ru.requester.ItemRequester;
import ru.requester.RequestedItem;
import ru.drop.Drops;
import ru.util.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UHC implements Listener {

	public static boolean playing = false;
	public static List<Player> players = new ArrayList<>();
	public static GameState state = GameState.STOPPED;
	public static Map<Player, Boolean> voteResults = new HashMap<>();
	public static int voteTimer = 0;
	public static int preparingTimer = 0;
	public static int outbreakTimer = 0;
	public static int deathmatchTimer = 0;
	public static int arenaTimer = 0;
	public static boolean skip = false; //True skips the current GameState
	public static int endTimer = 0;
	public static int arenaPvpTimer = 0;
	//V 2.0
	public static boolean isDuo = false;
	public static Map<Player, Player> teammateChoices = new HashMap<>();
	public static List<PlayerTeam> teams = new ArrayList<>();
	public static List<Player> spectators = new ArrayList<>();
	public static int mapSize = 1;
	public static int gameDuration = 1;
	public static boolean generating = false;
	public static boolean stats = true;
	public static Location parkourStart;
	public static String timerInfo = "";
	public static List<Landmine> landmines = new ArrayList<>();
	public static Set<Player> processedPlayers = new HashSet<>();
	public static int fastStart = 0; //0 - disabled, 1 - without mutators, 2 - with mutators
	public static List<Location> glassPlates = new ArrayList<>();
	public static List<TerraTracer> tracers = new ArrayList<>();
	private static int mutatorCount = 0;
	private static Mutator prevMutator = null;
	private static Player lastLeft = null;
	private static BossBar voteBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);

	public static void init() {
		WorldManager.init();
		RecipeHandler.init();
		SignManager.init();
		PvpArena.init();
		for(Player p : Bukkit.getOnlinePlayers()) {
			resetPlayer(p);
			createLobbyScoreboard(p);
		}
		parkourStart = new Location(WorldManager.getLobby(), -1, 4, 21);
	}

	public static String getColoredUHC() {
		return ChatColor.RED + "" + ChatColor.BOLD + "U" + ChatColor.GOLD + ChatColor.BOLD + "H" + ChatColor.YELLOW + ChatColor.BOLD + "C";
	}

	public static void createGameScoreboard(Player p) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

		Team spectatorTeam = board.registerNewTeam("SpectatorTeam");
		spectatorTeam.setColor(ChatColor.WHITE);
		spectators.forEach(pl -> spectatorTeam.addEntry(pl.getName()));

		Team lobbyTeam = board.registerNewTeam("LobbyTeam");
		lobbyTeam.setColor(ChatColor.GOLD);
		WorldManager.getLobby().getPlayers().forEach(pl -> lobbyTeam.addEntry(pl.getName()));

		Team playerTeam = board.registerNewTeam("PlayerTeam");
		playerTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		playerTeam.setCanSeeFriendlyInvisibles(false);
		playerTeam.setColor(ChatColor.AQUA);
		players.forEach(pl -> playerTeam.addEntry(pl.getName()));

		p.setScoreboard(board);
		updateGameScoreboard(p);
	}

	public static void updateGameScoreboard(Player p) {
		Scoreboard board = p.getScoreboard();
		Objective gameInfo = board.getObjective("gameInfo");
		if(gameInfo != null) gameInfo.unregister();
		gameInfo = board.registerNewObjective("gameInfo", "dummy", getColoredUHC());
		gameInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
		int c = 0;
		if(isDuo && PlayerOptions.SHOW_TEAMS.isActive(p)) {
			for(PlayerTeam playerTeam : teams) {
				Player pl = playerTeam.getPlayer1();
				Player teammate = getTeammate(pl.getPlayer());
				String s;
				String me = (pl == p ? ChatColor.GREEN : ChatColor.GOLD) + (isPlaying(pl) ? "" : ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH) + pl.getName();
				if(teammate != null) {
					String mate =
							((teammate == p) ? ChatColor.GREEN : ChatColor.GOLD) + (isPlaying(teammate) ? "" : ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH)
									+ teammate.getName();
					String finalString = ChatColor.DARK_GRAY + "- " + me + ChatColor.WHITE + " / " + mate;
					String[] trimmed = trimTeam(me, mate, finalString.length());
					me = trimmed[0];
					mate = trimmed[1];
					s = ChatColor.DARK_GRAY + "- " + me + ChatColor.WHITE + " / " + mate;
				} else {
					s = ChatColor.DARK_GRAY + "- " + me;
				}
				Score team = gameInfo.getScore(s);
				team.setScore(c++);
			}
			Score info = gameInfo.getScore(ChatColor.YELLOW + "Тимы:");
			info.setScore(c++);
		}
		if(state.isInGame()) {
			final boolean show = state == GameState.PREPARING || state == GameState.VOTE || state == GameState.OUTBREAK;
			for(Drop drop : new Drop[] {Drops.CAVEDROP, Drops.AIRDROP}) {
				if(drop.getTimer() <= deathmatchTimer || show) {
					Score locationScore = gameInfo.getScore(ChatColor.DARK_GRAY + "- " + drop.getCoordinatesInfo());
					locationScore.setScore(c++);
					Score textScore = gameInfo.getScore(
								drop.getName() +
									ChatColor.DARK_GRAY + " (" +
									ChatColor.AQUA + MathUtils.formatTime(drop.getTimer()) +
									ChatColor.DARK_GRAY + "):");
					textScore.setScore(c++);
				}
			}
		}
		if(!timerInfo.isEmpty()) {
			Score timer = gameInfo.getScore(timerInfo);
			timer.setScore(c);
		}

		registerHpInfo(p, "hpInfoList", board, DisplaySlot.PLAYER_LIST);
		registerHpInfo(p, "hpInfoName", board, DisplaySlot.BELOW_NAME);
	}

	private static void registerHpInfo(Player p, String name, Scoreboard board, DisplaySlot slot) {
		Objective hpInfo = board.getObjective(name);
		if(hpInfo != null) hpInfo.unregister();
		hpInfo = board.registerNewObjective(name, "health", ChatColor.RED + "\u2764");
		for(Player player : players) {
			Score hp = hpInfo.getScore(player.getName());
			hp.setScore((int) player.getHealth());
		}
		if(isSpectator(p) || MutatorManager.isActive(MutatorManager.healthDisplay)) {
			hpInfo.setDisplaySlot(slot);
		} else {
			hpInfo.setDisplaySlot(null);
		}
	}

	private static String[] trimTeam(String playerName1, String playerName2, int ln) {
		boolean change1 = false, change2 = false;
		while(ln >= 40 - (change1 ? 2 : 0) - (change2 ? 2 : 0)) {
			if(playerName1.length() > playerName2.length()) {
				playerName1 = playerName1.substring(0, playerName1.length() - 1);
				change1 = true;
			} else {
				playerName2 = playerName2.substring(0, playerName2.length() - 1);
				change2 = true;
			}
			ln--;
		}
		if(change1) playerName1 += "..";
		if(change2) playerName2 += "..";
		return new String[] {playerName1, playerName2};
	}

	public static void refreshScoreboardsLater() {
		refreshGameScoreboardLater();
		refreshLobbyScoreboardLater();
	}

	public static void refreshScoreboards() {
		refreshGameScoreboard();
		refreshLobbyScoreboard();
	}

	public static void refreshLobbyScoreboardLater() {
		TaskManager.invokeLater(UHC::refreshLobbyScoreboard);
	}

	public static void refreshGameScoreboardLater() {
		TaskManager.invokeLater(UHC::refreshGameScoreboard);
	}

	public static void refreshLobbyScoreboard() {
		WorldManager.getLobby().getPlayers().forEach(UHC::createLobbyScoreboard);
	}

	public static void refreshGameScoreboard() {
		getInGamePlayers().forEach(UHC::createGameScoreboard);
	}

	public static void createLobbyScoreboard(Player p) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

		Team spectatorTeam = board.registerNewTeam("SpectatorTeam");
		spectatorTeam.setColor(ChatColor.WHITE);
		spectators.forEach(pl -> spectatorTeam.addEntry(pl.getName()));

		Team lobbyTeam = board.registerNewTeam("LobbyTeam");
		lobbyTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		lobbyTeam.setCanSeeFriendlyInvisibles(false);
		lobbyTeam.setColor(ChatColor.GOLD);
		WorldManager.getLobby().getPlayers().forEach(pl -> lobbyTeam.addEntry(pl.getName()));

		Team playerTeam = board.registerNewTeam("PlayerTeam");
		playerTeam.setColor(ChatColor.AQUA);
		players.forEach(pl -> playerTeam.addEntry(pl.getName()));

		WorldManager.getLobby().getPlayers().forEach(pl -> lobbyTeam.addEntry(pl.getName()));
		p.setScoreboard(board);
		updateLobbyScoreboard(p);
	}

	public static void updateLobbyScoreboard(Player p) {
		Scoreboard board = p.getScoreboard();
		Objective teamInfo = board.getObjective("teamInfo");
		if(teamInfo != null) teamInfo.unregister();
		teamInfo = board.registerNewObjective("teamInfo", "dummy", getColoredUHC());
		if(!isDuo) {
			teamInfo.setDisplaySlot(null);
			return;
		} else {
			teamInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		List<Player> registered = new ArrayList<>();
		int c = 0;
		for(Player pl : WorldManager.getLobby().getPlayers()) {
			Player teammate = getTeammate(pl);
			String s;
			boolean request = getTeammate(pl) != null && getTeammate(pl) == p && !isTeammates(pl, p);
			String me = (pl == p ? ChatColor.GREEN : (request ? ChatColor.GOLD + "" + ChatColor.BOLD : ChatColor.GOLD)) + pl.getName();
			if(teammate != null && isTeammates(pl, teammate)) {
				if(registered.contains(teammate)) continue;
				String mate = ((teammate == p) ? ChatColor.GREEN : ChatColor.GOLD) + teammate.getName();
				registered.add(teammate);
				String finalString = ChatColor.DARK_GRAY + "- " + me + ChatColor.WHITE + " / " + mate;
				String[] trimmed = trimTeam(me, mate, finalString.length());
				me = trimmed[0];
				mate = trimmed[1];
				s = ChatColor.DARK_GRAY + "- " + me + ChatColor.WHITE + " / " + mate;
			} else {
				s = ChatColor.DARK_GRAY + "- " + me;
			}
			Score team = teamInfo.getScore(s);
			team.setScore(c);
			c++;
			registered.add(pl);
		}
		Score info = teamInfo.getScore(ChatColor.YELLOW + "Тимы:");
		info.setScore(c);
	}

	public static void endGame() {
		if(playing) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				resetPlayer(p);
				p.setGameMode(GameMode.ADVENTURE);
				p.teleport(WorldManager.getLobby().getSpawnLocation());
				if(isDuo) p.getInventory().setItem(4, getTeammateChooseItem());
				showPlayer(p);
			}
			voteBar.removeAll();
			voteBar.setVisible(false);
			players.clear();
			teams.clear();
			spectators.clear();
			landmines.clear();
			tracers.clear();
			ArtifactManager.resetPrices();
			ItemRequester.requestedItems.forEach(RequestedItem::deleteStands);
			ItemRequester.requestedItems.clear();
			MutatorManager.deactivateMutators();
			mapSize = 1;
			gameDuration = 1;
			stats = true;
			glassPlates.forEach(location -> location.getBlock().setType(Material.AIR));
			if(!WorldManager.keepMap && !state.isPreGame()) WorldManager.removeMap();
			state = GameState.STOPPED;
			playing = false;
			SignManager.updateSigns();
			refreshLobbyScoreboard();
		} else {
			Bukkit.broadcastMessage(ChatColor.RED + "Игра не идет");
		}
	}

	public static void startGame() {
		if(!playing) {
			if(!WorldManager.hasMap()) {
				Bukkit.broadcastMessage(ChatColor.RED + "Карта не сгенерирована!");
				return;
			}
			resetAllAdvancements();
			mutatorCount = MathUtils.chance(30) ? 4 : (MathUtils.chance(65) ? 3 : 2);
			voteResults.clear();
			for(Player player : Bukkit.getOnlinePlayers()) {
				InventoryHelper.sendActionBarMessage(player,
						ChatColor.GOLD + "" + ChatColor.BOLD + "Игра" +
							ChatColor.RED + ChatColor.BOLD + " начинается" +
							ChatColor.GRAY + "!");
			}
			WorldManager.updateBorder();
			World map = WorldManager.getGameMap();
			map.setPVP(false);
			map.setTime(0);
			map.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
			map.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
			Drops.firstSetup();
			for(Player p : Bukkit.getOnlinePlayers()) {
				PvpArena.onArenaLeave(p);
				if(!isDuo) {
					teams.add(new PlayerTeam(p));
				} else {
					Player teammate = getTeammate(p);
					if(teammate != null && isTeammates(p, teammate)) {
						boolean flag = true;
						for(PlayerTeam team : teams) {
							if(team.contains(p) || team.contains(teammate)) {
								flag = false;
								break;
							}
						}
						if(flag) teams.add(new PlayerTeam(p, teammate));
					} else {
						teams.add(new PlayerTeam(p));
						teammateChoices.remove(p);
					}
				}
				players.add(p);
				resetPlayer(p);
				p.setNoDamageTicks(600);
				p.setGameMode(GameMode.SURVIVAL);
				p.getInventory().setItem(3, InventoryHelper.generateItemWithName(Material.LIME_DYE,
						ChatColor.GREEN + "Карта " + ChatColor.DARK_GREEN + ChatColor.BOLD + "Норм"));
				p.getInventory().setItem(5, InventoryHelper.generateItemWithName(Material.RED_DYE,
						ChatColor.RED + "Карта " + ChatColor.DARK_RED + ChatColor.BOLD + "Говно"));
			}
			if(isDuo) {
				List<Player> newPlayers = new ArrayList<>();
				for(Player p : players) {
					if(newPlayers.contains(p)) continue;
					newPlayers.add(p);
					Player teammate = getTeammate(p);
					if(teammate != null) {
						newPlayers.add(teammate);
					}
				}
				players = newPlayers;
			}
			double radius = players.size() / 1.5;
			double radsPerPlayer = 2 * Math.PI / players.size();
			int spawnHeight = WorldManager.spawnLocation.getWorld().getHighestBlockYAt(WorldManager.spawnLocation) + 16;
			List<Location> spawnLocations = new ArrayList<>();
			for(int i = 0; i < players.size(); i++) {
				int x = (int) Math.round(WorldManager.spawnLocation.getX() + Math.cos(radsPerPlayer * i) * radius);
				int z = (int) Math.round(WorldManager.spawnLocation.getZ() + Math.sin(radsPerPlayer * i) * radius);
				Location l = new Location(WorldManager.spawnLocation.getWorld(), x, 1, z);
				spawnLocations.add(l);
				int topY = WorldManager.spawnLocation.getWorld().getHighestBlockYAt(l) + 4;
				if(topY >= spawnHeight) spawnHeight = topY;
			}
			for(Location l : spawnLocations) {
				l.setY(spawnHeight);
			}
			glassPlates.clear();
			for(int i = 0; i < players.size(); i++) {
				Player p = players.get(i);
				Location l = spawnLocations.get(i);
				Location l2 = WorldManager.spawnLocation.clone();
				double xLength = l2.getX() - l.getX();
				double zLength = l2.getZ() - l.getZ();
				double yaw = -Math.atan2(xLength, zLength);
				Location tpLoc = l.clone().add(0.5, 1, 0.5);
				tpLoc.setYaw((float) Math.toDegrees(yaw));
				p.teleport(tpLoc);
				glassPlates.add(l);
				l.getBlock().setType(Material.GLASS);
			}
			refreshScoreboards();
			if(UHC.fastStart == 0) {
				for(Player player : players) {
					voteBar.addPlayer(player);
				}
				voteBar.setVisible(true);
				updateVoteBar();
				state = GameState.VOTE;
				voteTimer = 20;
			} else {
				state = GameState.PREPARING;
				preparingTimer = 4;
			}
			playing = true;
			processedPlayers.clear();
			WorldManager.getArena().setPVP(false);
			SignManager.updateSigns();
		} else {
			Bukkit.broadcastMessage(ChatColor.RED + "Игра уже идет");
		}
	}

	public static void addPoints(String playerName, int points) {
		increaseStat(playerName, PlayerStat.POINTS, points);
	}

	public static void addPoints(Player p, int points) {
		increaseStat(p.getName(), PlayerStat.POINTS, points);
		if(p.isOnline() && stats) {
			String pointStr = new NumericalCases("очко)", "очка", "очков").byNumber(points);
			p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "| " + ChatColor.RESET + ChatColor.DARK_GREEN + "+" + ChatColor.GREEN + ChatColor.BOLD + points +
					ChatColor.RESET + " " + ChatColor.LIGHT_PURPLE + pointStr + ChatColor.DARK_GRAY + " (" + ChatColor.GRAY + "всего " + ChatColor.DARK_AQUA + ChatColor.BOLD +
					PlayerStat.POINTS.getValue(p) + ChatColor.RESET + ChatColor.DARK_GRAY + ")" + ChatColor.BOLD + " |");
		}
	}

	public static void increaseStat(Player p, PlayerStat stat, int value) {
		increaseStat(p.getName(), stat, value);
	}

	public static void increaseStat(String playerName, PlayerStat stat, int value) {
		if(stats) {
			stat.setValue(playerName, stat.getValue(playerName) + value);
		}
	}

	public static void hidePlayer(Player p) {
		Bukkit.getOnlinePlayers().forEach(pl -> pl.hidePlayer(UHCPlugin.instance, p));
	}

	public static void showPlayer(Player p) {
		Bukkit.getOnlinePlayers().forEach(pl -> pl.showPlayer(UHCPlugin.instance, p));
	}

	public static int getNoPVPDuration() {
		return gameDuration == 0 ? 10 : (gameDuration == 1 ? 15 : 20);
	}

	public static int getGameDuration() {
		return gameDuration == 0 ? 35 : (gameDuration == 1 ? 55 : 70);
	}

	public static void endVote() {
		voteBar.removeAll();
		voteBar.setVisible(false);
		int votesFor = 0;
		for(Player pl : voteResults.keySet()) {
			if(voteResults.get(pl)) votesFor++;
		}
		double progress;
		if(!voteResults.isEmpty()) {
			progress = votesFor / (double) voteResults.size();
		} else {
			progress = 0.5;
		}
		if(progress >= 0.6) {
			for(Player p : players) {
				p.sendTitle(ChatColor.GREEN + "Карта " + ChatColor.DARK_GREEN + ChatColor.BOLD + "Норм",
						ChatColor.GOLD + "35 секунд до начала", 10, 60, 30);
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
				p.getInventory().clear();
			}
			state = GameState.PREPARING;
			preparingTimer = 35;
		} else {
			for(Player p : players) {
				p.sendTitle(ChatColor.RED + "Карта " + ChatColor.DARK_RED + ChatColor.BOLD + "Говно" + ChatColor.GRAY + "!",
						ChatColor.GOLD + "Большинство проголосовало против", 10, 60, 30);
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 0.5F);
				p.getInventory().clear();
				p.setGameMode(GameMode.SPECTATOR);
				p.getLocation().clone().add(0, -1, 0).getBlock().setType(Material.AIR);
			}
			state = GameState.ENDING;
			endTimer = 8;
		}
	}

	private static void mutatorInvPreEffect(int n, boolean active) {
		int slot = 1 + (n * 2);
		if(active) {
			for(Player p : getInGamePlayers()) {
				if(n < mutatorCount) {
					ChatColor color = ChatColor.DARK_AQUA;
					if(n + 1 == 3)color = ChatColor.RED;
					if(n + 1 == 4) color = ChatColor.DARK_PURPLE;
					p.sendTitle(ChatColor.GOLD + "Выбираем мутаторы", ChatColor.AQUA + "Количество" +
							ChatColor.GRAY + ": " + color + ChatColor.BOLD + (n + 1), 0, 200, 0);
					p.getInventory().setItem(slot, ItemUtils.builder(Material.REDSTONE).withName(" ").build());
					p.playSound(p.getLocation(), Sound.ITEM_FIRECHARGE_USE, 0.3F, 1 + 0.1F * n);
				} else {
					p.getInventory().setItem(slot, ItemUtils.builder(Material.GRAY_DYE).withName(" ").build());
					p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.3F, 1.2F);
				}
			}
		} else {
			for(Player p : getInGamePlayers()) {
				p.getInventory().setItem(slot, ItemUtils.builder(Material.GUNPOWDER).withName(" ").build());
				p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5F, 1 + 0.1F * n);
			}
		}
	}

	private static String getOrderByN(int n) {
		return switch (n) {
			case 1 -> "Первый";
			case 2 -> "Второй";
			case 3 -> "Третий";
			case 4 -> "Четвертый";
			default -> "";
		};
	}

	private static void mutatorInvSwitchEffect(int n, int startTime) {
		float pitch = (float) (((startTime - preparingTimer) + (TaskManager.tick / 20.0)) / 2.0);
		String order = getOrderByN(n + 1);
		int slot = 1 + (n * 2);
		for(Player p : getInGamePlayers()) {
			Mutator mutator = MutatorManager.getRandomMutatorExcept(prevMutator != null ? Lists.newArrayList(prevMutator) : Lists.newArrayList());
			prevMutator = mutator;
			p.getInventory()
					.setItem(slot, ItemUtils.builder(mutator.getItemToShow()).withName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + mutator.getName()).build());
			p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 0.3F, 1 + pitch);
			p.sendTitle(ChatColor.GOLD + "Выбираем мутаторы", ChatColor.AQUA + order + " мутатор" + ChatColor.GRAY + ": " +
					ChatColor.DARK_GRAY + mutator.getName(), 0, 200, 0);
		}
	}

	private static void mutatorInvSelect(int n) {
		String order = getOrderByN(n + 1);
		int slot = 1 + (n * 2);
		Mutator mutator = MutatorManager.activateRandomMutator(true, true);
		for(Player p : getInGamePlayers()) {
			if(mutator.isHidden()) {
				p.getInventory().setItem(slot, ItemUtils.builder(Material.NAME_TAG).withName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Скрытый Мутатор").build());
				p.sendTitle(ChatColor.GOLD + "Выбираем мутаторы",
						ChatColor.AQUA + order + " мутатор" + ChatColor.GRAY + ": " + ChatColor.DARK_RED + ChatColor.BOLD + "Скрытый Мутатор", 0, 200, 0);
				p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.3F, 1F);
			} else {
				p.getInventory()
						.setItem(slot, ItemUtils.builder(mutator.getItemToShow()).withName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + mutator.getName()).build());
				p.sendTitle(ChatColor.GOLD + "Выбираем мутаторы",
						ChatColor.AQUA + order + " мутатор" + ChatColor.GRAY + ": " +
								ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + mutator.getName(), 0, 200, 0);
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3F, 1F + 0.2F * n);
			}
		}
	}

	private static void mutatorInvEnd() {
		for(Player p : getInGamePlayers()) {
			p.sendMessage(	ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "< " + ChatColor.RESET + MutatorManager.getMessageFromCurrentMutators() +
							ChatColor.RESET + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " >");
			p.sendTitle(ChatColor.GREEN + "Выбор закончен!", "", 0, 60, 10);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
		}
	}

	public static void tickGame() {
		if(state == GameState.VOTE) {
			if(TaskManager.isSecUpdated()) {
				voteTimer--;
				if(voteTimer <= 0 || skip) {
					skip = false;
					endVote();
				}
				timerInfo = ChatColor.GOLD + "Голосование" + ChatColor.GRAY + ": " + ChatColor.AQUA + ChatColor.BOLD + voteTimer;
			}
		}
		if(state == GameState.PREPARING) {
			if(TaskManager.isSecUpdated()) {
				preparingTimer--;
				if(preparingTimer == 30) {
					for(Player p : getInGamePlayers()) {
						p.sendTitle(getColoredUHC() + ChatColor.DARK_GRAY + " aka " + ChatColor.GOLD + "Битва Инвалидов", "", 5, 100, 0);
						p.sendMessage(
								ChatColor.GRAY + "-------- " + getColoredUHC() + ChatColor.DARK_GRAY + " aka " + ChatColor.GOLD + "Битва Инвалидов" + ChatColor.GRAY
										+ " --------");
						p.sendMessage("");
						p.sendMessage(ChatColor.YELLOW + "Главное - остаться в живых. " + ChatColor.BOLD + "Хп не регенится самостоятельно! " + ChatColor.RESET
								+ ChatColor.YELLOW + "Собирай золотые яблоки, делай запросы, ищи аирдроп - все это поможет выживанию.");
						p.sendMessage("");
						p.sendMessage(ChatColor.GRAY + "----------------------------------");
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.7F);
					}
				}
				if(preparingTimer <= 3 && preparingTimer > 0) {
					ChatColor c = switch (preparingTimer) {
						case 3 -> ChatColor.RED;
						case 2 -> ChatColor.GOLD;
						case 1 -> ChatColor.YELLOW;
						default -> ChatColor.DARK_AQUA;
					};
					for(Player p : getInGamePlayers()) {
						p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 0.5F, 0.9F + (float) (preparingTimer * 0.1));
						p.sendTitle(c + "" + ChatColor.BOLD + preparingTimer, "", 0, 100, 0);
					}
				}
				if(preparingTimer <= 0 || skip) {
					skip = false;
					endPreparing();
				}
				timerInfo = ChatColor.GOLD + "Подготовка" + ChatColor.GRAY + ": " + ChatColor.AQUA + ChatColor.BOLD + preparingTimer;
			}
			if(preparingTimer == 26) {
				if(TaskManager.tick == 0) {
					for(Player p : getInGamePlayers()) {
						p.sendTitle(ChatColor.GOLD + "Выбираем мутаторы", ChatColor.AQUA + "Количество" + ChatColor.GRAY + ": " +
										ChatColor.DARK_AQUA + ChatColor.BOLD + 0,
								10, 200, 0);
					}
				}
				if(TaskManager.tick == 10) {
					mutatorInvPreEffect(0, false);
				}
			}
			if(preparingTimer == 25) {
				if(TaskManager.tick == 0) {
					mutatorInvPreEffect(1, false);
				}
				if(TaskManager.tick == 10) {
					mutatorInvPreEffect(2, false);
				}
			}
			if(preparingTimer == 24) {
				if(TaskManager.tick == 0) {
					mutatorInvPreEffect(3, false);
				}
			}
			if(preparingTimer == 23) {
				if(TaskManager.tick == 0) {
					mutatorInvPreEffect(0, true);
				}
				if(TaskManager.tick == 10) {
					mutatorInvPreEffect(1, true);
				}
			}
			if(preparingTimer == 22) {
				if(TaskManager.tick == 0) {
					mutatorInvPreEffect(2, true);
				}
				if(TaskManager.tick == 10) {
					mutatorInvPreEffect(3, true);
				}
			}
			//Choosing mutator PHASE 1
			if(preparingTimer == 20 || preparingTimer == 19) {
				if(TaskManager.tick % 4 == 0) {
					mutatorInvSwitchEffect(0, 20);
				}
			}
			if(preparingTimer == 18 && TaskManager.tick == 0) {
				mutatorInvSelect(0);
			}
			if(preparingTimer <= 17) {
				if(mutatorCount >= 2) {
					//Choosing mutator PHASE 2
					if(preparingTimer == 17 || preparingTimer == 16) {
						if(TaskManager.tick % 4 == 0) {
							mutatorInvSwitchEffect(1, 17);
						}
					}
					if(preparingTimer == 15 && TaskManager.tick == 0) {
						mutatorInvSelect(1);
					}
					if(preparingTimer <= 14) {
						if(mutatorCount >= 3) {
							//Choosing mutator PHASE 3
							if(preparingTimer == 14 || preparingTimer == 13) {
								if(TaskManager.tick % 4 == 0) {
									mutatorInvSwitchEffect(2, 14);
								}
							}
							if(preparingTimer == 12 && TaskManager.tick == 0) {
								mutatorInvSelect(2);
							}
							if(preparingTimer <= 11) {
								if(mutatorCount == 4) {
									//Choosing mutator PHASE 4
									if(preparingTimer == 11 || preparingTimer == 10) {
										if(TaskManager.tick % 4 == 0) {
											mutatorInvSwitchEffect(3, 11);
										}
									}
									if(preparingTimer == 9 && TaskManager.tick == 0) {
										mutatorInvSelect(3);
									}
									//End of PHASE 4
									if(preparingTimer == 8 && TaskManager.tick == 0) {
										mutatorInvEnd();
									}
								} else {
									//End of PHASE 3
									if(preparingTimer == 11 && TaskManager.tick == 0) {
										mutatorInvEnd();
									}
								}
							}
						} else {
							//End of PHASE 2
							if(preparingTimer == 14 && TaskManager.tick == 0) {
								mutatorInvEnd();
							}
						}
					}
				} else {
					//End of PHASE 1
					if(preparingTimer == 17 && TaskManager.tick == 0) {
						mutatorInvEnd();
					}
				}
			}
		}
		if(state.isInGame() || state == GameState.DEATHMATCH) {
			MutatorManager.updateMutators();
		}
		if(state == GameState.OUTBREAK) {
			if(TaskManager.isSecUpdated()) {
				outbreakTimer--;
				timerInfo = ChatColor.GOLD + "До ПВП" + ChatColor.GRAY + ": " + ChatColor.AQUA + ChatColor.BOLD + MathUtils.formatTime(outbreakTimer);
				if(outbreakTimer == 0 || skip) {
					skip = false;
					state = GameState.GAME;
					deathmatchTimer = 60 * getGameDuration();
					WorldManager.getGameMap().setPVP(true);
					for(Player p : getInGamePlayers()) {
						p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 0.5F);
						p.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "ПВП " + ChatColor.GOLD + ChatColor.BOLD + "Включено" + ChatColor.GRAY + "!",
								ChatColor.YELLOW + "До дезматча " + ChatColor.AQUA + ChatColor.BOLD + getGameDuration() +
										ChatColor.RESET + ChatColor.YELLOW + " минут", 10, 60, 30);
					}
				}
			}
		}
		if(state == GameState.GAME) {
			if(TaskManager.isSecUpdated()) {
				deathmatchTimer--;
				timerInfo = ChatColor.GOLD + "До дезматча" + ChatColor.GRAY + ": " + ChatColor.AQUA + ChatColor.BOLD + MathUtils.formatTime(deathmatchTimer);
				if(deathmatchTimer == 0 || skip) {
					skip = false;
					state = GameState.DEATHMATCH;
					arenaTimer = 60 * 10;
					arenaPvpTimer = 15;
					for(Player p : getInGamePlayers()) {
						p.teleport(WorldManager.getArena().getSpawnLocation());
						if(isPlaying(p)) addPoints(p, 5);
						p.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Дезматч" + ChatColor.GRAY + "!",
								ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "15" + ChatColor.RESET + ChatColor.GOLD + " секунд до " +
										ChatColor.DARK_RED + ChatColor.BOLD + "ПВП" + ChatColor.GRAY + "!",
								10, 60, 30);
					}
				}
			}
		}
		if(state == GameState.DEATHMATCH) {
			if(TaskManager.isSecUpdated()) {
				arenaTimer--;
				arenaPvpTimer--;
				if(arenaTimer == 0 || skip) {
					skip = false;
					draw();
				}
				if(arenaPvpTimer == 0) {
					WorldManager.getArena().setPVP(true);
					for(Player p : getInGamePlayers()) {
						p.sendTitle("", ChatColor.DARK_RED + "" + ChatColor.BOLD + ">>> " + ChatColor.GOLD + ChatColor.BOLD + "ПВП" + ChatColor.RED +
								ChatColor.BOLD + " Включено!" + ChatColor.DARK_RED + ChatColor.BOLD + " <<<", 0, 35, 15);
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
					}
				}
				if(arenaPvpTimer <= 3 && arenaPvpTimer > 0) {
					for(Player p : getInGamePlayers()) {
						p.sendTitle("", ChatColor.DARK_GRAY + "" + ChatColor.BOLD + arenaPvpTimer, 0, 40, 0);
						p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 0.8F, 0.5F + arenaPvpTimer / 3F);
					}
				}
				if(arenaPvpTimer > 0) {
					timerInfo = ChatColor.RED + "" + ChatColor.BOLD + "До" + ChatColor.DARK_RED + ChatColor.BOLD + " ПВП" + ChatColor.GRAY + ": " +
							ChatColor.DARK_AQUA + ChatColor.BOLD + arenaPvpTimer;
				} else {
					timerInfo = "";
				}
			}
		}
		if(state == GameState.ENDING) {
			if(TaskManager.isSecUpdated()) {
				endTimer--;
				if(endTimer == 0 || skip) {
					skip = false;
					endGame();
				}
			}
			timerInfo = "";
		}
		if(playing) {
			FightHelper.update();
			ItemRequester.updateItems();
			if(state.isInGame()) {
				for(Landmine mine : landmines) {
					mine.update();
				}
				for(TerraTracer tracer : tracers) {
					tracer.update();
				}
			}
		}
		if(playing && state == GameState.GAME || state == GameState.OUTBREAK) {
			Drops.update();
		}
		if(TaskManager.isSecUpdated() && playing) {
			for(Player p : getInGamePlayers()) {
				updateGameScoreboard(p);
				Player teammate = getTeammate(p);
				String teammateInfo = "";
				if(teammate != null && isPlaying(teammate)) {
					String slash = ChatColor.GRAY + " | ";
					String comma = ChatColor.GRAY + ", ";
					Location l = teammate.getLocation();
					String dist = (p.getWorld() == teammate.getWorld()) ? (String.valueOf((int) l.distance(p.getLocation()))) : ("");
					String loc = ChatColor.DARK_AQUA + "" + l.getBlockX() + comma + ChatColor.DARK_AQUA + l.getBlockY() + comma + ChatColor.DARK_AQUA + l.getBlockZ()
							+ ChatColor.DARK_GRAY + " (" + ChatColor.AQUA + dist + ChatColor.DARK_GRAY + ")";
					teammateInfo =
							ChatColor.GOLD + teammate.getName() + slash + ChatColor.RED + ((int) Math.round(teammate.getHealth())) + ChatColor.DARK_RED + " \u2764"
									+ slash + loc + ChatColor.AQUA + " " + getArrow(p.getLocation(), l);
				}
				if(!teammateInfo.isEmpty()) {
					InventoryHelper.sendActionBarMessage(p, teammateInfo);
				}
			}
			for(Player p : players) {
				ItemStack compass = p.getInventory().getItemInMainHand();
				ItemStack compass2 = p.getInventory().getItemInOffHand();
				boolean showPlayer = false;
				if(CustomItems.tracker.isEquals(compass) || CustomItems.tracker.isEquals(compass2)) {
					List<Player> list = players.stream()
							.filter(player -> player.getWorld() == p.getWorld() && player != p && (getTeammate(player) == null || player != getTeammate(p)))
							.collect(Collectors.toList());
					double dist = Double.MAX_VALUE;
					Player nearest = null;
					for(Player player : list) {
						double d = player.getLocation().distance(p.getLocation());
						if(d < dist) {
							dist = d;
							nearest = player;
						}
					}
					if(nearest != null) {
						p.setCompassTarget(nearest.getLocation());
						showPlayer = true;
					}
				}
				if(!showPlayer) {
					Location drop = null;
					for(Item item : p.getWorld().getEntitiesByClass(Item.class)) {
						if(item.hasMetadata("airdrop")) {
							drop = item.getLocation().clone();
							break;
						}
					}
					if(drop == null) {
						p.setCompassTarget(Drops.AIRDROP.getLocation());
					} else {
						p.setCompassTarget(drop);
					}
				}
			}
		}
	}

	private static char getArrow(Location l1, Location l2) {
		int x1 = l1.getBlockX();
		int z1 = l1.getBlockZ();
		int x2 = l2.getBlockX();
		int z2 = l2.getBlockZ();
		char arrow = '-';
		double d1 = l1.getYaw();
		d1 = d1 % 360.0D;
		double d2 = Math.atan2(z2 - z1, x2 - x1);
		double d0 = Math.PI - ((d1 - 90.0D) * 0.01745329238474369D - d2);
		float f = (float) (d0 / (Math.PI * 2D));
		float res = positiveModulo(f, 1.0F);
		if(res > 1 || ((res < 0.125 && res >= 0) || (res >= 0.875 && res <= 1))) {
			arrow = '\u2191'; // forward
		}
		if(res >= 0.125 && res < 0.375) {
			arrow = '\u2192'; // right
		}
		if(res >= 0.375 && res < 0.625) {
			arrow = '\u2193'; // back
		}
		if(res >= 0.625 && res < 0.875) {
			arrow = '\u2190'; // left
		}
		return arrow;
	}

	private static float positiveModulo(float numerator, float denominator) {
		return (numerator % denominator + denominator) % denominator;
	}

	public static void inviteTeammate(Player p, Player teammate) {
		p.sendMessage(ChatColor.YELLOW + "Ты отправил запрос на создание тимы для " + ChatColor.GOLD + teammate.getName());
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 1F);
		teammate.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " хочет быть твоим тиммейтом");
		teammate.playSound(teammate.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1F);
		teammateChoices.put(p, teammate);
	}

	public static void acceptInvite(Player p, Player teammate) {
		p.sendMessage(ChatColor.YELLOW + "Теперь ты в тиме с " + ChatColor.GOLD + teammate.getName());
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.5F, 1F);
		teammate.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.GREEN + " принял " + ChatColor.YELLOW + "запрос на создание тимы");
		teammate.playSound(teammate.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.5F, 1F);
		teammateChoices.put(p, teammate);
	}

	public static void denyInvite(Player p, Player teammate) {
		p.sendMessage(ChatColor.RED + "Ты отказал " + ChatColor.GOLD + teammate.getName());
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.5F);
		teammate.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.RED + " отклонил " + ChatColor.YELLOW + "запрос на создание тимы");
		teammate.playSound(teammate.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.5F);
		teammateChoices.put(teammate, null);
	}

	public static void leaveTeam(Player p) {
		if(isTeamed(p)) {
			Player teammate = getTeammate(p);
			teammate.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.RED + " больше с тобой не в тиме");
			teammate.playSound(teammate.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.5F);
			p.sendMessage(ChatColor.RED + "У тебя больше нет тиммейта");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.5F);
			teammateChoices.remove(teammate);
		}
		teammateChoices.remove(p);
	}

	public static boolean isInvited(Player p, Player teammate) {
		return getTeammate(p) == teammate && getTeammate(teammate) != p;
	}

	public static boolean isTeammates(Player p1, Player p2) {
		return p1 != null && p2 != null && getTeammate(p1) == p2 && getTeammate(p2) == p1;
	}

	public static boolean isTeamed(Player p) {
		return getTeammate(p) != null && isTeammates(p, getTeammate(p));
	}

	public static Player getTeammate(Player p) {
		return teammateChoices.getOrDefault(p, null);
	}

	public static Inventory getTeammatesInventory(Player p) {
		List<Player> players = WorldManager.getLobby().getPlayers();
		players.removeIf(pl -> pl == p);
		boolean manyPlayers = players.size() >= 10;
		Inventory inv = Bukkit.createInventory(p, manyPlayers ? 54 : 27, ChatColor.GREEN + "Выбор тиммейта");
		for(int i = 0; i < players.size(); i++) {
			Player pl = players.get(i);
			ItemStack head = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			meta.setOwningPlayer(pl);
			meta.setDisplayName(ChatColor.GOLD + pl.getName());
			head.setItemMeta(meta);
			int pos = i >= 9 ? i + 27 : i + 9;
			inv.setItem(pos, head);
			if(isInvited(pl, p)) {
				ItemStack deny = InventoryHelper.generateItemWithName(Material.RED_DYE, ChatColor.RED + "Отклонить");
				ItemStack accept = InventoryHelper.generateItemWithName(Material.LIME_DYE, ChatColor.GREEN + "Принять");
				inv.setItem(pos - 9, deny);
				inv.setItem(pos + 9, accept);
			}
			if(isTeammates(p, pl)) {
				ItemStack deny = InventoryHelper.generateItemWithName(Material.BARRIER, ChatColor.RED + "Выйти из тимы");
				inv.setItem(pos - 9, deny);
			}
		}
		return inv;
	}

	public static int getMapSize() {
		return mapSize == 0 ? 36 : (mapSize == 1 ? 52 : (mapSize == 2 ? 68 : 512));
	}

	public static void updateInventoryTeamItemFor(Player p) {
		ItemStack item0 = InventoryHelper.getFirstStack(p.getInventory(), Material.REDSTONE);
		ItemStack item = item0 == null ? InventoryHelper.getFirstStack(p.getInventory(), Material.PLAYER_HEAD) : item0;
		if(item == null) {
			p.getInventory().setItem(4, getTeammateChooseItem());
			item = p.getInventory().getItem(4);
		}
		Player teammate = getTeammate(p);
		boolean hasTeammate = isTeammates(teammate, p);
		item.setType(hasTeammate ? Material.PLAYER_HEAD : Material.REDSTONE);
		InventoryHelper.setName(item, ChatColor.YELLOW + "Тима: " + (hasTeammate ? ChatColor.GOLD + teammate.getName() : ChatColor.RED + "нет"));
		if(hasTeammate) {
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwningPlayer(teammate);
			item.setItemMeta(meta);
		}
	}

	public static void updateTeams() {
		TaskManager.invokeLater(() -> {

			for(Player p : WorldManager.getLobby().getPlayers()) {
				if(p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.GREEN + "Выбор тиммейта")) {
					p.openInventory(getTeammatesInventory(p));
				}
				if(isDuo) {
					updateInventoryTeamItemFor(p);
				}
				updateLobbyScoreboard(p);
			}

		});
	}

	public static List<Player> getInGamePlayers() {
		List<Player> list = Lists.newArrayList(players);
		list.addAll(spectators);
		return list;
	}

	public static ItemStack getTeammateChooseItem() {
		return InventoryHelper.generateItemWithName(Material.REDSTONE, ChatColor.YELLOW + "Тима: " + ChatColor.RED + "нет");
	}

	public static void draw() {
		if(state != GameState.ENDING) {
			state = GameState.ENDING;
			endTimer = 8;
		}
		for(Player pl : Bukkit.getOnlinePlayers()) {
			if(isPlaying(pl)) pl.setGameMode(GameMode.SPECTATOR);
			pl.sendTitle(ChatColor.YELLOW + "Ничья", ChatColor.GOLD + "Видимо, игра затянулась", 10, 60, 20);
		}
	}

	public static void tryWin() {
		if(teams.size() == 0) endGame();
		if(teams.size() <= 1 && state.isPreGame()) {
			endGame();
		}
		if(teams.size() == 1) {
			for(Player p : players) {
				increaseGames(p);
			}
			PlayerTeam team = teams.get(0);
			if(state != GameState.ENDING) {
				state = GameState.ENDING;
				endTimer = 8;
			}
			Player p1 = team.getPlayer1();
			Player p2 = getTeammate(p1);
			addPoints(p1, p2 == null ? 50 : 35);
			increaseStat(p1, PlayerStat.WINS, 1);
			if(p2 != null) {
				addPoints(p2, 35);
				increaseStat(p2, PlayerStat.WINS, 1);
			}
			boolean dual = p2 != null && p2.isOnline();
			if(dual) {
				String names = ChatColor.GOLD + p1.getName() + ChatColor.YELLOW + " и " + ChatColor.GOLD + p2.getName();
				for(Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(names + ChatColor.GREEN + " победили!");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
				}
			} else {
				for(Player pl : Bukkit.getOnlinePlayers()) {
					pl.sendMessage(ChatColor.GOLD + p1.getName() + ChatColor.GREEN + " победил!");
					pl.playSound(pl.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
				}
			}
			List<Player> pls = new ArrayList<>();
			pls.add(p1);
			if(p2 != null && p2.isOnline()) pls.add(p2);
			for(Player p : pls) {
				if(isInGame(p)) {
					p.setGameMode(GameMode.SPECTATOR);
					p.sendTitle(ChatColor.YELLOW + "Ты победил!", "", 10, 40, 20);
					Firework firework = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
					FireworkMeta meta = firework.getFireworkMeta();
					meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GREEN).withFlicker().build());
					firework.setFireworkMeta(meta);
				}
			}
			List<Mutator> hiddenMutators = MutatorManager.activeMutators.stream().filter(Mutator::isHidden).collect(Collectors.toList());
			if(hiddenMutators.size() > 0) {
				for(Player p : getInGamePlayers()) {
					if(hiddenMutators.size() == 1) {
						p.sendMessage(ChatColor.YELLOW + "Скрытым мутатором был: " + ChatColor.LIGHT_PURPLE + hiddenMutators.get(0).getName());
					} else {
						p.sendMessage(ChatColor.YELLOW + "Скрытыми мутаторами были:");
						for(Mutator mutator : hiddenMutators) {
							p.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.LIGHT_PURPLE + mutator.getName());
						}
					}
				}
			}
		}
	}

	private static void removePlayerInfoOnLeave(Player p) {
		players.remove(p);
		for(PlayerTeam team : teams) {
			if(team.contains(p)) {
				team.remove(p);
			}
		}
		teams.removeIf(PlayerTeam::isEmpty);
	}

	public static void inGameLeave(Player p, boolean death) {
		if(isSpectator(p)) {
			spectators.remove(p);
			resetPlayer(p);
			p.setGameMode(GameMode.ADVENTURE);
			p.teleport(WorldManager.getLobby().getSpawnLocation());
		}
		if(isPlaying(p)) {
			resetPlayer(p);
			if(death) {
				heal(p);
				if(p.getLastDamageCause() != null && p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID) {
					p.teleport(p.getWorld().getSpawnLocation());
				}
				p.setGameMode(GameMode.SPECTATOR);
				spectators.add(p);
				p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
				if(MutatorManager.isActive(MutatorManager.meetingPlace)) {
					MutatorManager.meetingPlace.bar.addPlayer(p);
				}
			} else {
				p.setGameMode(GameMode.ADVENTURE);
				p.teleport(WorldManager.getLobby().getSpawnLocation());
			}

			removePlayerInfoOnLeave(p);

			boolean fewTeams = isDuo && teams.size() <= 3;
			boolean fewPlayers = players.size() <= 4 && players.size() > 1;
			if(state == GameState.OUTBREAK && (fewPlayers || fewTeams)) {
				state = GameState.GAME;
				deathmatchTimer = 60 * getGameDuration();
				WorldManager.getGameMap().setPVP(true);
				for(Player pl : getInGamePlayers()) {
					pl.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 0.5F);
					pl.sendTitle(ChatColor.GOLD + "ПВП Включено!", "", 10, 60, 30);
				}
			}
			int minTime = 420;
			if(state == GameState.GAME && deathmatchTimer > minTime && (fewPlayers || fewTeams)) {
				if(players.size() == 4 || teams.size() == 3) deathmatchTimer = (int) Math.max(deathmatchTimer / 1.5, minTime);
				if(players.size() == 3) deathmatchTimer = Math.max(deathmatchTimer / 2, minTime);
				if(players.size() == 2) deathmatchTimer = minTime;
				for(Player player : getInGamePlayers()) {
					player.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1.2F);
					String remainInfo = new NumericalCases("остался ", "осталось ", "осталось ").byNumber(players.size());
					String playerInfo = new NumericalCases(" игрок.", " игрока.", " игроков.").byNumber(players.size());
					player.sendMessage(ChatColor.GOLD + "В живых " + remainInfo + ChatColor.AQUA + ChatColor.BOLD + players.size() +
							ChatColor.GOLD + playerInfo + ChatColor.RED + ChatColor.BOLD + " Время сокращено!");
				}
			}

			if(MutatorManager.isActive(MutatorManager.oxygen)) {
				MutatorManager.oxygen.unregister(p);
			}

			increaseGames(p);
			tryWin();
		}
		if(isInGame(p)) {
			if(MutatorManager.isActive(MutatorManager.meetingPlace)) {
				MutatorManager.meetingPlace.bar.removePlayer(p);
			}
			refreshScoreboards();
		}
	}

	public static boolean isSpectator(Player p) {
		return spectators.contains(p);
	}

	public static boolean isInGame(Player p) {
		return isPlaying(p) || isSpectator(p);
	}

	public static boolean isInLobby(Player p) {
		return WorldManager.getLobby().getPlayers().contains(p);
	}

	public static boolean isPlaying(Player p) {
		return players.contains(p);
	}

	public static void resetPlayer(Player p) {
		p.getInventory().clear();
		p.getActivePotionEffects().forEach(ef -> p.removePotionEffect(ef.getType()));
		heal(p);
		p.setFireTicks(0);
		p.setNoDamageTicks(0);
		p.setExp(0);
		p.setLevel(0);
	}

	public static void increaseGames(Player p) {
		if(!state.isPreGame() && !processedPlayers.contains(p)) {
			increaseStat(p, PlayerStat.GAMES, 1);
			processedPlayers.add(p);
		}
	}

	public static void endPreparing() {
		state = GameState.OUTBREAK;
		if(UHC.fastStart != 1) {
			if(MutatorManager.activeMutators.size() != mutatorCount) {
				int size = MutatorManager.activeMutators.size();
				for(int i = 0; i < mutatorCount - size; i++) {
					MutatorManager.activateRandomMutator(true, true);
				}
			}
		}
		for(Player p : players) {
			glassPlates.forEach(location -> location.getBlock().setType(Material.AIR));
			if(MutatorManager.isActive(MutatorManager.hungerGames)) {
				p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Игра " + ChatColor.GOLD + ChatColor.BOLD + "началась!",
						ChatColor.YELLOW + "У тебя " + ChatColor.DARK_RED + ChatColor.BOLD + "ОДНА СУКА МИНУТА" +
								ChatColor.YELLOW + " на развитие без ПВП", 10, 60, 30);
			} else {
				p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Игра " + ChatColor.GOLD + ChatColor.BOLD + "началась!",
						ChatColor.YELLOW + "У тебя " + ChatColor.AQUA + ChatColor.BOLD + getNoPVPDuration() + ChatColor.YELLOW + " минут на развитие без ПВП",
						10, 60, 30);
			}
			p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5F, 1);
			p.getInventory().clear();
			p.getActivePotionEffects().forEach(ef -> p.removePotionEffect(ef.getType()));
			heal(p);
			if(MutatorManager.lessHealth.isActive()) {
				p.setHealth(6);
			}
			p.setNoDamageTicks(160);
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 9, true, true, true));
			Inventory inv = p.getInventory();
			for(Mutator inventoryMutator : MutatorManager.activeMutators) {
				if(inventoryMutator instanceof ItemBasedMutator) {
					inv.addItem(((ItemBasedMutator) inventoryMutator).getItemsToAdd().toArray(new ItemStack[0]));
				}
			}
		}
		if(MutatorManager.isActive(MutatorManager.hungerGames)) {
			outbreakTimer = 60;
		} else {
			outbreakTimer = 60 * getNoPVPDuration();
		}
	}

	public static void passVote(Player p, boolean vote) {
		voteResults.put(p, vote);
		p.getWorld().playSound(p.getLocation(), vote ? Sound.ENTITY_VILLAGER_YES : Sound.ENTITY_VILLAGER_NO, 1, 1);
		p.getInventory().clear();
		ParticleUtils.createParticlesAround(p, Particle.REDSTONE, vote ? Color.LIME : Color.RED, 20);
		p.getLocation().clone().add(0, -1, 0).getBlock().setType(vote ? Material.LIME_STAINED_GLASS : Material.RED_STAINED_GLASS);
		updateVoteBar();
		if(voteResults.size() == players.size()) {
			endVote();
		}
	}

	private static void updateVoteBar() {
		int votesFor = 0;
		for(Player pl : voteResults.keySet()) {
			if(voteResults.get(pl)) votesFor++;
		}
		int votesAgainst = voteResults.size() - votesFor;
		double progress;
		if(!voteResults.isEmpty()) {
			progress = votesFor / (double) voteResults.size();
		} else {
			progress = 0.5;
		}
		if(progress >= 0.6) {
			voteBar.setColor(BarColor.GREEN);
		} else {
			voteBar.setColor(BarColor.RED);
		}
		voteBar.setTitle(ChatColor.GOLD + "Голосование " + ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + ChatColor.BOLD + votesFor + ChatColor.GRAY + " / " +
				ChatColor.RED + ChatColor.BOLD + votesAgainst + ChatColor.DARK_GRAY + ")");
		voteBar.setProgress(progress);
	}

	public static void heal(Player p) {
		p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		p.setHealth(20);
		p.setSaturation(20);
		p.setExhaustion(20);
		p.setFoodLevel(20);
	}

	public static String getDeathMessage(Player p, EntityDamageEvent.DamageCause cause) {
		String name = ChatColor.GOLD + p.getName() + ChatColor.RED + " ";
		switch (cause) {
			case CONTACT -> name += "умер от кактуса, серьезно?";
			case ENTITY_ATTACK -> name += "погиб";
			case ENTITY_SWEEP_ATTACK -> name += "умер от свайпа, бля...";
			case PROJECTILE -> name += "застрелили";
			case SUFFOCATION -> name += "задохнулся в стене";
			case FALL -> name += "позорно разбился";
			case FIRE, FIRE_TICK -> name += "сгорел";
			case MELTING -> name += "расплавился";
			case LAVA -> name += "трагически сгорел в лаве";
			case DROWNING -> name += "позорно захлебнулся";
			case BLOCK_EXPLOSION -> name += "взорвался";
			case ENTITY_EXPLOSION -> name += "взорвался";
			case VOID -> name += "выпал из мира";
			case LIGHTNING -> name += "может купить лотерейный билет";
			case SUICIDE -> name += "суициднулся";
			case STARVATION -> name += "умер от голода";
			case POISON -> name += "отравился";
			case MAGIC -> name += "взорвался";
			case WITHER -> name += "высох";
			case FALLING_BLOCK -> name += "расплющился";
			case THORNS -> name += "напал на жесткого челика с чаром на шипы";
			case DRAGON_BREATH -> name += "хз как но умер от дракона";
			case CUSTOM -> name += "умер";
			case FLY_INTO_WALL -> name += "влетел в стену";
			case HOT_FLOOR -> name += "поджарился на магме";
			case CRAMMING -> name += "раздавили мобы";
			case DRYOUT -> name += "высох";
			default -> name += "странно умер";
		}
		return FightHelper.padCrosses(name);
	}

	public static void teleportToParkour(Player p) {
		Location newLoc = parkourStart.clone();
		newLoc.setYaw(p.getLocation().getYaw());
		newLoc.setPitch(p.getLocation().getPitch());
		p.teleport(newLoc);
		p.setFireTicks(0);
		p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT_ON_FIRE, 0.5F, 1.2F);
	}

	public static ItemStack getBonusShell() {
		return ItemUtils.builder(Material.SHULKER_SHELL).withGlow().withName(ChatColor.LIGHT_PURPLE + "Сияющий панцирь")
				.withSplittedLore(ChatColor.GOLD + "Окружи его золотыми слитками и получи 2 золотых яблока").build();
	}

	public static void giveAllRecipes(Player player) {
		Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
		while(iterator.hasNext()) {
			Advancement adv = iterator.next();
			if(adv.getKey().getKey().startsWith("recipes")) {
				AdvancementProgress progress = player.getAdvancementProgress(adv);
				for(String criteria : progress.getRemainingCriteria()) {
					progress.awardCriteria(criteria);
				}
			}
		}
	}

	public static void resetAdvancements(Player player) {
		Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
		while(iterator.hasNext()) {
			Advancement adv = iterator.next();
			if(!adv.getKey().getKey().startsWith("recipes")) {
				AdvancementProgress progress = player.getAdvancementProgress(adv);
				for(String criteria : progress.getAwardedCriteria()) {
					progress.revokeCriteria(criteria);
				}
			}
		}
	}

	public static void resetAllAdvancements() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			resetAdvancements(player);
		}
	}

	private static String invViewStart = ChatColor.GRAY + "Инвентарь";

	public static void viewInventory(Player observer, Player target) {
		Inventory inv = Bukkit.createInventory(observer, 9 * 5, invViewStart + ChatColor.GOLD + " " + target.getName());
		for(int i = 0; i < target.getInventory().getContents().length; i++) {
			ItemStack item = target.getInventory().getContents()[i];
			inv.setItem(i, item);
		}
		for(int i = 0; i < target.getInventory().getArmorContents().length; i++) {
			ItemStack armor = target.getInventory().getArmorContents()[i];
			inv.setItem(9 * 4 + 2 + i, armor);
		}
		observer.openInventory(inv);
	}

	@EventHandler
	public void noClick(InventoryClickEvent e) {
		if(e.getView().getTitle().startsWith(invViewStart)) {
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void lobbyInvClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		InventoryView view = e.getView();
		Inventory inv = e.getInventory();
		int slot = e.getRawSlot();
		ItemStack item = e.getCurrentItem() == null ? new ItemStack(Material.AIR) : e.getCurrentItem();
		if(view.getTitle().equals(PlayerOptions.invName) && e.getClickedInventory() == view.getTopInventory()) {
			PlayerOptions option = PlayerOptions.values()[slot];
			if(option != null) {
				option.setActive(p, !option.isActive(p));
				PlayerOptions.openInventory(p);
			}
		}
		if(isInLobby(p) && view.getTitle().equals(ChatColor.GREEN + "Выбор тиммейта")) {
			if(item.getType() == Material.PLAYER_HEAD && getTeammate(p) == null) {
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				Player owner = Bukkit.getPlayerExact(meta.getOwner());
				if(owner != null && owner.isOnline()) {
					if(!isInvited(owner, p)) {
						inviteTeammate(p, owner);
						updateTeams();
						p.closeInventory();
					}
				} else {
					p.sendMessage(ChatColor.RED + "Этот человек уже не на серве");
					p.openInventory(getTeammatesInventory(p));
				}
			}
			if(item.getType() == Material.RED_DYE) {
				SkullMeta meta = (SkullMeta) inv.getItem(slot + 9).getItemMeta();
				Player owner = Bukkit.getPlayerExact(meta.getOwner());
				if(owner != null && owner.isOnline()) {
					denyInvite(p, owner);
					p.closeInventory();
					updateTeams();
				} else {
					p.sendMessage(ChatColor.RED + "Этот человек уже не на серве");
					p.openInventory(getTeammatesInventory(p));
				}
			} else if(item.getType() == Material.LIME_DYE) {
				SkullMeta meta = (SkullMeta) inv.getItem(slot - 9).getItemMeta();
				Player owner = Bukkit.getPlayerExact(meta.getOwner());
				if(owner != null && owner.isOnline()) {
					acceptInvite(p, owner);
					p.closeInventory();
					updateTeams();
				} else {
					p.sendMessage(ChatColor.RED + "Этот человек уже не на серве");
					p.openInventory(getTeammatesInventory(p));
				}
			}
			if(item.getType() == Material.BARRIER) {
				leaveTeam(p);
				p.closeInventory();
				updateTeams();
			}
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void damage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player p) {
			if(isInLobby(p) && !PvpArena.isOnArena(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void damageByEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player p && e.getDamager() instanceof Player damager) {
			if(isTeammates(p, damager) && !isInLobby(p)) e.setCancelled(true);
		}
	}

	@EventHandler
	public void consume(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		if(isPlaying(p)) {
			ItemStack item = e.getItem();
			//Fixing overpowered golden apples
			if(item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
				TaskManager.invokeLater(() -> {

					PotionEffect regen = p.getPotionEffect(PotionEffectType.REGENERATION);
					if(regen != null) {
						p.addPotionEffect(new PotionEffect(regen.getType(), regen.getDuration(), regen.getAmplifier() / 2, regen.isAmbient(), regen.hasParticles(),
								regen.hasIcon()));
					}

					PotionEffect absorp = p.getPotionEffect(PotionEffectType.ABSORPTION);
					if(absorp != null) {
						p.addPotionEffect(
								new PotionEffect(absorp.getType(), absorp.getDuration() / 2, absorp.getAmplifier() / 2, absorp.isAmbient(), absorp.hasParticles(),
										absorp.hasIcon()));
					}

				});
			}
			//Fixing stews to regenerate only 1 heart instead of 1.5 hearts
			if(item.getType() == Material.SUSPICIOUS_STEW) {
				SuspiciousStewMeta meta = (SuspiciousStewMeta) item.getItemMeta();
				if(meta.hasCustomEffect(PotionEffectType.REGENERATION)) {
					TaskManager.invokeLater(() -> {

						PotionEffect regen = p.getPotionEffect(PotionEffectType.REGENERATION);
						if(regen != null) {
							p.addPotionEffect(new PotionEffect(regen.getType(), (int) (regen.getDuration() / 1.5), regen.getAmplifier(), regen.isAmbient(),
									regen.hasParticles(), regen.hasIcon()));
						}

					});
				}
			}
		}
	}

	@EventHandler
	public void noMinecartCollide(VehicleEntityCollisionEvent e) {
		if(e.getEntity() instanceof Player p) {
			if(p.getGameMode() == GameMode.ADVENTURE && isInLobby(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void noMinecartDamage(VehicleDamageEvent e) {
		if(e.getAttacker() instanceof Player p) {
			if(p.getGameMode() == GameMode.ADVENTURE && isInLobby(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void noItemFrameInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		Entity ent = e.getRightClicked();
		if(ent instanceof ItemFrame && p.getGameMode() == GameMode.ADVENTURE && isInLobby(p)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void noLobbyGrief(HangingBreakByEntityEvent e) {
		if(e.getRemover() instanceof Player p) {
			if(p.getGameMode() == GameMode.ADVENTURE && isInLobby(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void noFrameItemBreak(EntityDamageByEntityEvent e) {
		if(e.getEntityType() == EntityType.ITEM_FRAME && e.getDamager() instanceof Player && isInLobby((Player) e.getDamager())
				&& ((Player) e.getDamager()).getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void preJoin(AsyncPlayerPreLoginEvent e) {
		if(generating) {
			e.setKickMessage(ChatColor.GOLD + "Сейчас идет генерация мира, зайди немного позже");
			e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		}
	}

	@EventHandler
	public void preventPhantomSpawn(CreatureSpawnEvent e) {
		if(e.getEntityType() == EntityType.PHANTOM) {
			e.setCancelled(true);
		}
	}



	@EventHandler
	public void entityDeath(EntityDeathEvent e) {
		Entity ent = e.getEntity();
		if(ent.getType() == EntityType.ZOMBIE || ent.getType() == EntityType.ZOMBIE_VILLAGER) {
			if(e.getDrops().stream().noneMatch(item -> item.getType() == Material.CARROT) && MathUtils.chance(50)) e.getDrops().add(new ItemStack(Material.CARROT));
		}
		if(ent instanceof PigZombie) {
			if(MathUtils.chance(30)) e.getDrops().add(new ItemStack(Material.GOLD_INGOT, MathUtils.chance(30) ? 2 : 1));
		}
		if(ent instanceof Blaze) {
			if(e.getDrops().stream().noneMatch(item -> item.getType() == Material.BLAZE_ROD)) e.getDrops().add(new ItemStack(Material.BLAZE_ROD));
		}
		if(ent instanceof MagmaCube) {
			if(e.getDrops().stream().noneMatch(item -> item.getType() == Material.MAGMA_CREAM) && MathUtils.chance(50))
				e.getDrops().add(new ItemStack(Material.MAGMA_CREAM));
		}
		if(ent instanceof WitherSkeleton) {
			e.getDrops().add(new ItemStack(Material.NETHER_WART));
		}
	}

	@EventHandler
	public void allowMusicDisks(PlayerInteractEvent e) {
		if(e.getPlayer().getWorld() == WorldManager.getLobby() && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.JUKEBOX
				&& e.getHand() == EquipmentSlot.HAND && e.getPlayer().getGameMode() == GameMode.ADVENTURE) {
			Jukebox jukebox = (Jukebox) e.getClickedBlock().getState();
			if(!jukebox.isPlaying() && jukebox.getRecord().getType() == Material.AIR && e.getItem() != null && e.getItem().getType().isRecord()) {
				e.setCancelled(true);
				e.setUseInteractedBlock(Event.Result.DENY);
				e.setUseItemInHand(Event.Result.DENY);
				jukebox.setRecord(e.getItem().clone());
				e.getItem().setAmount(e.getItem().getAmount() - 1);
				TaskManager.invokeLater(jukebox::update);
			}
		}
	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		Player p = e.getEntity();
		if(isPlaying(p)) {
			Player killer = FightHelper.getKiller(p);
			if(killer != null) {
				p.getWorld().dropItem(p.getLocation(), getBonusShell());
				addPoints(killer, 10);
				increaseStat(killer, PlayerStat.KILLS, 1);
				for(Player pl : getInGamePlayers()) {
					pl.sendMessage(FightHelper.getDeathMessage(p));
				}
				String taunt = ChatColor.DARK_AQUA + " " + MathUtils.choose("тебя унизил", "умнее тебя", "сильнее тебя", "не такой тупой, как кажется",
						"иногда проявляет себя", "играет в кубики лучше тебя");
				p.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Тебя замачили!", ChatColor.GOLD + killer.getName() + taunt, 10, 60, 20);
			} else {
				boolean golden = state == GameState.OUTBREAK;
				ItemStack apple = InventoryHelper.generateItemWithName(golden ? Material.GOLDEN_APPLE : Material.APPLE,
						golden ? (ChatColor.DARK_GREEN + "Золотое бонусное яблоко") : (ChatColor.GREEN + "Бонусное яблоко"), !golden);
				InventoryHelper.setValue(apple, ChatColor.YELLOW + "Владелец", ChatColor.GOLD + p.getName(), false);
				p.getWorld().dropItem(p.getLocation(), apple);
				if(p.getLastDamageCause() != null && lastLeft != p) {
					for(Player pl : getInGamePlayers()) {
						pl.sendMessage(getDeathMessage(p, p.getLastDamageCause().getCause()));
					}
				}
				lastLeft = null;
				p.sendTitle(ChatColor.DARK_RED + "Ты погиб!", "", 10, 60, 20);
			}
			Player teammate = getTeammate(p);
			if(teams.size() == 3 && (teammate == null || !isPlaying(teammate))) {
				String info = ChatColor.YELLOW + "Ты занял " + ChatColor.DARK_AQUA + ChatColor.BOLD + "третье" + ChatColor.RESET + ChatColor.YELLOW + " место!";
				addPoints(p, teammate == null ? 30 : 20);
				p.sendMessage(info);
				if(teammate != null) {
					addPoints(teammate, 20);
					teammate.sendMessage(info);
				}
			}
			if(teams.size() == 2 && (teammate == null || !isPlaying(teammate))) {
				String info = ChatColor.YELLOW + "Ты занял " + ChatColor.AQUA + ChatColor.BOLD + "второе" + ChatColor.RESET + ChatColor.YELLOW + " место!";
				addPoints(p, teammate == null ? 40 : 25);
				p.sendMessage(info);
				if(teammate != null) {
					addPoints(teammate, 25);
					teammate.sendMessage(info);
				}
			}

			p.getWorld().strikeLightningEffect(p.getLocation());
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, Float.MAX_VALUE, 0.8F);
			inGameLeave(p, true);
		}
	}

	@EventHandler
	public void pickup(EntityPickupItemEvent e) {
		if(e.getEntity() instanceof Player p) {
			Player teammate = getTeammate(p);
			ItemStack item = e.getItem().getItemStack();
			boolean hasVal = InventoryHelper.hasValue(item, "Владелец");
			if(teammate != null && hasVal && ChatColor.stripColor(InventoryHelper.getStringValue(item, "Владелец")).equalsIgnoreCase(teammate.getName())) {
				e.setCancelled(true);
			} else {
				if(hasVal) {
					InventoryHelper.removeLore(item, "Владелец");
				}
			}
		}
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		Player sender = e.getPlayer();
		String mes = e.getMessage();
		boolean onlyLocal = PlayerOptions.ONLY_LOCAL.isActive(sender) && isInGame(sender);
		boolean local = mes.startsWith(".") || onlyLocal;
		if(local && !onlyLocal) mes = mes.substring(1);
		String suffix = ChatColor.GOLD + sender.getName() + ChatColor.WHITE + ": " + mes;
		String prefix = ChatColor.LIGHT_PURPLE + "<Локально> ";
		for(Player receiver : Bukkit.getOnlinePlayers()) {
			if(!local) {
				if(isInLobby(sender) && isInGame(receiver)) {
					receiver.sendMessage(ChatColor.YELLOW + "<Лобби> " + suffix);
					continue;
				}
				if(isInLobby(receiver) && isInGame(sender)) {
					receiver.sendMessage(ChatColor.AQUA + "<Игра> " + suffix);
					continue;
				}
				if(isSpectator(sender)) {
					receiver.sendMessage(ChatColor.DARK_RED + "<Мертв> " + suffix);
					continue;
				}
				receiver.sendMessage(suffix);
			} else {
				if((isInLobby(sender) && isInLobby(receiver)) || (isSpectator(sender) && isSpectator(receiver)) || (!isDuo && isPlaying(sender) && isPlaying(
						receiver))) {
					receiver.sendMessage(prefix + suffix);
					continue;
				}
				Player teammate = getTeammate(sender);
				if(isDuo && ((teammate != null && teammate == receiver) || receiver == sender)) {
					receiver.sendMessage(ChatColor.LIGHT_PURPLE + "<Тиме> " + suffix);
				}
			}
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		Player p = e.getPlayer();
		showPlayer(p);
		if(isInGame(p)) {
			for(Player pl : getInGamePlayers()) {
				if(isSpectator(p)) {
					pl.sendMessage(ChatColor.AQUA + "Наблюдатель " + ChatColor.GOLD + p.getName() + ChatColor.AQUA + " отключился");
				} else {
					if(state.isPreGame()) {
						pl.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.DARK_RED + ChatColor.BOLD + " вылетел с сервера");
						pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_PLAYER_BURP, 2F, 1F);
						ParticleUtils.createParticlesAround(p, Particle.SMOKE_NORMAL, null, 20);
					} else {
						pl.sendMessage(FightHelper.padCrosses(ChatColor.GOLD + p.getName() + ChatColor.DARK_RED + " вышел из игры"));
						lastLeft = p;
					}
				}
			}
			if(isPlaying(p)) {
				if(state.isPreGame()) {
					removePlayerInfoOnLeave(p);
				} else {
					p.setHealth(0);
				}
			}
			if(MutatorManager.isActive(MutatorManager.meetingPlace)) {
				MutatorManager.meetingPlace.bar.removePlayer(p);
			}
			if(MutatorManager.isActive(MutatorManager.oxygen)) {
				MutatorManager.oxygen.unregister(p);
			}
		} else {
			updateTeams();
			leaveTeam(p);
			for(Player pl : WorldManager.getLobby().getPlayers()) {
				pl.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "- " + ChatColor.RESET + ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " отключился");
			}
			PvpArena.onArena.remove(p);
			refreshLobbyScoreboardLater();
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Player p = e.getPlayer();
		p.setGameMode(GameMode.ADVENTURE);
		resetPlayer(p);
		PlayerStat.defaultStats(p);
		giveAllRecipes(p);
		String msg = ChatColor.GREEN + "" + ChatColor.BOLD + "+ " + ChatColor.RESET + ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " присоединился";
		for(Player pl : WorldManager.getLobby().getPlayers()) {
			pl.sendMessage(msg);
		}
		p.sendMessage(msg);
		if(isDuo) p.getInventory().setItem(4, getTeammateChooseItem());
		if(!isInLobby(p)) p.teleport(WorldManager.getLobby().getSpawnLocation());
		if(playing) {
			refreshGameScoreboardLater();
			p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Сейчас идет игра! " + ChatColor.RESET + ChatColor.AQUA
					+ "За игрой можно наблюдать, нажав по табличке на стене.");
		} else {
			if(MutatorManager.getAvailablePreferences().getOrDefault(p.getName(), new HashSet<>()).isEmpty()) {
				p.sendMessage(ChatColor.GOLD + "Проголосуй за " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "мутаторы" + ChatColor.RESET + ChatColor.GOLD +
						"! Команда: " + ChatColor.YELLOW + ChatColor.BOLD + "/mutator");
			}
		}
		refreshLobbyScoreboardLater();
	}

	@EventHandler
	public void craft(CraftItemEvent e) {
		if(e.getRecipe().getResult().getType() == Material.EMERALD_BLOCK) {
			if(Stream.of(e.getInventory().getMatrix()).anyMatch(item -> !ItemUtils.getLore(item).isEmpty())) {
				Player p = (Player) e.getWhoClicked();
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 0.5F, 1F);
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void craft(PrepareItemCraftEvent e) {
		Recipe recipe = e.getRecipe();
		if(recipe != null && e.getRecipe().getResult().getType() == Material.EMERALD_BLOCK) {
			if(Stream.of(e.getInventory().getMatrix()).anyMatch(item -> !ItemUtils.getLore(item).isEmpty())) {
				recipe.getResult().setAmount(0);
			}
		}
	}

	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		if((state == GameState.VOTE || state == GameState.PREPARING) || (isInLobby(e.getPlayer()) && e.getPlayer().getGameMode() != GameMode.CREATIVE)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void swap(PlayerSwapHandItemsEvent e) {
		if((state == GameState.VOTE || state == GameState.PREPARING)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void emptyBucket(PlayerBucketEmptyEvent e) {
		if((state == GameState.VOTE || state == GameState.PREPARING)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void fillBucket(PlayerBucketFillEvent e) {
		if((state == GameState.VOTE || state == GameState.PREPARING)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void portal(PlayerPortalEvent e) {
		Player player = e.getPlayer();
		if(playing && isPlaying(player)) {
			Location loc = player.getLocation();
			if(player.getWorld().getEnvironment() == World.Environment.NETHER) {
				Location newLoc = new Location(WorldManager.getGameMap(), loc.getX() * 8, loc.getY(), loc.getZ() * 8);
				if(!WorldManager.getGameMap().getWorldBorder().isInside(newLoc)) {
					newLoc = WorldManager.getGameMap().getSpawnLocation();
				}
				e.setTo(newLoc);
			} else {
				e.setTo(new Location(WorldManager.getGameMapNether(), loc.getX() / 8, loc.getY(), loc.getZ() / 8));
			}
		}
	}

	@EventHandler
	public void place(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(isInLobby(p) && p.getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
		if(isPlaying(p) && (state == GameState.PREPARING || state == GameState.VOTE)) {
			e.setCancelled(true);
		}
		if(isPlaying(p) && state.isInGame()) {
			ItemStack item = e.getItemInHand();
			if(item.getType() == Material.DIRT) {
				Block b = e.getBlock();
				if(landmines.stream().anyMatch(mine -> WorldHelper.compareLocations(b.getLocation().clone().add(0, -1, 0), mine.getLocation()))) {
					b.setType(Material.GRASS_BLOCK);
					ParticleUtils.createParticlesOutline(b, Particle.VILLAGER_HAPPY, null, 20);
					b.getWorld().playSound(b.getLocation(), Sound.BLOCK_CHORUS_FLOWER_GROW, 1F, 1F);
				}
			}
		}
		if(isPlaying(p) && state == GameState.DEATHMATCH) {
			if(!CustomItems.tnt.isEquals(e.getItemInHand()) && !MutatorManager.interactiveArena.isActive()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void breakBlock(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if((state.isPreGame() || state == GameState.ENDING) && isInGame(e.getPlayer())) {
			e.setCancelled(true);
		}
		if(state.isInGame() && isInGame(p)) {
			if(e.getBlock().getType() == Material.EMERALD_ORE && e.getExpToDrop() > 0) {
				e.setDropItems(false);
				ItemStack drop = MathUtils
						.choose(new ItemStack(Material.DIAMOND, MathUtils.randomRange(1, 2)), new ItemStack(Material.REDSTONE, MathUtils.randomRange(24, 40)),
								new ItemStack(Material.GOLD_INGOT, MathUtils.randomRange(5, 8)));
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
				e.setExpToDrop(e.getExpToDrop() * 5);
			}
		}
		if(state.isInGame() && e.getBlock().getType() == Material.BEACON) {
			if(tracers.stream().anyMatch(tracer -> WorldHelper.compareLocations(tracer.getLocation(), e.getBlock().getLocation()))) {
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), CustomItems.terraTracer.getItemStack());
				tracers.removeIf(tracer -> WorldHelper.compareLocations(tracer.getLocation(), e.getBlock().getLocation()));
				e.setDropItems(false);
			}
		}
		if(isPlaying(p) && state == GameState.DEATHMATCH) {
			if(MutatorManager.interactiveArena.isActive()) {
				e.setDropItems(false);
				e.setExpToDrop(0);
			} else {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void lectern(PlayerInteractEvent e) {
		if(isInLobby(e.getPlayer()) && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = e.getPlayer();
			Block block = e.getClickedBlock();
			if(block.getType() == Material.LECTERN) {
				if(!e.getPlayer().getInventory().contains(Material.WRITTEN_BOOK)) {
					Lectern lectern = (Lectern) block.getState();
					ItemStack book = lectern.getInventory().getItem(0);
					if(book != null) {
						player.getInventory().addItem(book);
					}
				}
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void bucket(PlayerBucketFillEvent e) {
		if(isPlaying(e.getPlayer()) && state == GameState.DEATHMATCH && !MutatorManager.interactiveArena.isActive()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void bucket(PlayerBucketEmptyEvent e) {
		if(isPlaying(e.getPlayer()) && state == GameState.DEATHMATCH && !MutatorManager.interactiveArena.isActive()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void noTeleport(PlayerTeleportEvent e) {
		if(e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE && e.getTo().getWorld() == WorldManager.getLobby()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void spectatorOpenInv(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if((state.isInGame() || state == GameState.DEATHMATCH) &&
				e.getHand() == EquipmentSlot.HAND && e.getRightClicked() instanceof Player clicked && isSpectator(p)) {
			if(isPlaying(clicked)) {
				viewInventory(p, clicked);
			}
		}
	}

	@EventHandler
	public void emeraldTrade(PlayerInteractEntityEvent e) {
		if(e.getRightClicked().getType() == EntityType.VILLAGER) {
			Villager villager = (Villager) e.getRightClicked();
			if (!(villager instanceof CraftVillager)) {
				return;
			}
			List<MerchantRecipe> newRecipes = new ArrayList<>();
			MerchantRecipeList recipes = ((CraftVillager)villager).getHandle().getOffers();
			Iterator<MerchantRecipe> recipeIterator;
			for(recipeIterator = recipes.iterator(); recipeIterator.hasNext(); ) {
				MerchantRecipe recipe = recipeIterator.next();
				ItemStack copy = CraftItemStack.asBukkitCopy(recipe.getSellingItem());
				if(copy.getType() == Material.EMERALD) {
					ItemUtils.setLore(copy, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Без бонусов!");
					newRecipes.add(new MerchantRecipe(
							recipe.getBuyItem1(),
							recipe.getBuyItem2(),
							CraftItemStack.asNMSCopy(copy),
							recipe.getUses(),
							recipe.getMaxUses(),
							recipe.getXp(),
							recipe.getPriceMultiplier(),
							recipe.getDemand()
					));
					recipeIterator.remove();
				}
			}
			recipes.addAll(newRecipes);
		}
	}

	@EventHandler
	public void interact(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(state == GameState.VOTE && isPlaying(p) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			ItemStack item = p.getInventory().getItemInMainHand();
			if(item.getType() != Material.AIR) {
				passVote(p, item.getType() == Material.LIME_DYE);
			}
			e.setCancelled(true);
		}
		ItemStack item = e.getItem();
		if(state.isInGame() && isPlaying(p) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && item != null
				&& item.getType() == Material.EMERALD && ItemUtils.getLore(item).isEmpty()) {
			ItemStack drop = MathUtils
					.choose(new ItemStack(Material.DIAMOND, MathUtils.randomRange(1, 2)), new ItemStack(Material.REDSTONE, MathUtils.randomRange(24, 40)),
							new ItemStack(Material.GOLD_INGOT, MathUtils.randomRange(5, 8)));
			item.setAmount(item.getAmount() - 1);
			p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5F, 1F);
			p.getWorld().dropItemNaturally(p.getLocation(), drop);
		}
		if(!playing && isInLobby(p) && item != null && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if(item.getType() == Material.REDSTONE || item.getType() == Material.PLAYER_HEAD) {
				p.openInventory(getTeammatesInventory(p));
			}
		}
		if(state == GameState.PREPARING && isPlaying(p)) {
			e.setCancelled(true);
		}
		if(state.isInGame() && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.BEACON) {
			if(tracers.stream().anyMatch(tracer -> WorldHelper.compareLocations(tracer.getLocation(), e.getClickedBlock().getLocation()))) {
				e.setCancelled(true);
			}
		}
		if(state.isInGame() && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
			Location l = e.getClickedBlock().getLocation();
			for(int y = 0; y <= 1; y++) {
				convert(l.clone().add(2, y, 2));
				convert(l.clone().add(-2, y, 2));
				convert(l.clone().add(2, y, -2));
				convert(l.clone().add(-2, y, -2));
				for(int i = -1; i <= 1; i++) {
					convert(l.clone().add(i, y, 2));
					convert(l.clone().add(i, y, -2));
					convert(l.clone().add(2, y, i));
					convert(l.clone().add(-2, y, i));
				}
			}
		}
	}

	private void convert(Location l) {
		if(l.getBlock().getType() == Material.QUARTZ_BLOCK) {
			ParticleUtils.createParticlesOutline(l.getBlock(), Particle.FLAME, null, 5);
			l.getWorld().playSound(l, Sound.BLOCK_WOOD_BREAK, 0.1F, 1F);
			l.getBlock().setType(Material.BOOKSHELF);
		}
	}

	@EventHandler
	public void inventory(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if((state == GameState.VOTE || state == GameState.PREPARING) && isPlaying(p)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void tntArena(EntityExplodeEvent e) {
		if(e.getEntityType() == EntityType.PRIMED_TNT && (state == GameState.DEATHMATCH || state == GameState.ENDING)) {
			e.blockList().clear();
		}
	}

	@EventHandler
	public void noFoodLoss(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		if(isInLobby(p) && e.getFoodLevel() < 20) {
			p.setFoodLevel(20);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void move(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(state.isPreGame() && isPlaying(p) && !WorldHelper.compareLocations(e.getFrom(), e.getTo())) {
			e.setCancelled(true);
		}
		if(isSpectator(p)) {
			if(e.getTo().getY() <= 0) {
				e.setCancelled(true);
			}
		}
		if(isInLobby(p)) {
			if(e.getTo().getY() <= 0) {
				p.teleport(WorldManager.getLobby().getSpawnLocation());
			}
			if(p.getFireTicks() > 0 && !PvpArena.isOnArena(p)) {
				teleportToParkour(p);
			}
		}
	}

}
