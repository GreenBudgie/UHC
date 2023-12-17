package ru.greenbudgie.util.item;

import org.bukkit.enchantments.Enchantment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnchantmentLocalizer {

    private static final Pattern enchantmentNamePattern = Pattern.compile("\\b([a-zA-Z])");

    /**
     * Gets a localized english name for the specified enchantment
     */
    public static String localizeName(Enchantment enchantment) {
        String key = enchantment.getKey().getKey();
        String noUnderscoresKey = key.replaceAll("_", " ");

        Matcher matcher = enchantmentNamePattern.matcher(noUnderscoresKey);
        return matcher.replaceAll(match -> match.group().toUpperCase());
    }

    /**
     * Gets a roman number for the specified enchantment level.
     * Only works for levels from 1 to 10, then returns default integer representation.
     */
    public static String localizeLevel(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(level);
        };
    }

    public static String localize(Enchant enchantment) {
        return localize(enchantment.getEnchantment(), enchantment.getLevel());
    }

    public static String localize(Enchantment enchantment, int level) {
        boolean localizeLevel = enchantment.getMaxLevel() != 1;
        if (!localizeLevel) {
            return localizeName(enchantment);
        }
        return localizeName(enchantment) + " " + localizeLevel(level);
    }


}
