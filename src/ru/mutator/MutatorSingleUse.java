package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import ru.util.InventoryHelper;

public class MutatorSingleUse extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.GOLDEN_SHOVEL;
	}

	@Override
	public String getName() {
		return "Одноразовое Оружие";
	}

	@Override
	public String getDescription() {
		return "Мечи и топоры ломаются с одного удара";
	}

	@EventHandler
	public void damage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();
			ItemStack item = p.getInventory().getItemInMainHand();
			if(InventoryHelper.isSword(item.getType()) || InventoryHelper.isAxe(item.getType())) {
				item.setAmount(0);
				p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5F, 1F);
			}
		}
	}


}
