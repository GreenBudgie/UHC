package ru.greenbudgie.mutator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.MathUtils;

import java.util.HashSet;

public abstract class Mutator {

	private boolean isHidden = false;

	public Mutator() {
		MutatorManager.mutators.add(this);
	}

	public void hide() {
		if(canBeHidden() && MathUtils.chance(20)) {
			isHidden = true;
		}
	}

	public boolean isPreferredBy(String name) {
		return MutatorManager.preferredMutators.getOrDefault(name, new HashSet<>()).contains(this);
	}

	public String getConfigName() {
		return this.getClass().getSimpleName();
	}

	public boolean isHidden() {
		return isHidden;
	}

	public abstract ThreatStatus getThreatStatus();

	public abstract Material getItemToShow();

	public abstract String getName();

	public abstract String getDescription();

	public boolean canBeAddedFromArtifact() {
		return true;
	}

	public boolean canBeDeactivatedByArtifact() { return true; }

	public boolean canBeHidden() {
		return true;
	}

	public boolean conflictsWithClasses() {
		return false;
	}

	/**
	 * Whether this mutator can be randomly chosen even if arena is closed.
	 * Ex: interactive arena cannot
	 */
	public boolean canWorkIfArenaIsClosed() {
		return true;
	}

	public final void activate(boolean applyHiding, String preference) {
		MutatorManager.activeMutators.add(this);
		if(applyHiding) hide();
		for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
			if(!isHidden && preference != null) {
				p.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Предпочтение " + ChatColor.GOLD + preference + ChatColor.DARK_GRAY + ":");
			}
			p.sendMessage(getInfo());
		}
		if(this instanceof Listener) {
			Bukkit.getPluginManager().registerEvents((Listener) this, UHCPlugin.instance);
		}
		onChoose();
	}

	public final boolean isActive() {
		return MutatorManager.isActive(this);
	}

	public void onDeactivate() {
	}

	public final void deactivate() {
		onDeactivate();
		if(this instanceof Listener) {
			HandlerList.unregisterAll((Listener) this);
		}
		MutatorManager.activeMutators.remove(this);
		isHidden = false;
	}

	public final String getInfo() {
		if(isHidden) {
			return ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Скрытый Мутатор" + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_PURPLE + "Нет информации";
		} else {
			return ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Мутатор" + ChatColor.DARK_GRAY + "] " + ChatColor.LIGHT_PURPLE + getName() + ChatColor.GRAY + ": "
					+ ChatColor.YELLOW + getDescription();
		}
	}

	public void onChoose() {
	}

	public void update() {
	}

	public boolean conflictsWith(Mutator another) {
		return this.containsBossBar() && another.containsBossBar();
	}

	/**
	 * Whether this mutator contains a boss bar.
	 * Usually used to prevent multiple mutators with boss bars to appear.
	 */
	public boolean containsBossBar() {
		return false;
	}

}