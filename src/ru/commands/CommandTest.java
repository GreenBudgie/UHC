package ru.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import ru.main.UHCPlugin;
import ru.util.ParticleUtils;

import java.io.File;
import java.io.IOException;

public class  CommandTest implements CommandExecutor {

	//Test command. Write here anything you want.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		ParticleUtils.createCircle(p.getLocation(), Particle.CLOUD, null, 10, 100);
		return true;
	}
}
