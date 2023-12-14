package ru.greenbudgie.configuration;

import static org.bukkit.ChatColor.*;

public enum GameType {

    DEFAULT(GREEN + "" + BOLD +"Стандартная", true, true),
    NO_MUTATORS(DARK_GREEN + "" + BOLD + "Без мутаторов", false, true),
    NO_CLASSES(LIGHT_PURPLE + "" + BOLD + "Без классов", true, false),
    BORING(GRAY + "" + BOLD + "Скучная", false, false);

    private static GameType type = DEFAULT;

    public static GameType getType() {
        return type;
    }

    public static void setType(GameType type) {
        GameType.type = type;
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
