package ru.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;
import ru.main.UHCPlugin;
import ru.rating.GameSummary;
import ru.rating.PlayerSummary;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class  CommandTest implements CommandExecutor {

	//Test command. Write here anything you want.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
//		GameSummary gameSummary = new GameSummary();
//		PlayerSummary player1 = new PlayerSummary("player1");
//		PlayerSummary player2 = new PlayerSummary("player2");
//		gameSummary.getPlayerSummaries().add(player1);
//		gameSummary.getPlayerSummaries().add(player2);
//
//		GameSummary gameSummary2 = new GameSummary();
//		PlayerSummary player3 = new PlayerSummary("player3");
//		PlayerSummary player4 = new PlayerSummary("player4");
//		gameSummary2.getPlayerSummaries().add(player3);
//		gameSummary2.getPlayerSummaries().add(player4);
//
//		List<Map<String, Object>> serizaliedGS = new ArrayList<>();
//		serizaliedGS.add(gameSummary.serialize());
//		serizaliedGS.add(gameSummary2.serialize());
//
//		YamlConfiguration config = new YamlConfiguration();
//		config.set("summaries", serizaliedGS);
//		try	{
//		config.save(new File(UHCPlugin.instance.getDataFolder() + File.separator + "rating.yml"));
//		} catch(Exception e) {}
//
//		List<GameSummary> gameSummaries = new ArrayList<>();
//
//		List<Map<?, ?>> serializedGameSummaryList = config.getMapList("summaries");
//		for(Map<?, ?> serializedSummary : serializedGameSummaryList) {
//			gameSummaries.add(GameSummary.deserialize((Map<String, Object>) serializedSummary));
//		}
//
//		p.sendMessage(gameSummaries.get(0).getPlayerSummaries().get(0).getPlayerName());
		return true;
	}
}
