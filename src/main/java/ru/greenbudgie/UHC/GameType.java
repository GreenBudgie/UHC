package ru.greenbudgie.UHC;

import org.bukkit.ChatColor;

public enum GameType {

    DEFAULT(ChatColor.GREEN + "Стандартная", true, true),
    ONLY_CLASSES(ChatColor.DARK_GREEN + "Только классы", false, true),
    ONLY_MUTATORS(ChatColor.LIGHT_PURPLE + "Только мутаторы", true, false),
    BORING(ChatColor.GRAY + "Скучная", false, false);

    private static GameType type = DEFAULT;

    public static GameType getType() {
        return type;
    }

    public static void switchType() {
        if(UHC.playing) return;
        int typeOrdinal = type.ordinal();
        if(typeOrdinal == values().length - 1) {
            type = values()[0];
        } else {
            type = values()[typeOrdinal + 1];
        }
    }

    private final String description;
    private final boolean allowsMutators;
    private final boolean allowsClasses;

    GameType(String description, boolean allowsMutators, boolean allowsClasses) {
        this.description = description;
        this.allowsMutators = allowsMutators;
        this.allowsClasses = allowsClasses;
    }

    public String getDescription() {
        return description;
    }

    public boolean allowsMutators() {
        return allowsMutators;
    }

    public boolean allowsClasses() {
        return allowsClasses;
    }
}
