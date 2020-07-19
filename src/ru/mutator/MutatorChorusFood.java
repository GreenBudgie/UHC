package ru.mutator;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import ru.util.WorldHelper;

public class MutatorChorusFood extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.CHORUS_FRUIT;
	}

	@Override
	public String getName() {
		return "��� �� ����";
	}

	@Override
	public String getDescription() {
		return "����� ��� ������ ����� �������� ��� �����";
	}

	@EventHandler
	public void eat(PlayerItemConsumeEvent e) {
		if(e.getItem().getType().isEdible()) {
			WorldHelper.chorusTeleport(e.getPlayer(), 16);
		}
	}


}
