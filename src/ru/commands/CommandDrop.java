package ru.commands;

import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.UHC.Drops;
import ru.UHC.UHC;
import ru.util.MathUtils;

import java.util.List;

public class CommandDrop implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp() || !UHC.playing) return true;
		Player p = (Player) sender;
		if(args.length == 2) {
			boolean air = args[0].equalsIgnoreCase("air");
			if(args[1].equalsIgnoreCase("reset")) {
				if(air) Drops.setupAirdrop(); else Drops.setupCavedrop();
			}
			if(args[1].equalsIgnoreCase("drop")) {
				if(air) Drops.airdropTimer = 5; else Drops.cavedropTimer = 5;
			}
			if(args[1].equalsIgnoreCase("changeloc")) {
				if(air) Drops.chooseAirdropLocation(); else Drops.chooseCavedropLocation();
			}
			if(args[1].equalsIgnoreCase("currentloc")) {
				if(air) Drops.airdropLocation = p.getLocation(); else Drops.cavedropLocation = p.getLocation();
			}
			if(args[1].equalsIgnoreCase("tp")) {
				if(air)	p.teleport(Drops.airdropLocation); else p.teleport(Drops.cavedropLocation);
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, Lists.newArrayList("air", "cave"));
		}
		if(args.length == 2) {
			return MathUtils.getListOfStringsMatchingLastWord(args, Lists.newArrayList("reset", "drop", "changeloc", "currentloc", "tp"));
		}
		return null;
	}
}
