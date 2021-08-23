package ru.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import ru.UHC.UHCPlayer;
import ru.UHC.WorldManager;
import ru.main.UHCPlugin;

public class CommandTest implements CommandExecutor {

	//Test command. Write here anything you want.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		ItemStack item = new ItemStack(Material.FILLED_MAP);
		MapView view = Bukkit.createMap(p.getWorld());
		UHCPlugin.log(view.getCenterX(), view.getCenterZ(), WorldManager.spawnLocation);

		MapMeta meta = (MapMeta) item.getItemMeta();
		meta.setMapView(view);
		item.setItemMeta(meta);
		p.getInventory().addItem(item);
		return true;
	}

	class CustomRenderer extends MapRenderer {

		@Override
		public void render(MapView map, MapCanvas canvas, Player player) {
			int x = map.getCenterX();
			int z = map.getCenterZ();

		}

	}

}
