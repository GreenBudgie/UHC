package ru.greenbudgie.UHC;

import com.google.common.collect.Lists;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import ru.greenbudgie.artifact.ArtifactManager;
import ru.greenbudgie.block.CustomBlockManager;
import ru.greenbudgie.classes.ClassManager;
import ru.greenbudgie.classes.UHCClass;
import ru.greenbudgie.configuration.FastStart;
import ru.greenbudgie.configuration.GameDuration;
import ru.greenbudgie.configuration.GameType;
import ru.greenbudgie.configuration.MapSize;
import ru.greenbudgie.drop.Drop;
import ru.greenbudgie.drop.Drops;
import ru.greenbudgie.event.*;
import ru.greenbudgie.items.BlockHolder;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.lobby.Lobby;
import ru.greenbudgie.lobby.LobbyTeamBuilder;
import ru.greenbudgie.lobby.sign.SignManager;
import ru.greenbudgie.mutator.Mutator;
import ru.greenbudgie.mutator.MutatorManager;
import ru.greenbudgie.rating.GameSummary;
import ru.greenbudgie.rating.Rating;
import ru.greenbudgie.requester.ItemRequester;
import ru.greenbudgie.requester.RequestedItem;
import ru.greenbudgie.util.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.bukkit.ChatColor.*;

public class UHC implements Listener {

	public static boolean playing = false;
	public static GameState state = GameState.STOPPED;
	public static Map<Player, Boolean> voteResults = new HashMap<>();
	public static int voteTimer = 0;
	public static int preparingTimer = 0;
	public static int outbreakTimer = 0;
	public static int deathmatchTimer = 0;
	public static int arenaTimer = 0;
	public static boolean skip = false; //True skips the current GameState
	public static int endTimer = 0;
	//V 2.0
	public static boolean isDuo = false;
	public static MapSize mapSize = MapSize.DEFAULT;
	public static GameDuration gameDuration = GameDuration.DEFAULT;
	public static boolean generating = false;
	public static boolean isRatingGame = true;
	public static String timerInfo = "";
	public static FastStart fastStart = FastStart.DISABLED;
	private static int mutatorCount = 0;
	private static Mutator prevMutator = null;
	private static BossBar voteBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
	//V 3.0
	private static Region platformRegion = null;
	private static int scoreboardCurrentTeamIndex;
	private static int scoreboardTimeUntilNextTeam;
	private static final int scoreboardMaxTimeUntilNextTeam = 3;

	private static final String UHC_LOGO =
			RED + "" + BOLD + "U" +
			GOLD + BOLD + "H" +
			YELLOW + BOLD + "C";

	private static final int DEATHMATCH_NO_PVP_DURATION = 15; //15 seconds
	public static final int DEATHMATCH_START_TIMER = 8 * 60; //8 minutes
	private static final int DEATHMATCH_TIME_UNTIL_ZONE_SHRINK = 3 * 60; //3 minutes
	private static final int DEATHMATCH_ZONE_SHRINK_DURATION = 2 * 60; //2 minutes

	private static final int PREPARING_MAX_TIME_WITH_MUTATORS = 35;
	private static final int PREPARING_MAX_TIME_NO_MUTATORS = 15;
	private static int preparingMaxTime;

	public static final double DEFAULT_MAX_PLAYER_HP = 26;

	public static void init() {
		Lobby.init();
		WorldManager.init();
		RecipeHandler.init();
		MutatorManager.init();
		SignManager.init();
		for(Player p : Bukkit.getOnlinePlayers()) {
			createLobbyScoreboard(p);
		}
	}

	public static String getUHCLogo() {
		return UHC_LOGO;
	}

	public static void createGameScoreboard(Player p) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

		Team spectatorTeam = board.registerNewTeam("SpectatorTeam");
		spectatorTeam.setColor(WHITE);
		PlayerManager.getSpectators().forEach(pl -> spectatorTeam.addEntry(pl.getName()));

		Team lobbyTeam = board.registerNewTeam("LobbyTeam");
		lobbyTeam.setColor(GOLD);
		Lobby.getPlayersInLobbyAndArenas().forEach(pl -> lobbyTeam.addEntry(pl.getName()));

		Team playerTeam = board.registerNewTeam("PlayerTeam");
		playerTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		playerTeam.setCanSeeFriendlyInvisibles(false);
		playerTeam.setColor(AQUA);
		PlayerManager.getAliveOnlinePlayers().forEach(pl -> playerTeam.addEntry(pl.getName()));

