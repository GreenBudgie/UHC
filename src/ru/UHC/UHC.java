package ru.UHC;

import com.google.common.collect.Lists;
import net.minecraft.world.item.trading.MerchantRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
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
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import ru.artifact.ArtifactManager;
import ru.block.CustomBlockManager;
import ru.classes.ClassManager;
import ru.classes.UHCClass;
import ru.drop.Drop;
import ru.drop.Drops;
import ru.event.GameEndEvent;
import ru.event.GameInitializeEvent;
import ru.event.GameStartEvent;
import ru.items.BlockHolder;
import ru.items.CustomItem;
import ru.items.CustomItems;
import ru.lobby.Lobby;
import ru.lobby.LobbyGameManager;
import ru.lobby.LobbyTeamBuilder;
import ru.lobby.sign.SignManager;
import ru.mutator.Mutator;
import ru.mutator.MutatorManager;
import ru.rating.GameSummary;
import ru.rating.Rating;
import ru.requester.ItemRequester;
import ru.requester.RequestedItem;
import ru.util.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	public static int mapSize = 1;
	public static int gameDuration = 1;
	public static boolean generating = false;
	public static boolean isRatingGame = true;
	public static String timerInfo = "";
	public static int fastStart = 0; //0 - disabled, 1 - without mutators, 2 - with mutators
	private static int mutatorCount = 0;
	private static Mutator prevMutator = null;
	private static BossBar voteBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
	//V 3.0
	private static Region platformRegion = null;
	private static int scoreboardCurrentTeamIndex;
	private static int scoreboardTimeUntilNextTeam;
	private static final int scoreboardMaxTimeUntilNextTeam = 3;
	/**
	 * Used to ensure that time reduce will only happen once
	 * when there is only 3 teams left
	 */
	private static boolean reduceTimeOnFewTeams = true;

	private static final String UHC_LOGO =
			ChatColor.RED + "" + ChatColor.BOLD + "U" +
			ChatColor.GOLD + ChatColor.BOLD + "H" +
			ChatColor.YELLOW + ChatColor.BOLD + "C";

	private static final int DEATHMATCH_NO_PVP_DURATION = 15; //15 seconds
	public static final int DEATHMATCH_START_TIMER = 8 * 60; //8 minutes
	private static final int DEATHMATCH_TIME_UNTIL_ZONE_SHRINK = 3 * 60; //3 minutes
	private static final int DEATHMATCH_ZONE_SHRINK_DURATION = 2 * 60; //2 minutes

	private static final int PREPARING_MAX_TIME_WITH_MUTATORS = 35;
	private static final int PREPARING_MAX_TIME_NO_MUTATORS = 15;
	private static int preparingMaxTime;

	public static void init() {
		WorldManager.init();
		RecipeHandler.init();
		Lobby.init();
		MutatorManager.init();
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
		spectatorTeam.setColor(ChatColor.WHITE);
		PlayerManager.getSpectators().forEach(pl -> spectatorTeam.addEntry(pl.getName()));

		Team lobbyTeam = board.registerNewTeam("LobbyTeam");
		lobbyTeam.setColor(ChatColor.GOLD);
		Lobby.getPlayersInLobbyAndArenas().forEach(pl -> lobbyTeam.addEntry(pl.getName()));

		Team playerTeam = board.registerNewTeam("PlayerTeam");
		playerTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		playerTeam.setCanSeeFriendlyInvisibles(false);
		playerTeam.setColor(ChatColor.AQUA);
		PlayerManager.getAliveOnlinePlayers().forEach(pl -> playerTeam.addEntry(pl.getName()));

		p.setScoreboard(board);
		updateGameScoreboard(p);
	}

	private static String getPrefix(UHCPlayer currentPlayer, Player scoreboardOwner) {
		String prefix = ChatColor.GOLD + "";
		if(!currentPlayer.isAlive()) prefix = ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH; else
			if(!currentPlayer.isOnline()) prefix = ChatColor.GRAY + ""; else
				if(currentPlayer.compare(scoreboardOwner)) prefix = ChatColor.GREEN + "";
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
				if(aliveTeams.size() > 0) {
					PlayerTeam currentTeam = aliveTeams.get(scoreboardCurrentTeamIndex);
					String prefix = ChatColor.DARK_GRAY + "[" +
							ChatColor.WHITE + "" + ChatColor.BOLD + (scoreboardCurrentTeamIndex + 1) +
							ChatColor.DARK_GRAY + "] ";
					UHCPlayer player1 = currentTeam.getPlayer1();
					String player1Prefix = getPrefix(player1, player);

					UHCPlayer player2 = currentTeam.getPlayer2();
					String teamInfo1 = prefix + player1Prefix + player1.getNickname();
					String teamInfo2;
					if(player2 != null) {
						String player2Prefix = getPrefix(player2, player);
						teamInfo2 = prefix + player2Prefix + player2.getNickname();
					} else {
						teamInfo2 = prefix + ChatColor.GRAY + "Нет тиммейта";
					}
					Score teamScore2 = gameInfo.getScore(teamInfo2);
					teamScore2.setScore(c++);
					Score teamScore1 = gameInfo.getScore(teamInfo1);
					teamScore1.setScore(c++);
				}
			}

			int aliveTeamsNumber = PlayerManager.getAliveTeams().size();
			String teamCountInfo = ChatColor.GRAY + "В живых " +
					ChatColor.WHITE + ChatColor.BOLD + aliveTeamsNumber +
					ChatColor.GRAY + " ";
			if(isDuo) {
				teamCountInfo += new NumericalCases("команда", "команды", "команд").byNumber(aliveTeamsNumber);
			} else {
				teamCountInfo += new NumericalCases("игрок", "игрока", "игроков").byNumber(aliveTeamsNumber);
			}
			Score teamCountScore = gameInfo.getScore(teamCountInfo);
			teamCountScore.setScore(c++);
		}

		if(state.isInGame()) {
			final boolean show = state == GameState.PREPARING || state == GameState.VOTE || state == GameState.OUTBREAK;
			for(Drop drop : new Drop[] {Drops.NETHERDROP, Drops.CAVEDROP, Drops.AIRDROP}) {
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

		registerHpInfo(player, "hpInfoList", board, DisplaySlot.PLAYER_LIST);
		registerHpInfo(player, "hpInfoName", board, DisplaySlot.BELOW_NAME);
	}

	private static void registerHpInfo(Player playerToShowInfo, String name, Scoreboard board, DisplaySlot slot) {
		Objective hpInfo = board.getObjective(name);
		if(hpInfo != null) hpInfo.unregister();
		hpInfo = board.registerNewObjective(name, "health", ChatColor.RED + "\u2764");
		for(Player player : PlayerManager.getAliveOnlinePlayers()) {
			Score hp = hpInfo.getScore(player.getName());
			hp.setScore((int) player.getHealth());
		}
		if(PlayerManager.isSpectator(playerToShowInfo) || MutatorManager.isActive(MutatorManager.healthDisplay)) {
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
		spectatorTeam.setColor(ChatColor.WHITE);
		PlayerManager.getSpectators().forEach(pl -> spectatorTeam.addEntry(pl.getName()));

		Team lobbyTeam = board.registerNewTeam("LobbyTeam");
		lobbyTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
		lobbyTeam.setCanSeeFriendlyInvisibles(false);
		lobbyTeam.setColor(ChatColor.GOLD);
		Lobby.getPlayersInLobbyAndArenas().forEach(pl -> lobbyTeam.addEntry(pl.getName()));

		Team playerTeam = board.registerNewTeam("PlayerTeam");
		playerTeam.setColor(ChatColor.AQUA);
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
				String currentPlayerName = (currentPlayer == player ? ChatColor.GREEN : ChatColor.GOLD) + currentPlayer.getName();
				if(currentTeammate != null) {
					if(registered.contains(currentTeammate)) continue;
					String currentTeammateName = ((currentTeammate == player) ? ChatColor.GREEN : ChatColor.GOLD) + currentTeammate.getName();
					registered.add(currentTeammate);
					String finalString = ChatColor.DARK_GRAY + "- " + currentPlayerName + ChatColor.WHITE + " / " + currentTeammateName;
					String[] trimmed = trimTeam(currentPlayerName, currentTeammateName, finalString.length(), 40);
					currentPlayerName = trimmed[0];
					currentTeammateName = trimmed[1];
					s = ChatColor.DARK_GRAY + "- " + currentPlayerName + ChatColor.WHITE + " / " + currentTeammateName;
				} else {
					s = ChatColor.DARK_GRAY + "- " + currentPlayerName;
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
					ChatColor.GRAY + teamNumberText1 + " " +
							ChatColor.DARK_AQUA + ChatColor.BOLD + teamNumber + " " +
							ChatColor.GRAY + teamNumberText2 +
							ChatColor.DARK_GRAY + ":");
			teamNumberInfo.setScore(c++);
		}

		UHCClass selectedClass = ClassManager.getClassInLobby(player);
		if(selectedClass != null) {
			Score classInfo = teamInfo.getScore(
						ChatColor.GRAY + "Класс" +
							ChatColor.DARK_GRAY + ": " +
							selectedClass.getName());
			classInfo.setScore(c);
		}
	}

	public static void endGame() {
		if(playing) {
			Bukkit.getPluginManager().callEvent(new GameEndEvent());

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
			mapSize = 1;
			gameDuration = 1;
			isRatingGame = true;
			clearPlatformRegion();
			platformRegion = null;
			if(!WorldManager.keepMap && !state.isPreGame()) WorldManager.removeMap();
			state = GameState.STOPPED;
			playing = false;
			SignManager.updateTextOnSigns();
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
			reduceTimeOnFewTeams = true;
			if(GameType.getType().allowsMutators()) {
				mutatorCount = MathUtils.chance(30) ? 4 : (MathUtils.chance(65) ? 3 : 2);
			} else {
				mutatorCount = 0;
			}
			voteResults.clear();
			for(Player player : Bukkit.getOnlinePlayers()) {
				InventoryHelper.sendActionBarMessage(player,
						ChatColor.GOLD + "" + ChatColor.BOLD + "Игра" +
							ChatColor.RED + ChatColor.BOLD + " начинается" +
							ChatColor.GRAY + "!");
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
				LobbyGameManager.PVP_ARENA.onArenaLeave(player);
				UHCPlayer uhcPlayer = PlayerManager.registerPlayer(player);
				resetPlayer(player);
				player.setNoDamageTicks(600);
				player.setGameMode(GameMode.ADVENTURE);
				player.getInventory().setItem(3, InventoryHelper.generateItemWithName(Material.LIME_DYE,
						ChatColor.GREEN + "Карта " + ChatColor.DARK_GREEN + ChatColor.BOLD + "Норм"));
				player.getInventory().setItem(5, InventoryHelper.generateItemWithName(Material.RED_DYE,
						ChatColor.RED + "Карта " + ChatColor.DARK_RED + ChatColor.BOLD + "Говно"));
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

			if(UHC.fastStart == 0) {
				for(UHCPlayer uplayer : PlayerManager.getPlayers()) {
					voteBar.addPlayer(uplayer.getPlayer());
				}
				voteBar.setVisible(true);
				updateVoteBar();
				state = GameState.VOTE;
				voteTimer = 20;
			} else {
				state = GameState.PREPARING;
				preparingTimer = 3;
			}
			playing = true;
			ArenaManager.getCurrentArena().getWorld().setPVP(false);
			SignManager.updateTextOnSigns();

			Bukkit.getPluginManager().callEvent(new GameInitializeEvent());
		} else {
			Bukkit.broadcastMessage(ChatColor.RED + "Игра уже идет");
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
			if(GameType.getType().allowsMutators()) {
				preparingTimer = PREPARING_MAX_TIME_WITH_MUTATORS;
			} else {
				preparingTimer = PREPARING_MAX_TIME_NO_MUTATORS;
			}
			preparingMaxTime = preparingTimer;
			for(Player p : PlayerManager.getAliveOnlinePlayers()) {
				p.sendTitle(ChatColor.GREEN + "Карта " + ChatColor.DARK_GREEN + ChatColor.BOLD + "Норм",
						ChatColor.DARK_AQUA + "" + ChatColor.BOLD + preparingTimer +
								ChatColor.GOLD + " секунд до начала",
						10, 60, 30);
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
				p.getInventory().clear();
			}
			state = GameState.PREPARING;
		} else {
			for(Player p : PlayerManager.getAliveOnlinePlayers()) {
				p.sendTitle(ChatColor.RED + "Карта " + ChatColor.DARK_RED + ChatColor.BOLD + "Говно" + ChatColor.GRAY + "!",
						ChatColor.GOLD + "Большинство проголосовало против", 10, 60, 30);
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
		for(Player p : PlayerManager.getAliveOnlinePlayers()) {
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
		for(Player p : PlayerManager.getAliveOnlinePlayers()) {
			p.sendMessage(	ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "< " + ChatColor.RESET + MutatorManager.getMessageFromCurrentMutators() +
							ChatColor.RESET + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " >");
			p.sendTitle(ChatColor.GREEN + "Выбор закончен!", "", 0, 60, 10);
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
				timerInfo = ChatColor.GOLD + "Голосование" + ChatColor.GRAY + ": " + ChatColor.AQUA + ChatColor.BOLD + voteTimer;
			}
		}
		if(state == GameState.PREPARING) {
			if(TaskManager.isSecUpdated()) {
				preparingTimer--;
				if(preparingTimer == preparingMaxTime - 5) {
					for(Player p : PlayerManager.getAliveOnlinePlayers()) {
						p.sendTitle(getUHCLogo() + ChatColor.DARK_GRAY + " aka " + ChatColor.GOLD + "Битва Инвалидов",
								ChatColor.DARK_GRAY + "v3.0", 5, 100, 20);
						p.sendMessage(
								ChatColor.GRAY + "-------- " + getUHCLogo() + ChatColor.DARK_GRAY + " aka " + ChatColor.GOLD + "Битва Инвалидов" + ChatColor.GRAY
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
					for(Player p : PlayerManager.getAliveOnlinePlayers()) {
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
			if(mutatorCount > 0) {
				if(preparingTimer == 26) {
					if(TaskManager.tick == 0) {
						for(Player p : PlayerManager.getAliveOnlinePlayers()) {
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
					WorldManager.getGameMapNether().setPVP(true);
					for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
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
					arenaTimer = DEATHMATCH_START_TIMER;
					for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
						inGamePlayer.teleport(ArenaManager.getCurrentArena().getWorld().getSpawnLocation());
						inGamePlayer.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Дезматч" + ChatColor.GRAY + "!",
								ChatColor.DARK_AQUA + "" + ChatColor.BOLD + DEATHMATCH_NO_PVP_DURATION +
										ChatColor.RESET + ChatColor.GOLD + " секунд до " +
										ChatColor.DARK_RED + ChatColor.BOLD + "ПВП" +
										ChatColor.GRAY + "!",
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
					arenaBorder.setSize(arena.getMaxBorderSize());
					arenaBorder.setSize(arena.getMinBorderSize(), DEATHMATCH_ZONE_SHRINK_DURATION);
					for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
						inGamePlayer.playSound(inGamePlayer.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 0.8f);
						inGamePlayer.sendTitle(" ", ChatColor.DARK_RED + "" + ChatColor.BOLD + ">>> " +
								ChatColor.RED + "Арена сужается" + ChatColor.GRAY + "!" +
								ChatColor.DARK_RED + ChatColor.BOLD + " <<<",
								10, 25, 15);
					}
				}
				int timeUntilPvp = DEATHMATCH_NO_PVP_DURATION - (DEATHMATCH_START_TIMER - arenaTimer);
				if(timeUntilPvp == 0) {
					ArenaManager.getCurrentArena().getWorld().setPVP(true);
					for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
						p.sendTitle(" ", ChatColor.DARK_RED + "" + ChatColor.BOLD + ">>> " + ChatColor.GOLD + ChatColor.BOLD + "ПВП" + ChatColor.RED +
								ChatColor.BOLD + " Включено!" + ChatColor.DARK_RED + ChatColor.BOLD + " <<<", 0, 35, 15);
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
					}
				}
				if(timeUntilPvp <= 3 && timeUntilPvp > 0) {
					for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
						p.sendTitle(" ", ChatColor.DARK_GRAY + "" + ChatColor.BOLD + timeUntilPvp, 0, 40, 0);
						p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 0.8F, 0.5F + timeUntilPvp / 3F);
					}
				}
				if(timeUntilPvp > 0) {
					timerInfo = ChatColor.RED + "" + ChatColor.BOLD + "До" + ChatColor.DARK_RED + ChatColor.BOLD + " ПВП" + ChatColor.GRAY + ": " +
							ChatColor.DARK_AQUA + ChatColor.BOLD + timeUntilPvp;
				} else if(timeUntilShrink > 0) {
					timerInfo = ChatColor.DARK_RED + "" + ChatColor.BOLD + "Сужение арены" + ChatColor.GRAY + ": " +
							ChatColor.DARK_AQUA + ChatColor.BOLD + MathUtils.formatTime(timeUntilShrink);
				} else {
					timerInfo = ChatColor.AQUA + "" + ChatColor.BOLD + "Ничья через:" + ChatColor.GRAY + ": " +
							ChatColor.DARK_AQUA + ChatColor.BOLD + MathUtils.formatTime(arenaTimer);
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
		if(TaskManager.ticksPassed(5) && playing) {
			for(Player onlinePlayer : PlayerManager.getAliveOnlinePlayers()) {
				UHCPlayer teammate = PlayerManager.getUHCTeammate(onlinePlayer);
				if(teammate != null && teammate.isAlive()) {
					Location teammateLocation = teammate.getLocation();
					if(teammateLocation == null) continue;
					String slash = ChatColor.GRAY + " | ";
					String comma = ChatColor.GRAY + ", ";
					String distanceInfo =
							onlinePlayer.getWorld() == teammateLocation.getWorld() ?
							String.valueOf(((int) teammateLocation.distance(onlinePlayer.getLocation()))) :
							(WorldHelper.getEnvironmentNamePrepositional(teammateLocation.getWorld().getEnvironment(), ChatColor.GRAY));
					String locationInfo =
							ChatColor.DARK_AQUA + "" + teammateLocation.getBlockX() + comma +
							ChatColor.DARK_AQUA + teammateLocation.getBlockY() + comma +
							ChatColor.DARK_AQUA + teammateLocation.getBlockZ() +
							ChatColor.DARK_GRAY + " (" +
							ChatColor.AQUA + distanceInfo +
							ChatColor.DARK_GRAY + ")";
					String teammateInfo =
							ChatColor.GOLD + teammate.getNickname() + slash +
							ChatColor.RED + ((int) Math.round(teammate.getRealOrOfflineHealth())) +
							ChatColor.DARK_RED + " \u2764" + slash +
							locationInfo;
					if(onlinePlayer.getWorld() == teammateLocation.getWorld())
						teammateInfo += ChatColor.AQUA + " " + getArrow(onlinePlayer.getLocation(), teammateLocation);
					InventoryHelper.sendActionBarMessage(onlinePlayer, teammateInfo);
				}
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

			for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
				updateGameScoreboard(inGamePlayer);
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
							.collect(Collectors.toList());
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
				}
			}
		}
	}

	private static char getArrow(Location l1, Location l2) {
		double x1 = l1.getX();
		double z1 = l1.getZ();
		double x2 = l2.getX();
		double z2 = l2.getZ();
		double playerLookAngle = l1.getYaw();
		playerLookAngle = playerLookAngle % 360.0D;
		double teammateAngle = Math.atan2(z2 - z1, x2 - x1);
		double finalAngle = (Math.PI - (Math.toRadians(playerLookAngle - 90.0D) - teammateAngle)) % (Math.PI * 2);
		if(finalAngle < 0) finalAngle = 2 * Math.PI + finalAngle;
		char[] arrows = new char[] {'\u2191', '\u2B08', '\u2192', '\u2B0A', '\u2193', '\u2B0B', '\u2190', '\u2B09', '\u2191'};
		double step = Math.PI / 4;
		double range = Math.PI / 8;
		char arrow = ' ';
		for(int i = 0; i < arrows.length; i++) {
			double currentAngle = i * step;
			if(inRange(finalAngle, currentAngle - range, currentAngle + range)) {
				arrow = arrows[i];
			}
		}
		return arrow;
	}

	private static boolean inRange(double num, double min, double max) {
		return num >= min && num <= max;
	}

	public static int getMapSize() {
		return mapSize == 0 ? 36 : (mapSize == 1 ? 52 : (mapSize == 2 ? 68 : 512));
	}

	public static void draw() {
		if(state != GameState.ENDING) {
			state = GameState.ENDING;
			endTimer = 8;
		}
		for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			inGamePlayer.setGameMode(GameMode.SPECTATOR);
			inGamePlayer.sendTitle(ChatColor.YELLOW + "Ничья", ChatColor.GOLD + "Видимо, игра затянулась", 10, 60, 20);
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
			String names = ChatColor.GOLD + uplayer1.getNickname() + ChatColor.YELLOW + " и " + ChatColor.GOLD + uplayer2.getNickname();
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(names + ChatColor.GREEN + " победили!");
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
			}
		} else {
			for(Player pl : Bukkit.getOnlinePlayers()) {
				pl.sendMessage(ChatColor.GOLD + uplayer1.getNickname() + ChatColor.GREEN + " победил!");
				pl.playSound(pl.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
			}
		}
		for(UHCPlayer uhcWinner : team.getPlayers()) {
			uhcWinner.getSummary().setWinningPlace(1);
			if(uhcWinner.isInGame()) {
				Player winner = uhcWinner.getPlayer();
				winner.setGameMode(GameMode.SPECTATOR);
				winner.sendTitle(ChatColor.YELLOW + "Ты победил!", "", 10, 40, 20);
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
					inGamePlayer.sendMessage(ChatColor.YELLOW + "Скрытым мутатором был: " + ChatColor.LIGHT_PURPLE + hiddenMutators.get(0).getName());
				} else {
					inGamePlayer.sendMessage(ChatColor.YELLOW + "Скрытыми мутаторами были:");
					for(Mutator mutator : hiddenMutators) {
						inGamePlayer.sendMessage(ChatColor.DARK_GRAY + "- " + ChatColor.LIGHT_PURPLE + mutator.getName());
					}
				}
			}
		}
	}

	public static void recalculateTimeOnPlayerDeath() {
		int aliveTeams = PlayerManager.getAliveTeams().size();
		int alivePlayers = PlayerManager.getAlivePlayers().size();
		boolean fewTeams = isDuo && aliveTeams <= 3;
		boolean fewPlayers = alivePlayers <= 4 && alivePlayers > 1;
		if(state == GameState.OUTBREAK && (fewPlayers || fewTeams)) {
			state = GameState.GAME;
			deathmatchTimer = 60 * getGameDuration();
			WorldManager.getGameMap().setPVP(true);
			for(Player inGamePlayer : PlayerManager.getAliveOnlinePlayers()) {
				inGamePlayer.playSound(inGamePlayer.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 0.5F);
				inGamePlayer.sendTitle(ChatColor.GOLD + "ПВП Включено!", "", 10, 60, 30);
			}
		}
		int minTime = 600;
		if(state == GameState.GAME && deathmatchTimer > minTime && (fewPlayers || fewTeams)) {
			boolean timeChanged = false;
			if((alivePlayers == 4 || aliveTeams == 3) && reduceTimeOnFewTeams) {
				deathmatchTimer = (int) Math.max(deathmatchTimer / 1.3, minTime);
				reduceTimeOnFewTeams = false;
				timeChanged = true;
			} else if(alivePlayers == 3) {
				deathmatchTimer = (int) Math.max(deathmatchTimer / 1.5, minTime);
				timeChanged = true;
			} else if(alivePlayers == 2) {
				deathmatchTimer = (int) Math.max(deathmatchTimer / 1.7, minTime);
				timeChanged = true;
			}
			if(timeChanged) {
				for(Player player : PlayerManager.getInGamePlayersAndSpectators()) {
					player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.5F, 1.2F);
					String remainInfo = new NumericalCases("остался ", "осталось ", "осталось ").byNumber(alivePlayers);
					String playerInfo = new NumericalCases(" игрок.", " игрока.", " игроков.").byNumber(alivePlayers);
					player.sendMessage(ChatColor.GOLD + "В живых " + remainInfo + ChatColor.AQUA + ChatColor.BOLD + alivePlayers +
							ChatColor.GOLD + playerInfo + ChatColor.RED + ChatColor.BOLD + " Время сокращено!");
				}
			}
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
		if(UHC.fastStart != 1) {
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
			if(MutatorManager.isActive(MutatorManager.hungerGames)) {
				player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Игра " + ChatColor.GOLD + ChatColor.BOLD + "началась!",
						ChatColor.YELLOW + "У тебя " + ChatColor.DARK_RED + ChatColor.BOLD + "ОДНА СУКА МИНУТА" +
								ChatColor.YELLOW + " на развитие без ПВП", 10, 60, 30);
			} else {
				player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Игра " + ChatColor.GOLD + ChatColor.BOLD + "началась!",
						ChatColor.YELLOW + "У тебя " + ChatColor.AQUA + ChatColor.BOLD + getNoPVPDuration() + ChatColor.YELLOW + " минут на развитие без ПВП",
						10, 60, 30);
			}
			if(ArenaManager.doAnnounceArena() || ArenaManager.getChosenArena() != null) {
				player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "> " +
						ChatColor.GRAY + "Дезматч будет проходить на арене" +
						ChatColor.DARK_GRAY + ": " +
						ChatColor.DARK_GREEN + ArenaManager.getCurrentArena().getName() +
						ChatColor.DARK_GRAY + "" + ChatColor.BOLD + " <");
			}
			player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.4F, 1);
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
		voteBar.setTitle(ChatColor.GOLD + "Голосование " + ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + ChatColor.BOLD + votesFor + ChatColor.GRAY + " / " +
				ChatColor.RED + ChatColor.BOLD + votesAgainst + ChatColor.DARK_GRAY + ")");
		voteBar.setProgress(progress);
	}

	public static void heal(Player p) {
		p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
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

	public static ItemStack getBonusShell() {
		return ItemUtils.builder(Material.SHULKER_SHELL).withGlow().withName(ChatColor.LIGHT_PURPLE + "Сияющий панцирь")
				.withSplittedLore(ChatColor.GOLD + "Окружи его золотыми слитками и получи 2 золотых яблока").build();
	}

	private static String invViewStart = ChatColor.DARK_GRAY + "Инвентарь";

	public static void viewInventory(Player observer, Player target) {
		final int size = 9 * 6;
		PlayerInventory targetInventory = target.getInventory();
		Inventory currentInventory = Bukkit.createInventory(observer, size, invViewStart + ChatColor.DARK_AQUA + ChatColor.BOLD + " " + target.getName());
		for(int i = 0; i < targetInventory.getStorageContents().length; i++) {
			ItemStack item = targetInventory.getStorageContents()[i];
			currentInventory.setItem(i, item);
		}
		ItemStack blackPanel = ItemUtils.builder(Material.BLACK_STAINED_GLASS_PANE).withName(" ").build();
		for(int slot = size - 18; slot < size - 9; slot++) {
			currentInventory.setItem(slot, blackPanel);
		}
		for(int slot = size - 9 + 4; slot < size - 1; slot++) {
			currentInventory.setItem(slot, blackPanel);
		}
		currentInventory.setItem(size - 9, targetInventory.getHelmet());
		currentInventory.setItem(size - 9 + 1, targetInventory.getChestplate());
		currentInventory.setItem(size - 9 + 2, targetInventory.getLeggings());
		currentInventory.setItem(size - 9 + 3, targetInventory.getBoots());
		currentInventory.setItem(size - 1, targetInventory.getItemInOffHand());
		observer.openInventory(currentInventory);
	}

	@EventHandler
	public void noClick(InventoryClickEvent e) {
		if(e.getView().getTitle().startsWith(invViewStart)) {
			e.setCancelled(true);
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
			//Fixing stews to regenerate only 1 heart instead of 1.5 hearts
			if(item.getType() == Material.SUSPICIOUS_STEW) {
				SuspiciousStewMeta meta = (SuspiciousStewMeta) item.getItemMeta();
				if(meta.hasCustomEffect(PotionEffectType.REGENERATION)) {
					TaskManager.invokeLater(() -> {

						PotionEffect regen = player.getPotionEffect(PotionEffectType.REGENERATION);
						if(regen != null) {
							player.addPotionEffect(new PotionEffect(regen.getType(), (int) (regen.getDuration() / 1.5), regen.getAmplifier(), regen.isAmbient(),
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
		String suffix = ChatColor.GOLD + sender.getName() + ChatColor.WHITE + ": " + mes;
		String prefix = ChatColor.LIGHT_PURPLE + "<Локально> ";
		for(Player receiver : Bukkit.getOnlinePlayers()) {
			if(!local) {
				if(Lobby.isInLobbyOrWatchingArena(sender) && PlayerManager.isInGame(receiver)) {
					receiver.sendMessage(ChatColor.YELLOW + "<Лобби> " + suffix);
					continue;
				}
				if(Lobby.isInLobbyOrWatchingArena(receiver) && PlayerManager.isInGame(sender)) {
					receiver.sendMessage(ChatColor.AQUA + "<Игра> " + suffix);
					continue;
				}
				if(PlayerManager.isSpectator(sender)) {
					receiver.sendMessage(ChatColor.DARK_RED + "<Мертв> " + suffix);
					continue;
				}
				receiver.sendMessage(suffix);
			} else {
				if((Lobby.isInLobbyOrWatchingArena(sender) && Lobby.isInLobbyOrWatchingArena(receiver)) ||
						(PlayerManager.isSpectator(sender) && PlayerManager.isSpectator(receiver)) ||
						(!isDuo && PlayerManager.isPlaying(sender) && PlayerManager.isPlaying(receiver))) {
					receiver.sendMessage(prefix + suffix);
					continue;
				}
				Player teammate = PlayerManager.getTeammate(sender);
				if(isDuo && ((teammate != null && teammate == receiver) || receiver == sender)) {
					receiver.sendMessage(ChatColor.LIGHT_PURPLE + "<Тиме> " + suffix);
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
				PlayerManager.removeSpectator(player);
				for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
					inGamePlayer.sendMessage(ChatColor.AQUA + "Наблюдатель " + ChatColor.GOLD + player.getName() + ChatColor.AQUA + " отключился");
				}
			}
			if(PlayerManager.isPlaying(player)) {
				UHCPlayer uplayer = PlayerManager.asUHCPlayer(player);
				uplayer.leave();
			}
		} else {
			for(Player currentPlayer : Lobby.getPlayersInLobbyAndArenas()) {
				currentPlayer.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "- " + ChatColor.RESET + ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " отключился");
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
			String joinMessage = ChatColor.GREEN + "" + ChatColor.BOLD + "+ " +
					ChatColor.RESET + ChatColor.GOLD + player.getName() +
					ChatColor.YELLOW + " присоединился";
			for(Player currentPlayer : Lobby.getPlayersInLobbyAndArenas()) {
				currentPlayer.sendMessage(joinMessage);
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
				player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Сейчас идет игра! " + ChatColor.RESET + ChatColor.AQUA
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
		if(recipe != null && e.getRecipe().getResult().getType() == Material.EMERALD_BLOCK) {
			if(Stream.of(e.getInventory().getMatrix()).anyMatch(item -> !ItemUtils.getLore(item).isEmpty())) {
				recipe.getResult().setAmount(0);
			}
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
		if(PlayerManager.isPlaying(e.getPlayer()) && state == GameState.DEATHMATCH && !MutatorManager.interactiveArena.isActive()) {
			e.setCancelled(true);
		}
		if(state == GameState.VOTE || state == GameState.PREPARING) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void fillBucket(PlayerBucketFillEvent e) {
		if(PlayerManager.isPlaying(e.getPlayer()) && state == GameState.DEATHMATCH && !MutatorManager.interactiveArena.isActive()) {
			e.setCancelled(true);
		}
		if(state == GameState.VOTE || state == GameState.PREPARING) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void portal(PlayerPortalEvent e) {
		Player player = e.getPlayer();
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
	public void place(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(PlayerManager.isPlaying(p) && (state == GameState.PREPARING || state == GameState.VOTE)) {
			e.setCancelled(true);
		}
		if(PlayerManager.isPlaying(p) && state == GameState.DEATHMATCH) {
			CustomItem customItem = CustomItems.getCustomItem(e.getItemInHand());
			boolean canPlaceOnArena = false;
			if(customItem instanceof BlockHolder holder) {
				if(holder.canPlaceOnDeathmatch()) canPlaceOnArena = true;
			}
			if(!CustomItems.tnt.isEquals(e.getItemInHand()) && !MutatorManager.interactiveArena.isActive() && !canPlaceOnArena) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void breakBlock(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if((state.isPreGame() || state == GameState.ENDING) && PlayerManager.isPlaying(e.getPlayer())) {
			e.setCancelled(true);
		}
		if(state.isInGame() && PlayerManager.isPlaying(p)) {
			if(e.getBlock().getType() == Material.EMERALD_ORE && e.getExpToDrop() > 0) {
				e.setDropItems(false);
				ItemStack drop = MathUtils
						.choose(new ItemStack(Material.DIAMOND, MathUtils.randomRange(1, 2)), new ItemStack(Material.REDSTONE, MathUtils.randomRange(24, 40)),
								new ItemStack(Material.GOLD_INGOT, MathUtils.randomRange(5, 8)));
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
				e.setExpToDrop(e.getExpToDrop() * 5);
			}
		}
		if(PlayerManager.isPlaying(p) && state == GameState.DEATHMATCH) {
			if(MutatorManager.interactiveArena.isActive()) {
				e.setDropItems(false);
				e.setExpToDrop(0);
			} else {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void noTeleport(PlayerTeleportEvent e) {
		World world = e.getTo().getWorld();
		boolean isGameWorld = world == WorldManager.getGameMap() ||
				world == WorldManager.getGameMapNether() ||
				(ArenaManager.getCurrentArena() != null && world == ArenaManager.getCurrentArena().getWorld());
		if(e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE && !isGameWorld) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void spectatorOpenInv(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if((state.isInGame() || state == GameState.DEATHMATCH) &&
				e.getHand() == EquipmentSlot.HAND && e.getRightClicked() instanceof Player clicked && PlayerManager.isSpectator(p)) {
			if(PlayerManager.isPlaying(clicked)) {
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
		if(state == GameState.VOTE && PlayerManager.isPlaying(p) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			ItemStack item = p.getInventory().getItemInMainHand();
			if(item.getType() != Material.AIR) {
				passVote(p, item.getType() == Material.LIME_DYE);
			}
			e.setCancelled(true);
		}
		ItemStack item = e.getItem();
		if(state.isInGame() && PlayerManager.isPlaying(p) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && item != null
				&& item.getType() == Material.EMERALD && ItemUtils.getLore(item).isEmpty()) {
			ItemStack drop = MathUtils
					.choose(new ItemStack(Material.DIAMOND, MathUtils.randomRange(1, 2)), new ItemStack(Material.REDSTONE, MathUtils.randomRange(24, 40)),
							new ItemStack(Material.GOLD_INGOT, MathUtils.randomRange(5, 8)));
			item.setAmount(item.getAmount() - 1);
			p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5F, 1F);
			p.getWorld().dropItemNaturally(p.getLocation(), drop);
		}
		if(state == GameState.PREPARING && PlayerManager.isPlaying(p)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void inventory(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if((state == GameState.VOTE || state == GameState.PREPARING) && PlayerManager.isPlaying(p)) {
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
	public void pluginDisable(PluginDisableEvent event) {
		if(UHC.playing) {
			UHC.endGame();
		}
	}

}
