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

	public Artifact() {
		ArtifactManager.artifacts.add(this);
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract int getPrice();

	public void update() {
	}

	public abstract void onUse(Player p);

	public void use(Player p) {
		for(Player receiver : UHC.getInGamePlayers()) {
			receiver.sendMessage(ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " призвал силу артефакта " + ChatColor.BOLD + ChatColor.DARK_RED + getName());
			if(receiver != p) {
				receiver.playSound(receiver.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.5F, 0.5F);
			}
		}
		onUse(p);
	}

	public abstract Material getType();

	public ItemStack getItem() {
		return ItemUtils.builder(getType()).withName(getName()).withSplittedLore(ChatColor.YELLOW + getDescription()).withLore(getPriceString()).build();
	}

	private String getPriceString() {
		NumericalCases cases = new NumericalCases("артефакт", "артефакта", "артефактов");
		return ChatColor.AQUA + "" + getPrice() + ChatColor.GOLD + " " + cases.byNumber(getPrice());
	}

	public ItemStack getItemFor(Player p) {
		ItemStack artifact = getItem();
		if(ArtifactManager.getArtifactCount(p) >= getPrice()) {
			ItemUtils.addGlow(artifact);
			ItemUtils.addLore(artifact, false, ChatColor.GREEN + "Нажми, чтобы призвать");
		} else {
			ItemUtils.addLore(artifact, false, ChatColor.RED + "Недостаточно артефактов");
		}
		return artifact;
	}

}
