package ru.greenbudgie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.mutator.trolling.MutatorTrolling;
import ru.greenbudgie.mutator.trolling.TrollingEvent;
import ru.greenbudgie.util.MathUtils;

import java.util.List;

public class CommandTrolling implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.isOp()) {
			return true;
		}
		if (!UHC.playing) {
			return true;
		}
		if (args.length == 0) {
			return true;
		}
		String eventName = args[0];
		TrollingEvent event = MutatorTrolling.events.stream()
				.filter(currentEvent -> currentEvent.getClass().getSimpleName().equals(eventName))
				.findFirst()
				.orElseThrow();
		event.execute();
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.isOp()) {
			return null;
		}
		if (args.length > 1) {
			return null;
		}
		List<String> eventNames = MutatorTrolling.events.stream()
				.map(event -> event.getClass().getSimpleName())
				.toList();
		return MathUtils.getListOfStringsMatchingLastWord(args, eventNames);
	}
}
