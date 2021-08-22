package ru.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.main.UHCPlugin;

public class  CommandTest implements CommandExecutor {

	//Test command. Write here anything you want.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		Location l1 = p.getLocation();
		Location l2 = p.getWorld().getSpawnLocation();
		double x1 = l1.getX();
		double z1 = l1.getZ();
		double x2 = l2.getX();
		double z2 = l2.getZ();
		double playerLookAngle = l1.getYaw();
		playerLookAngle = playerLookAngle % 360.0D;
		double teammateAngle = Math.atan2(z2 - z1, x2 - x1);
		double finalAngle = (Math.PI - (Math.toRadians(playerLookAngle - 90.0D) - teammateAngle)) % (Math.PI * 2);
		if(finalAngle < 0) finalAngle = Math.PI / 2 + finalAngle;
		char[] arrows = new char[] {'\u2191', '\u2B08', '\u2192', '\u2B0A', '\u2193', '\u2B0B', '\u2190', '\u2B09', '\u2191'};
		double step = Math.PI / 4;
		double range = Math.PI / 8;
		char arrow = ' ';
		for(int i = 0; i < arrows.length; i++) {
			double currentAngle = i * step;
			if(inRange(finalAngle, currentAngle - range, currentAngle + range)) {
				arrow = arrows[i];
			}
		}
		UHCPlugin.log(arrow + " " + Math.toDegrees(finalAngle));
		return true;
	}

	private boolean inRange(double num, double min, double max) {
		return num >= min && num <= max;
	}
}
