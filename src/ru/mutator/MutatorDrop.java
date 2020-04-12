package ru.mutator;

import org.bukkit.Material;
import ru.UHC.Drops;

public class MutatorDrop extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.BEACON;
	}

	@Override
	public String getName() {
		return "Больше Дропа!";
	}

	@Override
	public String getDescription() {
		return "Аирдроп и кейвдроп спавнятся в два раза чаще";
	}

	@Override
	public void onChoose() {
		Drops.airdropTimer /= 2;
		Drops.cavedropTimer /= 2;
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

}
