package ru.greenbudgie.artifact;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.ArenaManager;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.util.ItemUtils;
import ru.greenbudgie.util.NumericalCases;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.bukkit.ChatColor.*;

public abstract class Artifact {

	private static final NumericalCases artifactCases = new NumericalCases(
			"артефакт",
			"артефакта",
			"артефактов"
	);
	public static final String ARTIFACT_CHAT_COLOR = RED + "" + BOLD;
	public static final String ARTIFACT_SYMBOL = DARK_RED + "" + BOLD + "⚡";

	private float currentPrice = getStartingPrice();

	public Artifact() {
		ArtifactManager.artifacts.add(this);
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract int getStartingPrice();

	public boolean canBeUsedOnArena() {
		return true;
	}

	public boolean canBeUsedOnClosedArena() {
		return true;
	}

	public boolean canBeUsedByMutator() {
		return true;
	}

	public int getCurrentPrice() {
		return (int) Math.floor(currentPrice);
	}

	public void resetPrice() {
		currentPrice = getStartingPrice();
	}

	public void update() {
	}

	public abstract float getPriceIncreaseAmount();

	public abstract boolean onUse(@Nullable Player player);

	public boolean canUse(@Nonnull Player player) {
		boolean enoughArtifacts = ArtifactManager.getArtifactCount(player) >= getCurrentPrice();
		if (!enoughArtifacts) {
			return false;
		}
		if (UHC.state.isBeforeDeathmatch()) {
			return true;
		}
		if (!canBeUsedOnArena()) {
			return false;
		}
		if (canBeUsedOnClosedArena()) {
			return true;
		}
		return ArenaManager.getCurrentArena().isOpen();
	}

	public boolean use(@Nullable Player player) {
		if(player != null) {
			if(onUse(player)) {
				for(Player receiver : PlayerManager.getInGamePlayersAndSpectators()) {
					String message = padSymbols(GOLD + player.getName() + GRAY + " призвал артефакт " + ARTIFACT_CHAT_COLOR + getName());
					receiver.sendMessage(message);
					if(receiver != player) {
						receiver.playSound(receiver.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.5F, 0.5F);
					}
				}
				currentPrice += getPriceIncreaseAmount();
				return true;
			} else {
				player.sendMessage(DARK_RED + "" + BOLD + "Невозможно призвать этот артефакт!");
			}
		} else {
			if(onUse(null)) {
				for(Player receiver : PlayerManager.getInGamePlayersAndSpectators()) {
					String message = padSymbols(GRAY + "Призвана сила артефакта " + ARTIFACT_CHAT_COLOR + getName());
					receiver.sendMessage(message);
					receiver.playSound(receiver.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.5F, 0.5F);
				}
				return true;
			}
		}
		return false;
	}

	public abstract Material getType();

	public ItemStack getItem() {
		ItemStack item = ItemUtils.builder(getType())
				.withName(ARTIFACT_CHAT_COLOR + getName())
				.withSplittedLore(GRAY + getDescription())
				.withLore(getCanBeUsedOnArenaString())
				.withLore(getPriceString())
				.build();
		if (getPriceIncreaseAmount() == 0) {
			return item;
		}
		ItemUtils.addLore(item, getPriceIncreaseString());
		return item;
	}

	private String getPriceString() {
		int starting = getStartingPrice();
		int current = getCurrentPrice();
		String added = "";
		if(starting != current) {
			added = GRAY + " (" + DARK_AQUA + "+" + (current - starting) + GRAY + ")";
		}
		return AQUA + "" + BOLD + current + RESET + RED + " " + artifactCases.byNumber(current) + added;
	}

	private String getCanBeUsedOnArenaString() {
		if (canBeUsedOnArena()) {
			if (!canBeUsedOnClosedArena()) {
				return DARK_GREEN + "" + ITALIC + "Может быть использован на открытой арене!";
			}
			return DARK_GREEN + "" + ITALIC + "Может быть использован на арене!";
		}
		return GOLD + "" + ITALIC + "Не может быть использован на арене";
	}

	private String getPriceIncreaseString() {
		float priceIncrease = getPriceIncreaseAmount();
		String formattedPriceIncrease;
		if(priceIncrease == (int) priceIncrease) {
			formattedPriceIncrease = String.format("%d", (int) priceIncrease);
		} else {
			formattedPriceIncrease = String.format("%s", priceIncrease);
		}
		return GRAY + "+" + AQUA + BOLD + formattedPriceIncrease + GOLD + " к цене за использование";
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

	public static String padSymbols(String input) {
		return ARTIFACT_SYMBOL + " " + RESET + input + " " + ARTIFACT_SYMBOL;
	}

}
