package ru.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import ru.main.UHCPlugin;

import java.io.File;
import java.io.IOException;

public class  CommandTest implements CommandExecutor {

	//Test command. Write here anything you want.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		String path = Bukkit.getWorldContainer().getAbsolutePath();
		File temp = new File(path + "\\Arena\\.Temp");
		p.sendMessage(temp.getAbsolutePath());
		return true;
	}
}
