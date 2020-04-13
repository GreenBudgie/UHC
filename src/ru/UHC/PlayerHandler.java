package ru.UHC;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import org.bukkit.*;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.mutator.MutatorManager;
import ru.pvparena.PvpArena;
import ru.util.InventoryHelper;
import ru.util.WorldHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerHandler implements Listener {

	private static List<Player> players = new ArrayList<>();
	private static List<Player> spectators = new ArrayList<>();
	private static Player lastLeft = null;

	public static boolean isPlaying(Player player) {
		return players.contains(player);
	}

	public static boolean isSpectating(Player player) {
		return spectators.contains(player);
	}

	public static boolean isInGame(Player player) {
		return isPlaying(player) || isSpectating(player);
	}

	public static List<Player> getPlayers() {
		return players;
	}

	public static List<Player> getSpectators() {
		return spectators;
	}

	public static List<Player> getInGamePlayers() {
		return Streams.concat(players.stream(), spectators.stream()).collect(Collectors.toList());
	}

	public static void registerPlayers(List<Player> players) {
		PlayerHandler.players = Lists.newArrayList(players);
	}

	public static void removePlayer(Player player) {
		players.remove(player);
	}

	public static void joinSpectator(Player spectator) {
		spectators.add(spectator);
		spectator.setGameMode(GameMode.SPECTATOR);
		spectator.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
	}

	public static void leaveSpectator(Player spectator) {
		spectators.remove(spectator);
	}

	public static void clearLists() {
		players.clear();
		spectators.clear();
	}

	public static void heal(Player player) {
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		player.setHealth(20);
		player.setSaturation(20);
		player.setExhaustion(20);
		player.setFoodLevel(20);
	}
	
	public static void resetPlayer(Player player) {
		heal(player);
		player.getInventory().clear();
		player.getActivePotionEffects().forEach(ef -> player.removePotionEffect(ef.getType()));
		player.setFireTicks(0);
		player.setNoDamageTicks(0);
		player.setExp(0);
		player.setLevel(0);
	}

	public static boolean isInLobby(Player player) {
		return WorldManager.getLobby().getPlayers().contains(player);
	}

	public static void handleDeath(Player player) {

	}

	public static String getDeathMessage(Player p, EntityDamageEvent.DamageCause cause) {
		String name = ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " ";
		switch(cause) {
		case CONTACT:
			return name + "умер от кактуса, серьезно?";
		case ENTITY_ATTACK:
			return name + "замачили";
		case ENTITY_SWEEP_ATTACK:
			return name + "умер от свайпа, бля...";
		case PROJECTILE:
			return name + "застрелили";
		case SUFFOCATION:
			return name + "задохнулся в стене";
		case FALL:
			return name + "позорно разбился";
		case FIRE:
		case FIRE_TICK:
			return name + "сгорел";
		case MELTING:
			return name + "расплавился";
		case LAVA:
			return name + "трагически сгорел в лаве";
		case DROWNING:
			return name + "позорно захлебнулся";
		case BLOCK_EXPLOSION:
			return name + "взорвался";
		case ENTITY_EXPLOSION:
			return name + "взорвался";
		case VOID:
			return name + "выпал из мира";
		case LIGHTNING:
			return name + "может купить лотерейный билет";
		case SUICIDE:
			return name + "суициднулся";
		case STARVATION:
			return name + "умер от голода";
		case POISON:
			return name + "отравился";
		case MAGIC:
			return name + "взорвался";
		case WITHER:
			return name + "высох";
		case FALLING_BLOCK:
			return name + "расплющился";
		case THORNS:
			return name + "напал на жесткого челика с чаром на шипы";
		case DRAGON_BREATH:
			return name + "хз как но умер от дракона";
		case CUSTOM:
			return name + "погиб";
		case FLY_INTO_WALL:
			return name + "влетел в стену";
		case HOT_FLOOR:
			return name + "поджарился на магме";
		case CRAMMING:
			return name + "раздавили мобы";
		case DRYOUT:
			return name + "высох";
		}
		return name + "сдох";
	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		Player player = e.getEntity();
		if(isPlaying(player)) {
			Player killer = FightHelper.getKiller(player);
			if(killer != null) {
				player.getWorld().dropItem(player.getLocation(), UHC.getBonusShell());
				UHC.addPoints(killer, 10);
				UHC.increaseStat(killer, PlayerStat.KILLS, 1);
				for(Player pl : getInGamePlayers()) {
					pl.sendMessage(FightHelper.getDeathMessage(player));
				}
				player.sendTitle(ChatColor.DARK_RED + "Тебя замачили!", "", 10, 60, 20);
			} else {
				boolean golden = GameState.OUTBREAK.isActive();
				ItemStack apple = InventoryHelper.generateItemWithName(golden ? Material.GOLDEN_APPLE : Material.APPLE,
						golden ? (ChatColor.DARK_GREEN + "Золотое бонусное яблоко") : (ChatColor.GREEN + "Бонусное яблоко"), !golden);
				InventoryHelper.setValue(apple, ChatColor.YELLOW + "Владелец", ChatColor.GOLD + player.getName(), false);
				player.getWorld().dropItem(player.getLocation(), apple);
				if(player.getLastDamageCause() != null && lastLeft != player) {
					for(Player pl : getInGamePlayers()) {
						pl.sendMessage(getDeathMessage(player, player.getLastDamageCause().getCause()));
					}
				}
				lastLeft = null;
				player.sendTitle(ChatColor.DARK_RED + "Ты погиб!", "", 10, 60, 20);
			}
			List<Player> teammates = PlayerTeams.getTeammates(player);
			if(PlayerTeams.getTeams().size() == 3 && teammates.stream().noneMatch(PlayerHandler::isPlaying)) {
				String info = ChatColor.YELLOW + "Ты занял " + ChatColor.BOLD + ChatColor.AQUA + "третье" + ChatColor.RESET + ChatColor.YELLOW + " место!";
				UHC.addPoints(player, teammates.isEmpty() ? 30 : 20);
				player.sendMessage(info);
				for(Player teammate : teammates) {
					UHC.addPoints(teammate, 20);
					teammate.sendMessage(info);
				}
			}
			if(PlayerTeams.getTeams().size() == 2 && teammates.stream().noneMatch(PlayerHandler::isPlaying)) {
				String info = ChatColor.YELLOW + "Ты занял " + ChatColor.BOLD + ChatColor.AQUA + "второе" + ChatColor.RESET + ChatColor.YELLOW + " место!";
				UHC.addPoints(player, teammates.isEmpty() ? 40 : 25);
				player.sendMessage(info);
				for(Player teammate : teammates) {
					UHC.addPoints(teammate, 25);
					teammate.sendMessage(info);
				}
			}

			player.getWorld().strikeLightningEffect(player.getLocation());
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, Float.MAX_VALUE, 0.8F);
		}
	}

	@EventHandler
	public void joinServer(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Player player = e.getPlayer();
		player.setGameMode(GameMode.ADVENTURE);
		resetPlayer(player);
		PlayerStat.defaultStats(player);
		String msg = ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " присоединился";
		for(Player pl : WorldManager.getLobby().getPlayers()) {
			pl.sendMessage(msg);
		}
		player.sendMessage(msg);
		if(!isInLobby(player)) player.teleport(WorldManager.getLobby().getSpawnLocation());
		if(GameState.isPlaying()) {
			UHC.refreshGameScoreboardLater();
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Сейчас идет игра! " + ChatColor.RESET + ChatColor.AQUA
					+ "За игрой можно наблюдать, нажав по табличке на стене.");
		}
		UHC.refreshLobbyScoreboardLater();
	}

	@EventHandler
	public void quitServer(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		Player player = e.getPlayer();
		if(isInGame(player)) {
			for(Player pl : getInGamePlayers()) {
				if(isSpectating(player)) {
					pl.sendMessage(ChatColor.AQUA + "Наблюдатель " + ChatColor.GOLD + player.getName() + ChatColor.AQUA + " отключился");
				} else {
					if(GameState.isPreGame()) {
						pl.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.DARK_RED + ChatColor.BOLD + " вылетел с сервера");
						pl.getWorld().playSound(pl.getLocation(), Sound.ENTITY_PLAYER_BURP, 2F, 1F);
						WorldHelper.spawnParticlesAround(player, Particle.SMOKE_NORMAL, null, 20);
					} else {
						pl.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.RED + " вышел из игры");
					}
				}
			}
			if(isPlaying(player)) {
				if(GameState.isPreGame()) {
					removePlayerInfoOnLeave(player);
					lastLeft = player;
				} else {
					player.setHealth(0); //Make player dead
				}
			}
			if(MutatorManager.meetingPlace.isActive()) {
				MutatorManager.meetingPlace.bar.removePlayer(player);
			}
			if(MutatorManager.oxygen.isActive()) {
				MutatorManager.oxygen.unregister(player);
			}
		} else {
			updateTeams();
			leaveTeam(player);
			for(Player pl : WorldManager.getLobby().getPlayers()) {
				pl.sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " отключился");
			}
			PvpArena.onArena.remove(player);
			refreshLobbyScoreboardLater();
		}
	}

}