		p.setScoreboard(board);
		updateGameScoreboard(p);
	}

	private static String getPrefix(UHCPlayer currentPlayer, Player scoreboardOwner) {
		String prefix = GOLD + "";
		if(!currentPlayer.isAlive()) prefix = DARK_RED + "" + STRIKETHROUGH; else
			if(!currentPlayer.isOnline()) prefix = GRAY + ""; else
				if(currentPlayer.compare(scoreboardOwner)) prefix = GREEN + "";
		return prefix;
	}

	public static void updateGameScoreboard(Player player) {
		Scoreboard board = player.getScoreboard();
		Objective gameInfo = board.getObjective("gameInfo");
		if(gameInfo != null) gameInfo.unregister();
		gameInfo = board.registerNewObjective("gameInfo", "dummy", getUHCLogo());
		gameInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
		int c = 0;

		if(!state.isPreGame()) {
			if(isDuo) {
				List<PlayerTeam> aliveTeams = PlayerManager.getAliveTeams();
				if(!aliveTeams.isEmpty()) {
					if (scoreboardCurrentTeamIndex >= aliveTeams.size()) {
						scoreboardCurrentTeamIndex = 0;
					}
					PlayerTeam currentTeam = aliveTeams.get(scoreboardCurrentTeamIndex);
					String prefix = DARK_GRAY + "[" +
							WHITE + "" + BOLD + (scoreboardCurrentTeamIndex + 1) +
							DARK_GRAY + "] ";
					UHCPlayer player1 = currentTeam.getPlayer1();
					String player1Prefix = getPrefix(player1, player);

					UHCPlayer player2 = currentTeam.getPlayer2();
					String teamInfo1 = prefix + player1Prefix + player1.getNickname();
					String teamInfo2;
					if(player2 != null) {
						String player2Prefix = getPrefix(player2, player);
						teamInfo2 = prefix + player2Prefix + player2.getNickname();
					} else {
						teamInfo2 = prefix + GRAY + "Нет тиммейта";
					}
					Score teamScore2 = gameInfo.getScore(teamInfo2);
					teamScore2.setScore(c++);
					Score teamScore1 = gameInfo.getScore(teamInfo1);
					teamScore1.setScore(c++);
				}
			}

			int aliveTeamsNumber = PlayerManager.getAliveTeams().size();
			String teamCountInfo = GRAY + "В живых " +
					WHITE + BOLD + aliveTeamsNumber +
					GRAY + " ";
			if(isDuo) {
				teamCountInfo += new NumericalCases("команда", "команды", "команд").byNumber(aliveTeamsNumber);
			} else {
				teamCountInfo += new NumericalCases("игрок", "игрока", "игроков").byNumber(aliveTeamsNumber);
			}
			Score teamCountScore = gameInfo.getScore(teamCountInfo);
			teamCountScore.setScore(c++);
		}

		if(state.isBeforeDeathmatch()) {
			final boolean show = state == GameState.OUTBREAK;
			Location playerLocation = player.getLocation();
			for(Drop drop : new Drop[] {Drops.NETHERDROP, Drops.CAVEDROP, Drops.AIRDROP}) {
				if(drop.getTimer() <= deathmatchTimer || show) {
					Score locationScore = gameInfo.getScore(
							DARK_GRAY + "- " + drop.getCoordinatesInfo(playerLocation)
					);
					locationScore.setScore(c++);
					Score textScore = gameInfo.getScore(
								drop.getName() +
									DARK_GRAY + " (" +
									AQUA + MathUtils.formatTime(drop.getTimer()) +
									DARK_GRAY + "):");
					textScore.setScore(c++);
				}
			}
			Score playerLocationScore = gameInfo.getScore(
					GRAY + "Ты: " +
					GRAY + BOLD + playerLocation.getBlockX() +
					WHITE + ", " +
					GRAY + BOLD + playerLocation.getBlockY() +
					WHITE + ", " +
					GRAY + BOLD + playerLocation.getBlockZ());
			playerLocationScore.setScore(c++);
		}
		if(!timerInfo.isEmpty()) {
			Score timer = gameInfo.getScore(timerInfo);
			timer.setScore(c);
		}

		registerHpInfo(player, "hpInfoList", board, DisplaySlot.PLAYER_LIST);
		registerHpInfo(player, "hpInfoName", board, DisplaySlot.BELOW_NAME);
	}

	private static void registerHpInfo(Player playerToShowInfo, String name, Scoreboard board, DisplaySlot slot) {
		Objective hpInfo = board.getObjective(name);
		if(hpInfo != null) hpInfo.unregister();
		hpInfo = board.registerNewObjective(name, Criteria.HEALTH, RED + "❤");
		for(Player player : PlayerManager.getAliveOnlinePlayers()) {
			Score hp = hpInfo.getScore(player.getName());
			hp.setScore((int) player.getHealth());
		}
		if(PlayerManager.isSpectator(playerToShowInfo) || MutatorManager.healthDisplay.isActive()) {
			hpInfo.setDisplaySlot(slot);
		} else {
			hpInfo.setDisplaySlot(null);
		}
	}

	private static String[] trimTeam(String playerName1, String playerName2, int ln, final int maxLength) {
		boolean change1 = false, change2 = false;
		while(ln >= maxLength - (change1 ? 1 : 0) - (change2 ? 1 : 0)) {
			if(playerName1.length() > playerName2.length()) {
				playerName1 = playerName1.substring(0, playerName1.length() - 1);
				change1 = true;
			} else {
				playerName2 = playerName2.substring(0, playerName2.length() - 1);
				change2 = true;
			}
			ln--;
		}
		if(change1) playerName1 += ".";
		if(change2) playerName2 += ".";
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
		Lobby.getPlayersInLobbyAndArenas().forEach(UHC::createLobbyScoreboard);
	}

	public static void refreshGameScoreboard() {
		PlayerManager.getInGamePlayersAndSpectators().forEach(UHC::createGameScoreboard);
	}

	public static void createLobbyScoreboard(Player p) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

		Team spectatorTeam = board.registerNewTeam("SpectatorTeam");
		spectatorTeam.setColor(WHITE);
		PlayerManager.getSpectators().forEach(pl -> spectatorTeam.addEntry(pl.getName()));

		Team lobbyTeam = board.registerNewTeam("LobbyTeam");
		lobbyTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		lobbyTeam.setCanSeeFriendlyInvisibles(false);
		lobbyTeam.setColor(GOLD);
		Lobby.getPlayersInLobbyAndArenas().forEach(pl -> lobbyTeam.addEntry(pl.getName()));

		Team playerTeam = board.registerNewTeam("PlayerTeam");
		playerTeam.setColor(AQUA);
		PlayerManager.getAliveOnlinePlayers().forEach(pl -> playerTeam.addEntry(pl.getName()));

		p.setScoreboard(board);
		updateLobbyScoreboard(p);
	}

	public static void updateLobbyScoreboard(Player player) {
		Scoreboard board = player.getScoreboard();
		Objective teamInfo = board.getObjective("teamInfo");
		if(teamInfo != null) teamInfo.unregister();
		teamInfo = board.registerNewObjective("teamInfo", "dummy", getUHCLogo());
		teamInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
		int c = 0;
		if(isDuo) {
			List<Player> registered = new ArrayList<>();
			for(Player currentPlayer : Lobby.getPlayersInLobbyAndArenas()) {
				Player currentTeammate = LobbyTeamBuilder.getTeammate(currentPlayer);
				String s;
				String currentPlayerName = (currentPlayer == player ? GREEN : GOLD) + currentPlayer.getName();
				if(currentTeammate != null) {
					if(registered.contains(currentTeammate)) continue;
					String currentTeammateName = ((currentTeammate == player) ? GREEN : GOLD) + currentTeammate.getName();
					registered.add(currentTeammate);
					String finalString = DARK_GRAY + "- " + currentPlayerName + WHITE + " / " + currentTeammateName;
					String[] trimmed = trimTeam(currentPlayerName, currentTeammateName, finalString.length(), 40);
					currentPlayerName = trimmed[0];
					currentTeammateName = trimmed[1];
					s = DARK_GRAY + "- " + currentPlayerName + WHITE + " / " + currentTeammateName;
				} else {
					s = DARK_GRAY + "- " + currentPlayerName;
				}
				Score team = teamInfo.getScore(s);
				team.setScore(c);
				c++;
				registered.add(currentPlayer);
			}
			int teamNumber = LobbyTeamBuilder.getTeamNumber();
			String teamNumberText1 = new NumericalCases(
					"Собрана",
					"Собраны",
					"Собрано").
					byNumber(teamNumber);
			String teamNumberText2 = new NumericalCases(
					"команда",
					"команды",
					"команд").
					byNumber(teamNumber);
			Score teamNumberInfo = teamInfo.getScore(
					GRAY + teamNumberText1 + " " +
							DARK_AQUA + BOLD + teamNumber + " " +
							GRAY + teamNumberText2 +
							DARK_GRAY + ":");
			teamNumberInfo.setScore(c++);
		}

		UHCClass selectedClass = ClassManager.getClassInLobby(player);
		if(selectedClass != null) {
			Score classInfo = teamInfo.getScore(
						GRAY + "Класс" +
							DARK_GRAY + ": " +
							selectedClass.getName());
			classInfo.setScore(c);
		}
	}

	public static void endGame() {
		if(playing) {
			playing = false;
			Bukkit.getPluginManager().callEvent(new BeforeGameEndEvent());

			if(Rating.getCurrentGameSummary().isWorthSaving()) {
				Rating.getCurrentGameSummary().calculateAndSetDuration();
				Rating.saveCurrentGameSummary();
			} else {
				Rating.dismissCurrentGameSummary();
			}

			for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
				inGamePlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
				resetPlayer(inGamePlayer);
				inGamePlayer.setGameMode(GameMode.ADVENTURE);
				inGamePlayer.teleport(Lobby.getLobby().getSpawnLocation());
			}
			for(UHCPlayer uhcPlayer : PlayerManager.getPlayers()) {
				if(uhcPlayer.getGhost() != null) uhcPlayer.getGhost().remove();
				uhcPlayer.removeTabPrefix();
			}
			CustomBlockManager.removeAllBlocks();
			voteBar.removeAll();
			voteBar.setVisible(false);
			PlayerManager.clear();
			ArtifactManager.resetPrices();
			ItemRequester.requestedItems.forEach(RequestedItem::deleteStands);
			ItemRequester.requestedItems.clear();
			MutatorManager.deactivateMutators();
			mapSize = MapSize.DEFAULT;
			gameDuration = GameDuration.DEFAULT;
			isRatingGame = true;
			clearPlatformRegion();
			platformRegion = null;
			if(!WorldManager.keepMap && !state.isPreGame()) WorldManager.removeMap();
			state = GameState.STOPPED;
			SignManager.updateTextOnSigns();
			refreshLobbyScoreboard();
			Bukkit.getPluginManager().callEvent(new AfterGameEndEvent());
		} else {
			Bukkit.broadcastMessage(RED + "Игра не идет");
		}
	}

	public static void startGame() {
		if(!playing) {
			if(!WorldManager.hasMap()) {
				Bukkit.broadcastMessage(RED + "Карта не сгенерирована!");
				return;
			}
			Bukkit.getPluginManager().callEvent(new BeforeGameInitializeEvent());
			if(GameType.getType().allowsMutators()) {
				mutatorCount = MathUtils.chance(30) ? 4 : (MathUtils.chance(65) ? 3 : 2);
			} else {
				mutatorCount = 0;
			}
			voteResults.clear();
			for(Player player : Bukkit.getOnlinePlayers()) {
				InventoryHelper.sendActionBarMessage(player,
						GOLD + "" + BOLD + "Игра" +
							RED + BOLD + " начинается" +
							GRAY + "!");
			}
			ArenaManager.resetArenaBorder(ArenaManager.getCurrentArena());
			WorldManager.updateBorder();
			World map = WorldManager.getGameMap();
			map.setPVP(false);
			map.setTime(0);
			map.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
			map.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
			WorldManager.getGameMapNether().setPVP(false);
			Drops.firstSetup();

			GameSummary summary = Rating.setupCurrentGameSummary();
			summary.setRatingGame(isRatingGame);
			summary.setDuo(isDuo);
			summary.setType(GameType.getType());

			for(Player player : Bukkit.getOnlinePlayers()) {
				PlayerManager.registerPlayer(player);
				player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(DEFAULT_MAX_PLAYER_HP);
				resetPlayer(player);
				player.setNoDamageTicks(600);
				player.setGameMode(GameMode.ADVENTURE);
				player.getInventory().setItem(3, InventoryHelper.generateItemWithName(Material.LIME_DYE,
						GREEN + "Карта " + DARK_GREEN + BOLD + "Норм"));
				player.getInventory().setItem(5, InventoryHelper.generateItemWithName(Material.RED_DYE,
						RED + "Карта " + DARK_RED + BOLD + "Говно"));
			}
			double radius = PlayerManager.getPlayers().size();
			double radsPerPlayer = 2 * Math.PI / PlayerManager.getPlayers().size();
			int spawnHeight = WorldManager.spawnLocation.getWorld().getHighestBlockYAt(WorldManager.spawnLocation) + 16;
			List<Location> spawnLocations = new ArrayList<>();
			for(int i = 0; i < PlayerManager.getPlayers().size(); i++) {
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

			Location platformCenter = WorldManager.spawnLocation.clone();
			platformCenter.setY(spawnHeight);
			createPlatform(platformCenter, radius + 2);

			for(int i = 0; i < PlayerManager.getPlayers().size(); i++) {
				Player p = PlayerManager.getPlayers().get(i).getPlayer();
				Location l = spawnLocations.get(i);
				Location l2 = WorldManager.spawnLocation.clone();
				double xLength = l2.getX() - l.getX();
				double zLength = l2.getZ() - l.getZ();
				double yaw = -Math.atan2(xLength, zLength);
				Location tpLoc = l.clone().add(0.5, 1, 0.5);
				tpLoc.setYaw((float) Math.toDegrees(yaw));
				p.teleport(tpLoc);
			}

			scoreboardCurrentTeamIndex = 0;
			scoreboardTimeUntilNextTeam = scoreboardMaxTimeUntilNextTeam;
			refreshScoreboards();

			if(UHC.fastStart == FastStart.DISABLED) {
				for(UHCPlayer uplayer : PlayerManager.getPlayers()) {
					voteBar.addPlayer(uplayer.getPlayer());
				}
				voteBar.setVisible(true);
				updateVoteBar();
				state = GameState.VOTE;
				voteTimer = 20;
			} else {
				state = GameState.PREPARING;
				preparingTimer = 1;
			}
			playing = true;
			ArenaManager.getCurrentArena().getWorld().setPVP(false);
			SignManager.updateTextOnSigns();

			Bukkit.getPluginManager().callEvent(new AfterGameInitializeEvent());
		} else {
			Bukkit.broadcastMessage(RED + "Игра уже идет");
		}
	}

	public static void createPlatform(Location center, double radius) {
		radius += 0.5;

		platformRegion = new Region(center.clone().add(-radius, 0, -radius), center.clone().add(radius, 4, radius));
		clearPlatformRegion();

		double invRadius = 1 / radius;
		int ceilRadius = (int) Math.ceil(radius);

		double nextXn = 0;
		for(int x = 0; x <= ceilRadius; ++x) {
			final double xn = nextXn;
			nextXn = (x + 1) * invRadius;
			double nextZn = 0;
			for(int z = 0; z <= ceilRadius; ++z) {
				final double zn = nextZn;
				nextZn = (z + 1) * invRadius;
				double distanceSq = (xn * xn) + (zn * zn);
				if(distanceSq > 1) {
					continue;
				}
				double xBorder = (nextXn * nextXn) + (zn * zn);
				double zBorder = (xn * xn) + (nextZn * nextZn);
				int y = 0;
				Material block = Material.GLASS;
				if(xBorder > 1 || zBorder > 1 ||
						(radius > 6 && (xBorder < 0.2 || zBorder < 0.2))) {
					y = 2;
					block = Material.BARRIER;
				}

				center.clone().add(x, y, z).getBlock().setType(block);
				center.clone().add(x, y, -z).getBlock().setType(block);
				center.clone().add(-x, y, z).getBlock().setType(block);
				center.clone().add(-x, y, -z).getBlock().setType(block);

				if(y == 0) {
					center.clone().add(x, 4, z).getBlock().setType(Material.BARRIER);
					center.clone().add(x, 4, -z).getBlock().setType(Material.BARRIER);
					center.clone().add(-x, 4, z).getBlock().setType(Material.BARRIER);
					center.clone().add(-x, 4, -z).getBlock().setType(Material.BARRIER);
				}
			}
		}
	}

	private static void clearPlatformRegion() {
		if(platformRegion != null) {
			for(Block block : platformRegion.getBlocksInside()) {
				block.setType(Material.AIR);
			}
		}
	}

	public static int getNoPVPDuration() {
		return gameDuration.getNoPvpDurationMinutes();
	}

	public static int getGameDuration() {
		return gameDuration.getGameDurationMinutes();
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
			if(GameType.getType().allowsMutators()) {
				preparingTimer = PREPARING_MAX_TIME_WITH_MUTATORS;
			} else {
				preparingTimer = PREPARING_MAX_TIME_NO_MUTATORS;
			}
			preparingMaxTime = preparingTimer;
			for(Player p : PlayerManager.getAliveOnlinePlayers()) {
				p.sendTitle(GREEN + "Карта " + DARK_GREEN + BOLD + "Норм",
						DARK_AQUA + "" + BOLD + preparingTimer +
								GOLD + " секунд до начала",
						10, 60, 30);
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
				p.getInventory().clear();
			}
			state = GameState.PREPARING;
		} else {
			for(Player p : PlayerManager.getAliveOnlinePlayers()) {
				p.sendTitle(RED + "Карта " + DARK_RED + BOLD + "Говно" + GRAY + "!",
						GOLD + "Большинство проголосовало против", 10, 60, 30);
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1F, 0.5F);
				p.getInventory().clear();
				p.setGameMode(GameMode.SPECTATOR);
			}
			state = GameState.ENDING;
			endTimer = 8;
		}
	}

	private static void mutatorInvPreEffect(int n, boolean active) {
		int slot = 1 + (n * 2);
		if(active) {
			for(Player p : PlayerManager.getAliveOnlinePlayers()) {
				if(n < mutatorCount) {
					ChatColor color = DARK_AQUA;
					if(n + 1 == 3)color = RED;
					if(n + 1 == 4) color = DARK_PURPLE;
					p.sendTitle(GOLD + "Выбираем мутаторы", AQUA + "Количество" +
							GRAY + ": " + color + BOLD + (n + 1), 0, 200, 0);
					p.getInventory().setItem(slot, ItemUtils.builder(Material.REDSTONE).withName(" ").build());
					p.playSound(p.getLocation(), Sound.ITEM_FIRECHARGE_USE, 0.3F, 1 + 0.1F * n);
				} else {
					p.getInventory().setItem(slot, ItemUtils.builder(Material.GRAY_DYE).withName(" ").build());
					p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.3F, 1.2F);
				}
			}
		} else {
			for(Player p : PlayerManager.getAliveOnlinePlayers()) {
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
		for(Player p : PlayerManager.getAliveOnlinePlayers()) {
			Mutator mutator = MutatorManager.getRandomMutatorExcept(prevMutator != null ? Lists.newArrayList(prevMutator) : Lists.newArrayList());
			prevMutator = mutator;
			p.getInventory()
					.setItem(slot, ItemUtils.builder(mutator.getItemToShow()).withName(DARK_PURPLE + "" + BOLD + mutator.getName()).build());
			p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 0.3F, 1 + pitch);
			p.sendTitle(GOLD + "Выбираем мутаторы", AQUA + order + " мутатор" + GRAY + ": " +
					DARK_GRAY + mutator.getName(), 0, 200, 0);
		}
	}

	private static void mutatorInvSelect(int n) {
		String order = getOrderByN(n + 1);
		int slot = 1 + (n * 2);
		Mutator mutator = MutatorManager.activateRandomMutator(true, true);
		for(Player p : PlayerManager.getAliveOnlinePlayers()) {
			if(mutator.isHidden()) {
				p.getInventory().setItem(slot, ItemUtils.builder(Material.NAME_TAG).withName(DARK_RED + "" + BOLD + "Скрытый Мутатор").build());
				p.sendTitle(GOLD + "Выбираем мутаторы",
						AQUA + order + " мутатор" + GRAY + ": " + DARK_RED + BOLD + "Скрытый Мутатор", 0, 200, 0);
				p.playSound(p.getLocation(), Sound.BLOCK_BELL_USE, 0.3F, 0.8F);
			} else {
				p.getInventory()
						.setItem(slot, ItemUtils.builder(mutator.getItemToShow()).withName(DARK_PURPLE + "" + BOLD + mutator.getName()).build());
				p.sendTitle(GOLD + "Выбираем мутаторы",
						AQUA + order + " мутатор" + GRAY + ": " +
								DARK_PURPLE + "" + BOLD + mutator.getName(), 0, 200, 0);
				p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3F, 1F + 0.2F * n);
			}
		}
	}

	private static void mutatorInvEnd() {
		for(Player p : PlayerManager.getAliveOnlinePlayers()) {
			p.sendMessage(	DARK_GRAY + "" + BOLD + "< " + RESET + MutatorManager.getMessageFromCurrentMutators() +
							RESET + DARK_GRAY + "" + BOLD + " >");
			p.sendTitle(GREEN + "Выбор закончен!", "", 0, 60, 10);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
		}
	}

	public static void tickGame() {
		if(!playing) return;
		if(state == GameState.VOTE) {
			if(TaskManager.isSecUpdated()) {
				voteTimer--;
				if(voteTimer <= 0 || skip) {
					skip = false;
					endVote();
				}
				timerInfo = GOLD + "Голосование" + GRAY + ": " + AQUA + BOLD + voteTimer;
			}
		}
		if(state == GameState.PREPARING) {
			if(TaskManager.isSecUpdated()) {
				preparingTimer--;
				if(preparingTimer == preparingMaxTime - 5) {
					for(Player p : PlayerManager.getAliveOnlinePlayers()) {
						p.sendTitle(
								getUHCLogo(),
								GOLD + "" + BOLD + "Битва Инвалидов",
								5,
								100,
								20
						);
						p.sendMessage("");
						p.sendMessage(
								GRAY + "-------- " + getUHCLogo() + DARK_GRAY + " aka " + GOLD + "Битва Инвалидов" + GRAY + " --------",
								"",
								DARK_GRAY + "" + BOLD + "1 " + RESET + GRAY + "Цель - остаться в живых",
								DARK_GRAY + "" + BOLD + "2 " + RESET + GRAY + "Здоровье не восстанавливается самостоятельно",
								DARK_GRAY + "" + BOLD + "3 " + RESET + GRAY + "Пополняй здоровье золотыми яболоками, супами из ромашек, зельями...",
								DARK_GRAY + "" + BOLD + "4 " + RESET + GRAY + "Приходи на координаты дропов, которые отображаются справа - там очень полезные вещи!",
								DARK_GRAY + "" + BOLD + "5 " + RESET + GRAY + "Делай запросы имбовых предметов, кликая ПКМ с редстоуном в руке",
								DARK_GRAY + "" + BOLD + "6 " + RESET + GRAY + "Ухудшай всем игру, призывая силы артефактов, которые выпадают с враждебных мобов",
								"",
								"----------------------------------------"
						);
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.7F);
					}
				}
				if(preparingTimer <= 3 && preparingTimer > 0) {
					ChatColor c = switch (preparingTimer) {
						case 3 -> RED;
						case 2 -> GOLD;
						case 1 -> YELLOW;
						default -> DARK_AQUA;
					};
					for(Player p : PlayerManager.getAliveOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 0.5F, 0.9F + (float) (preparingTimer * 0.1));
						p.sendTitle(c + "" + BOLD + preparingTimer, "", 0, 100, 0);
					}
				}
				if(preparingTimer <= 0 || skip) {
					skip = false;
					endPreparing();
				}
				timerInfo = GOLD + "Подготовка" + GRAY + ": " + AQUA + BOLD + preparingTimer;
			}
			if(mutatorCount > 0) {
				if(preparingTimer == 26) {
					if(TaskManager.tick == 0) {
						for(Player p : PlayerManager.getAliveOnlinePlayers()) {
							p.sendTitle(GOLD + "Выбираем мутаторы", AQUA + "Количество" + GRAY + ": " +
											DARK_AQUA + BOLD + 0,
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
		}
		if(state.isBeforeDeathmatch() || state == GameState.DEATHMATCH) {
			MutatorManager.updateMutators();
		}
		if(state == GameState.OUTBREAK) {
			if(TaskManager.isSecUpdated()) {
				outbreakTimer--;
				timerInfo = GOLD + "До ПВП" + GRAY + ": " + AQUA + BOLD + MathUtils.formatTime(outbreakTimer);
				if(outbreakTimer == 0 || skip) {
					skip = false;
					enablePvpAndSwitchState();
				}
			}
		}
		if(state == GameState.GAME) {
			if(TaskManager.isSecUpdated()) {
				deathmatchTimer--;
				timerInfo = GOLD + "До дезматча" + GRAY + ": " + AQUA + BOLD + MathUtils.formatTime(deathmatchTimer);
				if(deathmatchTimer == 0 || skip) {
					skip = false;
					state = GameState.DEATHMATCH;
					arenaTimer = DEATHMATCH_START_TIMER;
					for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
						inGamePlayer.teleport(ArenaManager.getCurrentArena().getWorld().getSpawnLocation());
						inGamePlayer.sendTitle(DARK_RED + "" + BOLD + "Дезматч" + GRAY + "!",
								DARK_AQUA + "" + BOLD + DEATHMATCH_NO_PVP_DURATION +
										RESET + GOLD + " секунд до " +
										DARK_RED + BOLD + "ПВП" +
										GRAY + "!",
								10, 50, 30);
					}
					for(UHCPlayer offlinePlayer : PlayerManager.getAlivePlayers()) {
						if(!offlinePlayer.isOnline()) {
							offlinePlayer.teleport(ArenaManager.getCurrentArena().getWorld().getSpawnLocation());
						}
					}
				}
			}
		}
		if(state == GameState.DEATHMATCH) {
			if(TaskManager.isSecUpdated()) {
				arenaTimer--;
				if(arenaTimer == 0 || skip) {
					skip = false;
					draw();
				}
				int timeUntilShrink = DEATHMATCH_TIME_UNTIL_ZONE_SHRINK - (DEATHMATCH_START_TIMER - arenaTimer);
				//Start border scaling
				if(timeUntilShrink == 0) {
					ArenaManager.Arena arena = ArenaManager.getCurrentArena();
					WorldBorder arenaBorder = arena.getWorld().getWorldBorder();
					arenaBorder.setSize(arena.getMinBorderSize(), DEATHMATCH_ZONE_SHRINK_DURATION);
					for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
						inGamePlayer.playSound(inGamePlayer.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 0.8f);
						inGamePlayer.sendTitle(" ", DARK_RED + "" + BOLD + ">>> " +
								RED + "Арена сужается" + GRAY + "!" +
								DARK_RED + BOLD + " <<<",
								10, 25, 15);
					}
				}
				int timeUntilPvp = DEATHMATCH_NO_PVP_DURATION - (DEATHMATCH_START_TIMER - arenaTimer);
				if(timeUntilPvp == 0) {
					ArenaManager.getCurrentArena().getWorld().setPVP(true);
					for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
						p.sendTitle(" ", DARK_RED + "" + BOLD + ">>> " + GOLD + BOLD + "ПВП" + RED +
								BOLD + " Включено!" + DARK_RED + BOLD + " <<<", 0, 35, 15);
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
					}
				}
				if(timeUntilPvp <= 3 && timeUntilPvp > 0) {
					for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
						p.sendTitle(" ", DARK_GRAY + "" + BOLD + timeUntilPvp, 0, 40, 0);
						p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 0.8F, 0.5F + timeUntilPvp / 3F);
					}
				}
				if(timeUntilPvp > 0) {
					timerInfo = RED + "" + BOLD + "До" + DARK_RED + BOLD + " ПВП" + GRAY + ": " +
							DARK_AQUA + BOLD + timeUntilPvp;
				} else if(timeUntilShrink > 0) {
					timerInfo = DARK_RED + "" + BOLD + "Сужение арены" + GRAY + ": " +
							DARK_AQUA + BOLD + MathUtils.formatTime(timeUntilShrink);
				} else {
					timerInfo = AQUA + "" + BOLD + "Ничья через:" + GRAY + ": " +
							DARK_AQUA + BOLD + MathUtils.formatTime(arenaTimer);
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
			for(UHCPlayer uplayer : PlayerManager.getPlayers()) {
				uplayer.update();
			}
			CustomBlockManager.updateBlocks();
		}
		if(playing && state == GameState.GAME || state == GameState.OUTBREAK) {
			Drops.update();
		}
		if(TaskManager.ticksPassed(4) && playing) {
			for(Player onlinePlayer : PlayerManager.getAliveOnlinePlayers()) {
				UHCPlayer teammate = PlayerManager.getUHCTeammate(onlinePlayer);
				if(teammate != null && teammate.isAlive()) {
					Location teammateLocation = teammate.getLocation();
					if(teammateLocation == null) continue;
					String separator = GRAY + " | ";
					String locationInfo = LocationFormatter.formatToWithDistance(
							onlinePlayer.getLocation(),
							teammateLocation,
							DARK_AQUA,
							GRAY,
							AQUA,
							DARK_GRAY,
							true
					);
					String teammateInfo =
							GOLD + teammate.getNickname() + separator +
							RED + ((int) Math.round(teammate.getRealOrOfflineHealth())) +
							DARK_RED + " ❤" + separator +
							locationInfo;
					if(onlinePlayer.getWorld() == teammateLocation.getWorld())
						teammateInfo += AQUA + " " + LocationFormatter.getArrowPointingTo(onlinePlayer.getLocation(), teammateLocation);
					InventoryHelper.sendActionBarMessage(onlinePlayer, teammateInfo);
				}
			}
			for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
				updateGameScoreboard(inGamePlayer);
			}
		}
		if(TaskManager.isSecUpdated() && playing) {
			List<PlayerTeam> aliveTeams = PlayerManager.getAliveTeams();
			if(aliveTeams.size() > 0) {
				if(scoreboardCurrentTeamIndex >= aliveTeams.size()) scoreboardCurrentTeamIndex = 0;
				scoreboardTimeUntilNextTeam--;
				if(scoreboardTimeUntilNextTeam <= 0) {
					scoreboardCurrentTeamIndex++;
					if(scoreboardCurrentTeamIndex >= aliveTeams.size()) scoreboardCurrentTeamIndex = 0;
					scoreboardTimeUntilNextTeam = scoreboardMaxTimeUntilNextTeam;
				}
			}

			for(Player currentPlayer : PlayerManager.getAliveOnlinePlayers()) {
				ItemStack compassMainHand = currentPlayer.getInventory().getItemInMainHand();
				ItemStack compassOffHand = currentPlayer.getInventory().getItemInOffHand();
				if(CustomItems.tracker.isEquals(compassMainHand) || CustomItems.tracker.isEquals(compassOffHand)) {
					List<Player> list =
							PlayerManager.getAliveOnlinePlayers().stream()
							.filter(anotherPlayer -> anotherPlayer.getWorld() == currentPlayer.getWorld()
									&& anotherPlayer != currentPlayer
									&& (!PlayerManager.isTeammates(anotherPlayer, currentPlayer)))
							.toList();
					double dist = Double.MAX_VALUE;
					Player nearest = null;
					for(Player anotherPlayer : list) {
						double d = anotherPlayer.getLocation().distance(currentPlayer.getLocation());
						if(d < dist) {
							dist = d;
							nearest = anotherPlayer;
						}
					}
					if(nearest != null) {
						currentPlayer.setCompassTarget(nearest.getLocation());
					}
				} else {
					Location compassLocation = Optional
							.ofNullable(currentPlayer.getBedSpawnLocation())
							.orElse(currentPlayer.getWorld().getSpawnLocation());
					currentPlayer.setCompassTarget(compassLocation);
				}
			}
		}
	}

	public static void draw() {
		if(state != GameState.ENDING) {
			state = GameState.ENDING;
			endTimer = 8;
		}
		for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			inGamePlayer.setGameMode(GameMode.SPECTATOR);
			inGamePlayer.sendTitle(YELLOW + "Ничья", GOLD + "Видимо, игра затянулась", 10, 60, 20);
		}
		for(UHCPlayer uhcAlivePlayer : PlayerManager.getAlivePlayers()) {
			uhcAlivePlayer.getSummary().setWinningPlace(2);
		}
	}

	public static void tryWin() {
		List<PlayerTeam> aliveTeams = PlayerManager.getAliveTeams();
		if(aliveTeams.size() == 0) endGame();
		if(aliveTeams.size() <= 1 && state.isPreGame()) {
			endGame();
		}
		if(aliveTeams.size() == 1) {
			PlayerTeam team = aliveTeams.get(0);
			win(team);
		}
	}

	public static void win(PlayerTeam team) {
		if(state != GameState.ENDING) {
			state = GameState.ENDING;
			endTimer = 8;
		}
		UHCPlayer uplayer1 = team.getPlayer1();
		UHCPlayer uplayer2 = team.getPlayer2();
		if(uplayer2 != null) {
			String names = GOLD + uplayer1.getNickname() + YELLOW + " и " + GOLD + uplayer2.getNickname();
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(names + GREEN + " победили!");
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
			}
		} else {
			for(Player pl : Bukkit.getOnlinePlayers()) {
				pl.sendMessage(GOLD + uplayer1.getNickname() + GREEN + " победил!");
				pl.playSound(pl.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
			}
		}
		for(UHCPlayer uhcWinner : team.getPlayers()) {
			uhcWinner.getSummary().setWinningPlace(1);
			if(uhcWinner.isInGame()) {
				Player winner = uhcWinner.getPlayer();
				winner.setGameMode(GameMode.SPECTATOR);
				winner.sendTitle(YELLOW + "Ты победил!", "", 10, 40, 20);
				Firework firework = (Firework) winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK);
				FireworkMeta meta = firework.getFireworkMeta();
				meta.addEffect(FireworkEffect.builder().
						with(FireworkEffect.Type.BALL_LARGE).
						withColor(Color.GREEN).
						withFade(Color.YELLOW).
						withFlicker().build());
				firework.setFireworkMeta(meta);
			}
		}
		List<Mutator> hiddenMutators = MutatorManager.activeMutators.stream().filter(Mutator::isHidden).collect(Collectors.toList());
		if(hiddenMutators.size() > 0) {
			for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
				if(hiddenMutators.size() == 1) {
					inGamePlayer.sendMessage(YELLOW + "Скрытым мутатором был: " + LIGHT_PURPLE + hiddenMutators.get(0).getName());
				} else {
					inGamePlayer.sendMessage(YELLOW + "Скрытыми мутаторами были:");
					for(Mutator mutator : hiddenMutators) {
						inGamePlayer.sendMessage(DARK_GRAY + "- " + LIGHT_PURPLE + mutator.getName());
					}
				}
			}
		}
	}

	public static void recalculateTimeOnPlayerDeath() {
		int alivePlayers = PlayerManager.getAlivePlayers().size();
		boolean fewPlayers = alivePlayers <= 4 && alivePlayers > 1;
		if (!fewPlayers) {
			return;
		}
		if(state == GameState.OUTBREAK) {
			int minOutbreakTime = 60;
			if (outbreakTimer < minOutbreakTime) {
				return;
			}
			int outbreakTimeDecrease = 150;
			outbreakTimer = Math.max(outbreakTimer - outbreakTimeDecrease, minOutbreakTime);
			announceTimeChanged(alivePlayers);
			return;
		}
		if(state == GameState.GAME) {
			int minGameTime = 600;
			if (deathmatchTimer < minGameTime) {
				return;
			}
			int gameTimeDecrease = 300;
			deathmatchTimer = Math.max(deathmatchTimer - gameTimeDecrease, minGameTime);
			announceTimeChanged(alivePlayers);
		}
	}

	private static void announceTimeChanged(int alivePlayersNumber) {
		for(Player player : PlayerManager.getInGamePlayersAndSpectators()) {
			player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.5F, 1.2F);
			player.sendMessage(GRAY + "В живых осталось " + AQUA + BOLD + alivePlayersNumber +
					GRAY + " игрока. " + DARK_RED + BOLD + "Время сокращено!");
		}
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

	public static void endPreparing() {
		state = GameState.OUTBREAK;
		if(UHC.fastStart != FastStart.NO_MUTATORS) {
			if(MutatorManager.activeMutators.size() != mutatorCount) {
				int size = MutatorManager.activeMutators.size();
				for(int i = 0; i < mutatorCount - size; i++) {
					MutatorManager.activateRandomMutator(true, true);
				}
			}
		}
		Rating.getCurrentGameSummary().setStartMutators(Lists.newArrayList(MutatorManager.activeMutators));
		Rating.getCurrentGameSummary().makeWorthSaving();

		clearPlatformRegion();
		for(Player player : PlayerManager.getAliveOnlinePlayers()) {
			if(MutatorManager.hungerGames.isActive()) {
				player.sendTitle(RED + "" + BOLD + "Игра " + GOLD + BOLD + "началась!",
						YELLOW + "У тебя " + DARK_RED + BOLD + "ОДНА СУКА МИНУТА" +
								YELLOW + " на развитие без ПВП", 10, 60, 30);
			} else {
				player.sendTitle(RED + "" + BOLD + "Игра " + GOLD + BOLD + "началась!",
						YELLOW + "У тебя " + AQUA + BOLD + getNoPVPDuration() + YELLOW + " минут на развитие без ПВП",
						10, 60, 30);
			}
			if(ArenaManager.doAnnounceArena() || ArenaManager.getChosenArena() != null) {
				player.sendMessage(DARK_GRAY + "" + BOLD + "> " +
						GRAY + "Дезматч будет проходить на арене" +
						DARK_GRAY + ": " +
						DARK_GREEN + ArenaManager.getCurrentArena().getName() +
						DARK_GRAY + "" + BOLD + " <");
			}
			player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.4F, 1);
			player.getInventory().clear();
			player.getActivePotionEffects().forEach(ef -> player.removePotionEffect(ef.getType()));
			player.setGameMode(GameMode.SURVIVAL);
			heal(player);
			if(MutatorManager.lessHealth.isActive()) {
				player.setHealth(player.getHealth() / 2);
			}
			player.setNoDamageTicks(160);
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 160, 9, true, true, true));
		}
		Bukkit.getPluginManager().callEvent(new GameStartEvent());
		if(MutatorManager.hungerGames.isActive()) {
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
		updateVoteBar();
		if(voteResults.size() == PlayerManager.getPlayers().size()) {
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
		voteBar.setTitle(GOLD + "Голосование " + DARK_GRAY + "(" + GREEN + BOLD + votesFor + GRAY + " / " +
				RED + BOLD + votesAgainst + DARK_GRAY + ")");
		voteBar.setProgress(progress);
	}

	public static void heal(Player p) {
		p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
		p.setSaturation(20);
		p.setExhaustion(20);
		p.setFoodLevel(20);
	}

	public static String getDeathMessage(Player p, EntityDamageEvent.DamageCause cause) {
		String name = GOLD + p.getName() + RED + " ";
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

	public static ItemStack getBonusShell() {
		return ItemUtils.builder(Material.SHULKER_SHELL).withGlow().withName(LIGHT_PURPLE + "Сияющий панцирь")
				.withSplittedLore(GOLD + "Окружи его золотыми слитками и получи 2 золотых яблока").build();
	}

	private static void enablePvpAndSwitchState() {
		state = GameState.GAME;
		deathmatchTimer = 60 * getGameDuration();
		WorldManager.getGameMap().setPVP(true);
		WorldManager.getGameMapNether().setPVP(true);
		for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
			p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 0.5F);
			p.sendTitle(DARK_RED + "" + BOLD + "ПВП " + GOLD + BOLD + "Включено" + GRAY + "!",
					YELLOW + "До дезматча " + AQUA + BOLD + getGameDuration() +
							RESET + YELLOW + " минут", 10, 60, 30);
		}
	}

	@EventHandler
	public void damageByEntity(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player player && e.getDamager() instanceof Player damager) {
			if(PlayerManager.isTeammates(player, damager) && !Lobby.isInLobbyOrWatchingArena(player)) e.setCancelled(true);
		}
	}

	@EventHandler
	public void consume(PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		if(PlayerManager.isPlaying(player)) {
			ItemStack item = e.getItem();
			//Fixing overpowered golden apples
			if(item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
				TaskManager.invokeLater(() -> {

					PotionEffect regen = player.getPotionEffect(PotionEffectType.REGENERATION);
					if(regen != null) {
						player.addPotionEffect(new PotionEffect(regen.getType(), regen.getDuration(), regen.getAmplifier() / 2, regen.isAmbient(), regen.hasParticles(),
								regen.hasIcon()));
					}

					PotionEffect absorp = player.getPotionEffect(PotionEffectType.ABSORPTION);
					if(absorp != null) {
						player.addPotionEffect(
								new PotionEffect(absorp.getType(), absorp.getDuration() / 2, absorp.getAmplifier() / 2, absorp.isAmbient(), absorp.hasParticles(),
										absorp.hasIcon()));
					}

				});
			}
			//Fixing stews to regenerate only 0.5 hearts instead of 1.5 hearts
			if(item.getType() == Material.SUSPICIOUS_STEW) {
				SuspiciousStewMeta meta = (SuspiciousStewMeta) item.getItemMeta();
				if(meta.hasCustomEffect(PotionEffectType.REGENERATION)) {
					TaskManager.invokeLater(() -> {

						PotionEffect regen = player.getPotionEffect(PotionEffectType.REGENERATION);
						if(regen != null) {
							player.removePotionEffect(PotionEffectType.REGENERATION);
							player.addPotionEffect(new PotionEffect(regen.getType(), 75, regen.getAmplifier(), regen.isAmbient(),
									regen.hasParticles(), regen.hasIcon()));
						}

					});
				}
			}
		}
	}

	@EventHandler
	public void preJoin(AsyncPlayerPreLoginEvent e) {
		if(generating) {
			e.setKickMessage(GOLD + "Сейчас идет генерация мира, зайди немного позже");
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
	public void death(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		Player player = e.getEntity();
		UHCPlayer uplayer = PlayerManager.asUHCPlayer(player);
		if(uplayer != null) {
			uplayer.kill();
		}
	}

	@EventHandler
	public void pickup(EntityPickupItemEvent e) {
		if(e.getEntity() instanceof Player player) {
			Player teammate = PlayerManager.getTeammate(player);
			ItemStack item = e.getItem().getItemStack();
			if(teammate != null &&
					ItemUtils.hasCustomValue(item, "owner") &&
					ItemUtils.getCustomValue(item, "owner").equals(teammate.getName())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
		Player sender = e.getPlayer();
		String mes = e.getMessage();
		boolean local = mes.startsWith(".");
		if(local) mes = mes.substring(1);
		String suffix = GOLD + sender.getName() + WHITE + ": " + mes;
		String localPrefix = LIGHT_PURPLE + "<Локально> ";
		for(Player receiver : Bukkit.getOnlinePlayers()) {
			if(!local) {
				if(Lobby.isInLobbyOrWatchingArena(sender) && PlayerManager.isInGame(receiver)) {
					receiver.sendMessage(YELLOW + "<Лобби> " + suffix);
					continue;
				}
				if(Lobby.isInLobbyOrWatchingArena(receiver) && PlayerManager.isInGame(sender)) {
					receiver.sendMessage(AQUA + "<Игра> " + suffix);
					continue;
				}
				if(PlayerManager.isSpectator(sender)) {
					receiver.sendMessage(DARK_RED + "<Мертв> " + suffix);
					continue;
				}
				receiver.sendMessage(suffix);
			} else {
				if((Lobby.isInLobbyOrWatchingArena(sender) && Lobby.isInLobbyOrWatchingArena(receiver)) ||
						(PlayerManager.isSpectator(sender) && PlayerManager.isSpectator(receiver)) ||
						(!isDuo && PlayerManager.isPlaying(sender) && PlayerManager.isPlaying(receiver))) {
					receiver.sendMessage(localPrefix + suffix);
					continue;
				}
				if (!PlayerManager.isPlaying(sender)) {
					continue;
				}
				Player teammate = PlayerManager.getTeammate(sender);
				if(isDuo && ((teammate != null && teammate == receiver) || receiver == sender)) {
					receiver.sendMessage(LIGHT_PURPLE + "<Тиме> " + suffix);
				}
			}
		}
	}

	@EventHandler
	public void ghostStandInteract(PlayerArmorStandManipulateEvent e) {
		ArmorStand stand = e.getRightClicked();
		UHCPlayer player = PlayerManager.getPlayerFromGhost(stand);
		if(player != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void ghostStandPlayerDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof ArmorStand stand && e.getDamager() instanceof Player damager) {
			UHCPlayer udamager = PlayerManager.asUHCPlayer(damager);
			UHCPlayer damagedPlayer = PlayerManager.getPlayerFromGhost(stand);
			if(damagedPlayer != null) {
				e.setCancelled(true);
				UHCPlayer damagedPlayerTeammate = damagedPlayer.getTeammate();
				if(udamager != null &&
						(damagedPlayerTeammate == null || damagedPlayerTeammate != udamager) &&
						stand.getWorld().getPVP()) {
					damagedPlayer.damageGhost(damager);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void ghostStandAnyDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof ArmorStand stand &&
				e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
				e.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
			UHCPlayer damagedPlayer = PlayerManager.getPlayerFromGhost(stand);
			if(damagedPlayer != null) {
				damagedPlayer.damageGhost(null);
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		Player player = e.getPlayer();
		if(PlayerManager.isInGame(player)) {
			if(PlayerManager.isSpectator(player)) {
				PlayerManager.unregisterSpectator(player);
				for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
					inGamePlayer.sendMessage(DARK_AQUA + "" + BOLD + "- " + GOLD + player.getName() + AQUA + " перестал наблюдать за игрой");
				}
			}
			if(PlayerManager.isPlaying(player)) {
				UHCPlayer uplayer = PlayerManager.asUHCPlayer(player);
				uplayer.leave();
			}
		} else {
			for(Player currentPlayer : Lobby.getPlayersInLobbyAndArenas()) {
				currentPlayer.sendMessage(RED + "" + BOLD + "- " + RESET + GOLD + player.getName() + GRAY + " отключился");
			}
		}
		refreshScoreboardsLater();
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Player player = e.getPlayer();
		UHCPlayer uplayer = PlayerManager.asUHCPlayer(player);
		if(uplayer != null && !uplayer.isSpectator()) {
			uplayer.rejoin(player);
		} else {
			player.setPlayerListFooter(null);
			if(player.getGameMode() != GameMode.CREATIVE) {
				player.setGameMode(GameMode.ADVENTURE);
			}
			String joinMessage = GREEN + "" + BOLD + "+ " +
					RESET + GOLD + player.getName() +
					GRAY + " присоединился";
			for(Player currentPlayer : Lobby.getPlayersInLobbyAndArenas()) {
				if (currentPlayer != player) {
					currentPlayer.sendMessage(joinMessage);
				}
			}
			player.sendMessage(joinMessage);
			TaskManager.invokeLater(() -> {
				if(!Lobby.isInLobbyOrWatchingArena(player)) {
					player.teleport(Lobby.getLobby().getSpawnLocation());
					player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
					resetPlayer(player);
				}
			});
			if(playing) {
				player.sendMessage(YELLOW + "" + BOLD + "Сейчас идет игра! " + RESET + AQUA
						+ "За игрой можно наблюдать, кликнув по табличке.");
			}
		}
		refreshScoreboardsLater();
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
		if (recipe == null) {
			return;
		}
		if(recipe.getResult().getType() == Material.EMERALD_BLOCK) {
			if(Stream.of(e.getInventory().getMatrix()).anyMatch(item -> !ItemUtils.getLore(item).isEmpty())) {
				recipe.getResult().setAmount(0);
			}
		}
		if (recipe.getResult().getType() == Material.NETHERITE_INGOT) {
			e.getInventory().setResult(
					ItemUtils.addSplittedLore(recipe.getResult(), Messages.NETHERITE_TRIM_IS_NOT_REQUIRED)
			);
		}
	}

	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		if(state == GameState.VOTE || state == GameState.PREPARING) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void swap(PlayerSwapHandItemsEvent e) {
		if(state == GameState.VOTE || state == GameState.PREPARING) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void emptyBucket(PlayerBucketEmptyEvent e) {
		if(PlayerManager.isPlaying(e.getPlayer()) && state.isDeathmatch() && ArenaManager.isPvpDisabled()) {
			e.getPlayer().sendMessage(Messages.CANNOT_INTERACT_WITH_ARENA_NOW);
			e.setCancelled(true);
		}
		if(state == GameState.VOTE || state == GameState.PREPARING) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void fillBucket(PlayerBucketFillEvent e) {
		if(PlayerManager.isPlaying(e.getPlayer()) && state.isDeathmatch() && ArenaManager.isPvpDisabled()) {
			e.getPlayer().sendMessage(Messages.CANNOT_INTERACT_WITH_ARENA_NOW);
			e.setCancelled(true);
		}
		if(state == GameState.VOTE || state == GameState.PREPARING) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void portal(PlayerPortalEvent e) {
		Player player = e.getPlayer();
		if (state.isDeathmatch()) {
			e.setCancelled(true);
			return;
		}
		if(PlayerManager.isPlaying(player)) {
			Location loc = player.getLocation();
			if(player.getWorld().getEnvironment() == World.Environment.NETHER) {
				e.setCreationRadius(16);
				e.setSearchRadius(16);
				Location newLoc = new Location(WorldManager.getGameMap(), loc.getX(), loc.getY(), loc.getZ());
				World map = WorldManager.getGameMap();
				double maxX = map.getSpawnLocation().getX() + map.getWorldBorder().getSize() / 2 - 1;
				double minX = map.getSpawnLocation().getX() - map.getWorldBorder().getSize() / 2 + 1;
				double maxZ = map.getSpawnLocation().getZ() + map.getWorldBorder().getSize() / 2 - 1;
				double minZ = map.getSpawnLocation().getZ() - map.getWorldBorder().getSize() / 2 + 1;
				if(newLoc.getX() > maxX) newLoc.setX(maxX);
				if(newLoc.getX() < minX) newLoc.setX(minX);
				if(newLoc.getZ() > maxZ) newLoc.setZ(maxZ);
				if(newLoc.getZ() < minZ) newLoc.setZ(minZ);
				e.setTo(newLoc);
			} else {
				Location newLoc = new Location(WorldManager.getGameMapNether(), loc.getX(), loc.getY(), loc.getZ());
				e.setTo(newLoc);
			}
		}
	}

	@EventHandler
	public void place(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		if(PlayerManager.isPlaying(p) && state.isPreGame()) {
			event.setCancelled(true);
		}
		if(PlayerManager.isPlaying(p) && state.isDeathmatch()) {
			ItemStack itemInHand = event.getItemInHand();
			boolean isIgnite = itemInHand.getType() == Material.FLINT_AND_STEEL;
			if (ArenaManager.isPvpDisabled()) {
				if (isIgnite) {
					p.sendMessage(Messages.CANNOT_INTERACT_WITH_ARENA_NOW);
					event.setCancelled(true);
					return;
				}
				if (!MutatorManager.interactiveArena.isActive()) {
					event.setCancelled(true);
					return;
				}
			}
			CustomItem customItem = CustomItems.getCustomItem(itemInHand);
			boolean canPlaceOnArena = false;
			if(customItem instanceof BlockHolder holder) {
				if(holder.canPlaceOnDeathmatch()) canPlaceOnArena = true;
			}
			if(!CustomItems.tnt.isEquals(itemInHand) && !MutatorManager.interactiveArena.isActive() && !canPlaceOnArena) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void breakBlock(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if((state.isPreGame() || state == GameState.ENDING) && PlayerManager.isPlaying(e.getPlayer())) {
			e.setCancelled(true);
		}
		if(state.isBeforeDeathmatch() && PlayerManager.isPlaying(player)) {
			if((e.getBlock().getType() == Material.EMERALD_ORE || e.getBlock().getType() == Material.DEEPSLATE_EMERALD_ORE) && e.getExpToDrop() > 0) {
				e.setDropItems(false);
				ItemStack drop = MathUtils.choose(
						new ItemStack(Material.DIAMOND, MathUtils.randomRange(1, 2)),
						new ItemStack(Material.REDSTONE, MathUtils.randomRange(24, 40)),
						new ItemStack(Material.GOLD_INGOT, MathUtils.randomRange(5, 8)),
						new ItemStack(Material.LAPIS_LAZULI, MathUtils.randomRange(7, 12))
				);
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
				player.playSound(
						player.getLocation(),
						Sound.BLOCK_NOTE_BLOCK_CHIME,
						0.5F,
						(float) MathUtils.randomRangeDouble(1.5, 2)
				);
				e.setExpToDrop(e.getExpToDrop() * 5);
			}
		}
		if(PlayerManager.isPlaying(player) && state == GameState.DEATHMATCH) {
			Material brokenType = e.getBlock().getType();
			boolean isFireExtinguish = brokenType == Material.FIRE || brokenType == Material.SOUL_FIRE;
			if (isFireExtinguish) {
				return;
			}
			if(MutatorManager.interactiveArena.isActive()) {
				e.setDropItems(false);
				e.setExpToDrop(0);
			} else {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void emeraldTrade(PlayerInteractEntityEvent e) {
		if(e.getRightClicked() instanceof Villager villager) {
			List<MerchantRecipe> recipes = villager.getRecipes();
			List<MerchantRecipe> newRecipes = new ArrayList<>();
			for(MerchantRecipe recipe : recipes) {
				var result = recipe.getResult();
				if(result.getType() != Material.EMERALD) {
					newRecipes.add(recipe);
					continue;
				}
				ItemUtils.setLore(result, DARK_RED + "" + BOLD + "Без бонусов!");
				var newRecipe = new MerchantRecipe(
						result,
						recipe.getUses(),
						recipe.getMaxUses(),
						recipe.hasExperienceReward(),
						recipe.getVillagerExperience(),
						recipe.getPriceMultiplier(),
						recipe.getDemand(),
						recipe.getSpecialPrice()
				);
				newRecipe.setIngredients(recipe.getIngredients());
				newRecipes.add(newRecipe);
			}
			villager.setRecipes(newRecipes);
		}
	}

	@EventHandler
	public void interact(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(state == GameState.VOTE && PlayerManager.isPlaying(player) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			if(item.getType() != Material.AIR) {
				passVote(player, item.getType() == Material.LIME_DYE);
			}
			e.setCancelled(true);
		}
		ItemStack item = e.getItem();
		if(state.isBeforeDeathmatch()
				&& PlayerManager.isPlaying(player)
				&& (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& item != null) {
			if (item.getType() == Material.EMERALD && ItemUtils.getLore(item).isEmpty()) {
				useEmerald(player, item);
			}
			if (item.getType() == Material.NETHERITE_INGOT) {
				useNetheriteIngot(player);
			}
		}
		if(state == GameState.PREPARING && PlayerManager.isPlaying(player)) {
			e.setCancelled(true);
		}
	}

	private void useEmerald(Player player, ItemStack item) {
		ItemStack drop = MathUtils.choose(
				new ItemStack(Material.DIAMOND, MathUtils.randomRange(1, 2)),
				new ItemStack(Material.REDSTONE, MathUtils.randomRange(24, 40)),
				new ItemStack(Material.GOLD_INGOT, MathUtils.randomRange(5, 8)),
				new ItemStack(Material.LAPIS_LAZULI, MathUtils.randomRange(7, 12))
		);
		item.setAmount(item.getAmount() - 1);
		player.playSound(
				player.getLocation(),
				Sound.BLOCK_NOTE_BLOCK_CHIME,
				0.5F,
				(float) MathUtils.randomRangeDouble(1.5, 2)
		);
		player.getWorld().dropItemNaturally(player.getLocation().add(-0.5, 0.5, -0.5), drop);
	}

	private void useNetheriteIngot(Player player) {
		player.playSound(
				player.getLocation(),
				Sound.BLOCK_NOTE_BLOCK_XYLOPHONE,
				0.5F,
				(float) MathUtils.randomRangeDouble(0.5, 1)
		);
		ItemStack template = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
		player.getWorld().dropItemNaturally(player.getLocation().add(-0.5, 0.5, -0.5), template);
	}

	@EventHandler
	public void inventory(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if((state == GameState.VOTE || state == GameState.PREPARING) && PlayerManager.isPlaying(p)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void tntArena(EntityExplodeEvent event) {
		if(event.getEntityType() == EntityType.PRIMED_TNT) {
			if (state == GameState.ENDING) {
				event.blockList().clear();
				return;
			}
			if (state != GameState.DEATHMATCH) {
				return;
			}
			event.setYield(0);
			if (!MutatorManager.interactiveArena.isActive()) {
				event.blockList().clear();
			}
		}
	}

	@EventHandler
	public void pluginDisable(PluginDisableEvent event) {
		if(UHC.playing) {
			UHC.endGame();
		}
	}

}
