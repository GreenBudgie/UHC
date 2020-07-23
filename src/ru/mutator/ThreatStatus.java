package ru.mutator;

import org.bukkit.ChatColor;
import ru.util.MathUtils;

public enum ThreatStatus {

	SUPPORTING(
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Скучные мутаторы... " + ChatColor.RESET + ChatColor.AQUA + "На этот раз всем повезло",
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Скучный выбор. " + ChatColor.RESET + ChatColor.AQUA + "Видимо, игра будет простой и долгой",
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Слишком легко. " + ChatColor.RESET + ChatColor.AQUA + "Надеюсь, кто-то активирует мутатор посложнее",
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Так совсем не интересно. " + ChatColor.RESET + ChatColor.AQUA + "Не мутаторы, а херота",
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Мда. " + ChatColor.RESET + ChatColor.AQUA + "Слишком много помощи от мутаторов",
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "ок найс мутаторы) " + ChatColor.RESET + ChatColor.AQUA + "будет мегаизи)"
	),
	INNOCENT(
			ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Интересный выбор. " + ChatColor.RESET + ChatColor.GREEN + "Скажем, сбалансированные мутаторы",
			ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Довольно скучно. " + ChatColor.RESET + ChatColor.GREEN + "Какие-то средние мутаторы",
			ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Средние мутаторы. " + ChatColor.RESET + ChatColor.GREEN + "Могло бы быть и поинтереснее"
	),
	DANGEROUS(
			ChatColor.DARK_RED + "" + ChatColor.BOLD + "Игра обещает быть интересной! " + ChatColor.RESET + ChatColor.RED + "Классные мутаторы",
			ChatColor.DARK_RED + "" + ChatColor.BOLD + "Будет весело. " + ChatColor.RESET + ChatColor.RED + "Надеюсь, мутаторы всем понравились",
			ChatColor.DARK_RED + "" + ChatColor.BOLD + "Отличный выбор! " + ChatColor.RESET + ChatColor.RED + "Игра будет необычной",
			ChatColor.DARK_RED + "" + ChatColor.BOLD + "А что, хорошие мутаторы. " + ChatColor.RESET + ChatColor.RED + "Скучно не будет точно"
	),
	CRITICAL(
			ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Ой, что за выбор... " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "Извиняюсь за такие мутаторы",
			ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "АХАХАХАХХАХАХАХА) " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "удачи с такими мутаторами)",
			ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Короткая игра намечается. " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "Все подохнут до арены, отвечаю",
			ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Слишком жестко. " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "Все сдохнут сами, можно не ПВПшиться",
			ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "АЧЕ) " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "КЛАСС МУТАТОРЫ)"
	);

	private String[] messages;

	ThreatStatus(String... messages) {
		this.messages = messages;
	}

	public String getRandomMessage() {
		return MathUtils.choose(messages);
	}

	public static ThreatStatus getAverageStatus(ThreatStatus... statuses) {
		int average = 0;
		for(ThreatStatus status : statuses) {
			average += status.ordinal();
		}
		average = Math.round(average / (float) statuses.length);
		for(ThreatStatus status : values()) {
			if(status.ordinal() == average) {
				return status;
			}
		}
		return null;
	}

}
