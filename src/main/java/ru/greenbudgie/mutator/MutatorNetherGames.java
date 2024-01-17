package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.GameStartPlatformManager;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.event.GameStartEvent;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.PotionEffectBuilder;
import ru.greenbudgie.util.item.Enchant;
import ru.greenbudgie.util.item.ItemUtils;

import static org.bukkit.ChatColor.*;

public class MutatorNetherGames extends Mutator implements Listener {

	private static final ItemStack WOODEN_PICKAXE = ItemUtils.builder(Material.WOODEN_PICKAXE)
			.unbreakable()
			.withEnchantments(new Enchant(Enchantment.DIG_SPEED, 1))
			.build();

	@Override
	public Material getItemToShow() {
		return Material.NETHERRACK;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Адские Игры";
	}

	@Override
	public String getDescription() {
		return "Игра начинается в аду! В начале игры всем на одну минуту выдается огнестойкость. Не забывай, что можно торговать с пиглинами: /barters.";
	}

	@Override
	public boolean canBeAddedFromArtifact() {
		return false;
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	@Override
	public boolean canBeDeactivatedByArtifact() {
		return false;
	}

	@Override
	public void onChoose() {
		if (!UHC.state.isPreGame()) {
			for (Player player : PlayerManager.getAliveOnlinePlayers()) {
				player.sendMessage(RED + "Мутатор Адские Игры не имеет эффекта при активации во время игры, только до нее!");
			}
			return;
		}
		GameStartPlatformManager.createNetherPlatformAndTeleportPlayers();
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		for (Player player : PlayerManager.getAliveOnlinePlayers()) {
			player.getInventory().addItem(WOODEN_PICKAXE);
			player.addPotionEffect(
					new PotionEffectBuilder(PotionEffectType.FIRE_RESISTANCE).minutes(1).build()
			);
		}
	}

}
