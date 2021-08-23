package ru.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.UHC.RecipeHandler;
import ru.lobby.LobbyGameManager;
import ru.lobby.sign.SignManager;
import ru.UHC.UHC;
import ru.artifact.ArtifactManager;
import ru.classes.ClassManager;
import ru.commands.*;
import ru.items.CustomItems;
import ru.items.CustomItemsListener;
import ru.mutator.InventoryBuilderMutator;
import ru.rating.InventoryBuilderRating;
import ru.rating.Rating;
import ru.requester.ItemRequester;
import ru.util.TaskManager;

public class UHCPlugin extends JavaPlugin {

	public static UHCPlugin instance;

	public void onEnable() {
		instance = this;
		registerCommand("test", new CommandTest());
		registerCommand("gm", new CommandGM());
		registerCommand("start", new CommandStart());
		registerCommand("end", new CommandEnd());
		registerCommand("lobby", new CommandLobby());
		registerCommand("arena", new CommandArena());
		registerCommand("skip", new CommandSkip());
		registerCommand("map", new CommandMap());
		registerCommand("rating", new CommandRating());
		registerCommand("drop", new CommandDrop());
		registerCommand("customitem", new CommandCustomItem());
		registerCommand("timer", new CommandTimer());
		registerCommand("mutator", new CommandMutator());
		registerCommand("optmutator", new CommandOptMutator());
		registerCommand("options", new CommandOptions());
		registerCommand("inv", new CommandInv());
		registerCommand("class", new CommandClass());
		registerCommand("teammate", new CommandTeammate());

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new UHC(), this);
		pm.registerEvents(new SignManager(), this);
		pm.registerEvents(new RecipeHandler(), this);
		pm.registerEvents(new CustomItemsListener(), this);
		pm.registerEvents(new ItemRequester(), this);
		pm.registerEvents(new ArtifactManager(), this);
		InventoryBuilderMutator.registerListener();
		InventoryBuilderRating.registerListener();

		UHC.init();
		CustomItems.init();
		ItemRequester.init();
		ClassManager.init();
		Rating.loadFromConfig();

		TaskManager.init();
	}
	
	private void registerCommand(String commandName, CommandExecutor executor) {
		PluginCommand command = this.getCommand(commandName);
		if(command != null) command.setExecutor(executor);
	}

	public void onDisable() {
		if(UHC.playing) {
			UHC.endGame();
		}
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(LobbyGameManager.PVP_ARENA.isOnArena(player)) {
				player.teleport(LobbyGameManager.PVP_ARENA.getSpawnLocation());
				LobbyGameManager.PVP_ARENA.onArenaLeave(player);
			}
		}
		if(!LobbyGameManager.PVP_ARENA.isOpen()) {
			LobbyGameManager.PVP_ARENA.openArena();
		}
	}

	/**
	 * Sends an error message to every online OP player
	 */
	public static void error(String s) {
		sendToOps(ChatColor.GRAY + "[" +
				ChatColor.DARK_RED + ChatColor.BOLD + "ERROR" +
				ChatColor.RESET + ChatColor.GRAY + "] " +
				ChatColor.WHITE + s);
	}

	/**
	 * Sends a warning message to every online OP player
	 */
	public static void warning(String s) {
		sendToOps(ChatColor.GRAY + "[" +
				ChatColor.GOLD + ChatColor.BOLD + "WARNING" +
				ChatColor.RESET + ChatColor.GRAY + "] " +
				ChatColor.WHITE + s);
	}

	/**
	 * Sends an informative message to every online OP player
	 */
	public static void info(String s) {
		sendToOps(ChatColor.GRAY + "[" +
				ChatColor.WHITE + ChatColor.BOLD + "INFO" +
				ChatColor.RESET + ChatColor.GRAY + "] " +
				ChatColor.WHITE + s);
	}

	public static void sendToOps(String s) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.isOp()) {
				player.sendMessage(s);
			}
		}
	}

	public static void log(Object... toLog) {
		for(Object obj : toLog) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(obj == null) obj = "null";
				player.sendMessage(obj.toString());
			}
		}
	}

	public static void log() {
		log("log");
	}

}
