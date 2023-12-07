package ru.greenbudgie.mutator;

import ru.greenbudgie.util.MathUtils;

import static org.bukkit.ChatColor.*;

public enum ThreatStatus {

	SUPPORTING(
			DARK_AQUA + "" + BOLD + "Скучные мутаторы... " + RESET + AQUA + "На этот раз всем повезло",
			DARK_AQUA + "" + BOLD + "Скучный выбор. " + RESET + AQUA + "Видимо, игра будет простой и долгой",
			DARK_AQUA + "" + BOLD + "Слишком легко. " + RESET + AQUA + "Надеюсь, кто-то активирует мутатор посложнее",
			DARK_AQUA + "" + BOLD + "Так совсем не интересно. " + RESET + AQUA + "Не мутаторы, а херота",
			DARK_AQUA + "" + BOLD + "Мда. " + RESET + AQUA + "Изи мутаторы)",
			DARK_AQUA + "" + BOLD + "ок найс мутаторы) " + RESET + AQUA + "будет мегаизи)"
	),
	INNOCENT(
			DARK_GREEN + "" + BOLD + "Интересный выбор. " + RESET + GREEN + "Скажем, сбалансированные мутаторы",
			DARK_GREEN + "" + BOLD + "Довольно скучно. " + RESET + GREEN + "Какие-то средние мутаторы",
			DARK_GREEN + "" + BOLD + "Средние мутаторы. " + RESET + GREEN + "Могло бы быть и поинтереснее"
	),
	DANGEROUS(
			DARK_RED + "" + BOLD + "Игра обещает быть интересной! " + RESET + RED + "Классные мутаторы",
			DARK_RED + "" + BOLD + "Будет весело. " + RESET + RED + "Надеюсь, мутаторы всем понравились",
			DARK_RED + "" + BOLD + "Отличный выбор! " + RESET + RED + "Игра будет необычной",
			DARK_RED + "" + BOLD + "А что, хорошие мутаторы. " + RESET + RED + "Скучно не будет точно"
	),
	CRITICAL(
			DARK_PURPLE + "" + BOLD + "Что за выбор... " + RESET + LIGHT_PURPLE + "Извиняюсь за такие мутаторы",
			DARK_PURPLE + "" + BOLD + "АХАХАХАХХАХАХАХА) " + RESET + LIGHT_PURPLE + "Удачи с такими мутаторами)",
			DARK_PURPLE + "" + BOLD + "Короткая игра намечается. " + RESET + LIGHT_PURPLE + "Все подохнут до арены)",
			DARK_PURPLE + "" + BOLD + "Слишком жестко. " + RESET + LIGHT_PURPLE + "Все сдохнут сами) Не пвпштесь)",
			DARK_PURPLE + "" + BOLD + "АЧЕ) " + RESET + LIGHT_PURPLE + "КЛАСС МУТАТОРЫ)"
	);

	private final String[] messages;

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
