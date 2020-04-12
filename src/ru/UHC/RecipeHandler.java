package ru.UHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import ru.main.UHCPlugin;
import ru.util.InventoryHelper;

public class RecipeHandler implements Listener {

	public static void init() {
		ShapedRecipe apple = new ShapedRecipe(new NamespacedKey(UHCPlugin.instance, "goldenApple"), new ItemStack(Material.GOLDEN_APPLE, 2));
		apple.shape("ggg", "gsg", "ggg");
		apple.setIngredient('g', Material.GOLD_INGOT);
		apple.setIngredient('s', Material.SHULKER_SHELL);
		Bukkit.addRecipe(apple);
	}

}
