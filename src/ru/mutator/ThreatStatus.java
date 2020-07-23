package ru.mutator;

import org.bukkit.ChatColor;
import ru.util.MathUtils;

public enum ThreatStatus {

	SUPPORTING(
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "������� ��������... " + ChatColor.RESET + ChatColor.AQUA + "�� ���� ��� ���� �������",
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "������� �����. " + ChatColor.RESET + ChatColor.AQUA + "������, ���� ����� ������� � ������",
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "������� �����. " + ChatColor.RESET + ChatColor.AQUA + "�������, ���-�� ���������� ������� ���������",
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "��� ������ �� ���������. " + ChatColor.RESET + ChatColor.AQUA + "�� ��������, � ������",
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "���. " + ChatColor.RESET + ChatColor.AQUA + "������� ����� ������ �� ���������",
			ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "�� ���� ��������) " + ChatColor.RESET + ChatColor.AQUA + "����� �������)"
	),
	INNOCENT(
			ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "���������� �����. " + ChatColor.RESET + ChatColor.GREEN + "������, ���������������� ��������",
			ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "�������� ������. " + ChatColor.RESET + ChatColor.GREEN + "�����-�� ������� ��������",
			ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "������� ��������. " + ChatColor.RESET + ChatColor.GREEN + "����� �� ���� � ������������"
	),
	DANGEROUS(
			ChatColor.DARK_RED + "" + ChatColor.BOLD + "���� ������� ���� ����������! " + ChatColor.RESET + ChatColor.RED + "�������� ��������",
			ChatColor.DARK_RED + "" + ChatColor.BOLD + "����� ������. " + ChatColor.RESET + ChatColor.RED + "�������, �������� ���� �����������",
			ChatColor.DARK_RED + "" + ChatColor.BOLD + "�������� �����! " + ChatColor.RESET + ChatColor.RED + "���� ����� ���������",
			ChatColor.DARK_RED + "" + ChatColor.BOLD + "� ���, ������� ��������. " + ChatColor.RESET + ChatColor.RED + "������ �� ����� �����"
	),
	CRITICAL(
			ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "��, ��� �� �����... " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "��������� �� ����� ��������",
			ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "����������������) " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "����� � ������ ����������)",
			ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "�������� ���� ����������. " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "��� �������� �� �����, �������",
			ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "������� ������. " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "��� ������� ����, ����� �� ���������",
			ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "���) " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + "����� ��������)"
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
