package ru.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;
import ru.UHC.UHC;
import ru.lobby.LobbyGameManager;
import ru.main.UHCPlugin;
import ru.rating.GameSummary;
import ru.rating.PlayerSummary;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class  CommandTest implements CommandExecutor {

	//Test command. Write here anything you want.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		if(LobbyGameManager.PVP_ARENA.isOpen()) {
			LobbyGameManager.PVP_ARENA.closeArena();
		} else {
			LobbyGameManager.PVP_ARENA.openArena();
		}
		return true;
	}
}
