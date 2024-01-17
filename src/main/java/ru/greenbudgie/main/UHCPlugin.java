package ru.greenbudgie.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.greenbudgie.UHC.*;
import ru.greenbudgie.artifact.ArtifactManager;
import ru.greenbudgie.classes.ClassManager;
import ru.greenbudgie.commands.*;
import ru.greenbudgie.drop.DropsPreviewInventory;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.items.CustomItemsListener;
import ru.greenbudgie.lobby.sign.SignManager;
import ru.greenbudgie.mutator.manager.InventoryBuilderMutator;
import ru.greenbudgie.nether.PiglinBarterManager;
import ru.greenbudgie.rating.InventoryBuilderRating;
import ru.greenbudgie.rating.Rating;
import ru.greenbudgie.requester.ItemRequester;
import ru.greenbudgie.tutorial.TutorialInventory;
import ru.greenbudgie.util.TaskManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UHCPlugin extends JavaPlugin {

	private static Logger log;
	public static UHCPlugin instance;

	public void onEnable() {
		instance = this;
		log = getLogger();
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
		registerCommand("class", new CommandClass());
		registerCommand("teammate", new CommandTeammate());
		registerCommand("worldtime", new CommandWorldTime());
		registerCommand("editarena", new CommandEditArena());
		registerCommand("requests", new CommandRequests());
		registerCommand("artifacts", new CommandArtifacts());
		registerCommand("watch", new CommandWatch());
		registerCommand("barters", new CommandBarters());
		registerCommand("drops", new CommandDrops());

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new UHC(), this);
		pm.registerEvents(new SignManager(), this);
		pm.registerEvents(new RecipeHandler(), this);
		pm.registerEvents(new CustomItemsListener(), this);
		pm.registerEvents(new ItemRequester(), this);
		pm.registerEvents(new ArtifactManager(), this);
		pm.registerEvents(new AutoOreSmelting(), this);
		pm.registerEvents(new SpectatorManager(), this);
		pm.registerEvents(new PlayerInventoryView(), this);
		pm.registerEvents(new SafeTeleport(), this);
		pm.registerEvents(new TutorialInventory(), this);
		pm.registerEvents(new PiglinBarterManager(), this);
		pm.registerEvents(new DropsPreviewInventory(), this);
		InventoryBuilderMutator.registerListener();
		InventoryBuilderRating.registerListener();

		UHC.init();
		CustomItems.init();
		ItemRequester.init();
		ArtifactManager.init();
		ClassManager.init();
		Rating.loadFromConfig();

		TaskManager.init();
	}
	
	private void registerCommand(String commandName, CommandExecutor executor) {
		PluginCommand command = this.getCommand(commandName);
		if(command != null) command.setExecutor(executor);
	}

	public void onDisable() {
		PlayerOptionHolder.saveOptions();
	}

	/**
	 * Sends an error message to every online OP player
	 */
	public static void error(String message) {
		log.log(Level.SEVERE, message);
		sendToOps(ChatColor.GRAY + "[" +
				ChatColor.DARK_RED + ChatColor.BOLD + "ERROR" +
				ChatColor.RESET + ChatColor.GRAY + "] " +
				ChatColor.WHITE + message);
	}

	/**
	 * Sends a warning message to every online OP player
	 */
	public static void warning(String message) {
		log.log(Level.WARNING, message);
		sendToOps(ChatColor.GRAY + "[" +
				ChatColor.GOLD + ChatColor.BOLD + "WARNING" +
				ChatColor.RESET + ChatColor.GRAY + "] " +
				ChatColor.WHITE + message);
	}

	/**
	 * Sends an informative message to every online OP player
	 */
	public static void info(String message) {
		log.log(Level.INFO, message);
		sendToOps(ChatColor.GRAY + "[" +
				ChatColor.WHITE + ChatColor.BOLD + "INFO" +
				ChatColor.RESET + ChatColor.GRAY + "] " +
				ChatColor.WHITE + message);
	}

	private static void sendToOps(String s) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.isOp()) {
				player.sendMessage(s);
			}
		}
	}

}
