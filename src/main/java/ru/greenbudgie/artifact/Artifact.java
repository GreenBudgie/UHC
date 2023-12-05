package ru.greenbudgie.artifact;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.ItemUtils;
import ru.greenbudgie.util.NumericalCases;

import javax.annotation.Nullable;

import static org.bukkit.ChatColor.*;

public abstract class Artifact {

	private float currentPrice = getStartingPrice();

	public Artifact() {
		ArtifactManager.artifacts.add(this);
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract int getStartingPrice();

	public int getCurrentPrice() {
		return Math.round(currentPrice);
	}

	public void resetPrice() {
		currentPrice = getStartingPrice();
	}

	public void update() {
	}

	public abstract float getPriceIncreaseAmount();

	public abstract boolean onUse(@Nullable Player player);

	public boolean use(@Nullable Player player) {
		if(player != null) {
			if(onUse(player)) {
				for(Player receiver : PlayerManager.getInGamePlayersAndSpectators()) {
					receiver.sendMessage(GOLD + player.getName() + YELLOW + " призвал силу артефакта " + BOLD + DARK_RED + getName());
					if(receiver != player) {
						receiver.playSound(receiver.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.5F, 0.5F);
					}
				}
				currentPrice += getPriceIncreaseAmount();
				return true;
			} else {
				player.sendMessage(DARK_RED + "" + BOLD + "Невозможно активировать этот артефакт!");
			}
		} else {
			if(onUse(null)) {
				for(Player receiver : PlayerManager.getInGamePlayersAndSpectators()) {
					receiver.sendMessage(YELLOW + "Призвана сила артефакта " + BOLD + DARK_RED + getName());
					receiver.playSound(receiver.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.5F, 0.5F);
				}
				return true;
			}
		}
		return false;
	}

	public abstract Material getType();

	public ItemStack getItem() {
		return ItemUtils.builder(getType()).withName(getName()).withSplittedLore(YELLOW + getDescription()).withLore(getPriceString()).build();
	}

	private String getPriceString() {
		NumericalCases cases = new NumericalCases("артефакт", "артефакта", "артефактов");
		int starting = getStartingPrice();
		int current = getCurrentPrice();
		String added = "";
		if(starting != current) {
			added = GRAY + " (" + DARK_AQUA + "+" + (current - starting) + GRAY + ")";
		}
		return AQUA + "" + BOLD + current + RESET + GOLD + " " + cases.byNumber(current) + added;
	}

	public ItemStack getItemFor(Player p) {
		ItemStack artifact = getItem();
		if(ArtifactManager.getArtifactCount(p) >= getCurrentPrice()) {
			ItemUtils.addGlow(artifact);
			ItemUtils.addLore(artifact, false, GREEN + "" + BOLD + "Нажми, чтобы призвать");
		} else {
			ItemUtils.addLore(artifact, false, DARK_RED + "" + BOLD + "Недостаточно артефактов");
		}
		return artifact;
	}

}
