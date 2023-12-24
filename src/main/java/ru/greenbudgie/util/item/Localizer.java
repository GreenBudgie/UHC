package ru.greenbudgie.util.item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Localizer {

    private static final Pattern keyNamePattern = Pattern.compile("\\b([a-zA-Z])");

    /**
     * Gets a localized english name for the specified enchantment
     */
    public static String localizeEnchantmentName(Enchantment enchantment) {
        return localizeKey(enchantment.getKey().getKey());
    }

    public static String localizePotionEffectType(PotionEffectType type) {
        return localizeKey(type.getKey().getKey());
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

    public static String localize(Enchantment enchantment, int level) {
        boolean localizeLevel = enchantment.getMaxLevel() != 1;
        if (!localizeLevel) {
            return localizeEnchantmentName(enchantment);
        }
        return localizeEnchantmentName(enchantment) + " " + localizeLevel(level);
    }

    private static String localizeKey(String namespacedKey) {
        String noUnderscoresKey = namespacedKey.replaceAll("_", " ");

        Matcher matcher = keyNamePattern.matcher(noUnderscoresKey);
        return matcher.replaceAll(match -> match.group().toUpperCase());
    }

}
