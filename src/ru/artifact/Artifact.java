package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.UHC.UHC;
import ru.util.ItemUtils;
import ru.util.NumericalCases;

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

	public abstract void onUse(Player p);

	public void use(Player p) {
		for(Player receiver : UHC.getInGamePlayers()) {
			receiver.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " призвал силу артефакта " + ChatColor.BOLD + ChatColor.DARK_RED + getName());
			if(receiver != p) {
				receiver.playSound(receiver.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.5F, 0.5F);
			}
		}
		onUse(p);
		currentPrice += getPriceIncreaseAmount();
	}

	public abstract Material getType();

	public ItemStack getItem() {
		return ItemUtils.builder(getType()).withName(getName()).withSplittedLore(ChatColor.YELLOW + getDescription()).withLore(getPriceString()).build();
	}

	private String getPriceString() {
		NumericalCases cases = new NumericalCases("артефакт", "артефакта", "артефактов");
		int starting = getStartingPrice();
		int current = getCurrentPrice();
		String added = "";
		if(starting != current) {
			added = ChatColor.GRAY + " (" + ChatColor.DARK_AQUA + "+" + (current - starting) + ChatColor.GRAY + ")";
		}
		return ChatColor.AQUA + "" + ChatColor.BOLD + current + ChatColor.RESET + ChatColor.GOLD + " " + cases.byNumber(current) + added;
	}

	public ItemStack getItemFor(Player p) {
		ItemStack artifact = getItem();
		if(ArtifactManager.getArtifactCount(p) >= getCurrentPrice()) {
			ItemUtils.addGlow(artifact);
			ItemUtils.addLore(artifact, false, ChatColor.GREEN + "Нажми, чтобы призвать");
		} else {
			ItemUtils.addLore(artifact, false, ChatColor.RED + "Недостаточно артефактов");
		}
		return artifact;
	}

}
