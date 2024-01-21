package ru.greenbudgie.mutator.base;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.mutator.manager.MutatorManager;
import ru.greenbudgie.util.MathUtils;

import javax.annotation.Nullable;

import static org.bukkit.ChatColor.*;

public abstract class Mutator {

	public static final String MUTATOR_NAME_COLOR = LIGHT_PURPLE + "" + BOLD;

	private boolean isHidden = false;
	private boolean isActive = false;

	public Mutator() {
		MutatorManager.mutators.add(this);
	}

	public void hide() {
		if(canBeHidden() && MathUtils.chance(20)) {
			isHidden = true;
		}
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
	 * Whether this mutator can be randomly chosen even if arena is closed
	 */
	public boolean canWorkIfArenaIsClosed() {
		return true;
	}

	public boolean isDuoOnly() {
		return false;
	}

	public final void activate(boolean applyHiding, @Nullable String preferenceInfo) {
		isActive = true;
		MutatorManager.activeMutators.add(this);
		if(applyHiding) hide();
		for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
			if(!isHidden && preferenceInfo != null) {
				p.sendMessage(DARK_PURPLE + "" + BOLD + "Предпочтение " + GOLD + preferenceInfo + DARK_GRAY + ":");
			}
			p.sendMessage(getInfo());
		}
		if(this instanceof Listener) {
			Bukkit.getPluginManager().registerEvents((Listener) this, UHCPlugin.instance);
		}
		onChoose();
	}

	public final boolean isActive() {
		return isActive;
	}

	public void onDeactivate() {
	}

	public final void deactivate() {
		isActive = false;
		if(this instanceof Listener) {
			HandlerList.unregisterAll((Listener) this);
		}
		MutatorManager.activeMutators.remove(this);
		isHidden = false;
		onDeactivate();
	}

	public final String getInfo() {
		if(isHidden) {
			return DARK_GRAY + "[" + DARK_RED + "Скрытый Мутатор" + DARK_GRAY + "] " + DARK_PURPLE + "Нет информации";
		} else {
			return DARK_GRAY + "[" + RED + "Мутатор" + DARK_GRAY + "] " + MUTATOR_NAME_COLOR + getName() + GRAY + ": "
					+ GRAY + getDescription();
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
