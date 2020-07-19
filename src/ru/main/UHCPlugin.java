package ru.main;

import de.slikey.effectlib.EffectManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.artifact.ArtifactManager;
import ru.UHC.RecipeHandler;
import ru.UHC.SignManager;
import ru.UHC.UHC;
import ru.commands.*;
import ru.items.CustomItems;
import ru.items.CustomItemsListener;
import ru.mutator.MutatorManager;
import ru.pvparena.PvpArena;
import ru.requester.ItemRequester;
import ru.util.TaskManager;

import java.util.function.Consumer;

public class UHCPlugin extends JavaPlugin {

	public static UHCPlugin instance;
	public static EffectManager em;

	public void onEnable() {
		instance = this;
		em = new EffectManager(this);
		this.getCommand("test").setExecutor(new CommandTest());
		this.getCommand("gm").setExecutor(new CommandGM());
		this.getCommand("ping").setExecutor(new CommandPing());
		this.getCommand("start").setExecutor(new CommandStart());
		this.getCommand("end").setExecutor(new CommandEnd());
		this.getCommand("lobby").setExecutor(new CommandLobby());
		this.getCommand("arena").setExecutor(new CommandArena());
		this.getCommand("skip").setExecutor(new CommandSkip());
		this.getCommand("map").setExecutor(new CommandMap());
		this.getCommand("stat").setExecutor(new CommandStat());
		this.getCommand("rating").setExecutor(new CommandRating());
		this.getCommand("drop").setExecutor(new CommandDrop());
		this.getCommand("customitem").setExecutor(new CommandCustomItem());
		this.getCommand("timer").setExecutor(new CommandTimer());
		this.getCommand("mutator").setExecutor(new CommandMutator());
		this.getCommand("optmutator").setExecutor(new CommandOptMutator());
		this.getCommand("options").setExecutor(new CommandOptions());

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new UHC(), this);
		pm.registerEvents(new SignManager(), this);
		pm.registerEvents(new RecipeHandler(), this);
		pm.registerEvents(new CustomItemsListener(), this);
		pm.registerEvents(new ItemRequester(), this);
		pm.registerEvents(new ArtifactManager(), this);
		pm.registerEvents(new MutatorManager(), this);
		pm.registerEvents(new PvpArena(), this);

		TaskManager.init();
		UHC.init();
		CustomItems.init();
		ItemRequester.init();
	}

	public void onDisable() {
		if(UHC.playing) {
			UHC.endGame();
		}
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.getInventory().clear();
			p.updateInventory();
			if(PvpArena.isOnArena(p)) {
				p.teleport(PvpArena.arenaSpawnLocation);
				PvpArena.onArenaLeave(p);
			}
		}
		if(!PvpArena.isOpen) {
			PvpArena.openArena();
		}
		em.dispose();
	}

	public static void log(Object s) {
		Bukkit.broadcastMessage(s.toString());
	}

	public static void log() {
		log("log");
	}

}
