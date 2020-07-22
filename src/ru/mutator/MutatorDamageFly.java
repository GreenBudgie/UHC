package ru.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;
import ru.util.MathUtils;

import java.util.List;

public class MutatorDamageFly extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.FEATHER;
	}

	@Override
	public String getName() {
		return "��������� �����";
	}

	@Override
	public String getDescription() {
		return "��� ��������� ����� �� ��������� ������ ��������� �� 5 ������. ���������, ���� ��� ������� � ���� ������ �� ����� �������� ����!";
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void damage(EntityDamageEvent e) {
		if(!e.isCancelled() && e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if(UHC.isPlaying(player)) {
				if(!player.hasPotionEffect(PotionEffectType.LEVITATION)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
				}
			}
		}
	}


}
