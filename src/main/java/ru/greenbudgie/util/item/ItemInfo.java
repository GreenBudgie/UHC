package ru.greenbudgie.util.item;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Helps to store structured information.
 * Can be applied to an item lore.
 */
public class ItemInfo {

    private final String primaryInfo;
    private String extraInfo;
    private String note;
    private String explanation;
    private String example;

    public ItemInfo(String primaryInfo) {
        this.primaryInfo = primaryInfo;
    }

    /**
     * Primary info shows a short and most informative text
     */
    public String getPrimaryInfo() {
        return primaryInfo;
    }

    /**
     * An explanation may contain a decent amount of text explaining
     * some critical moments
     */
    @Nullable
    public String getExplanation() {
        return explanation;
    }

    /**
     * An example may contain an info about the exact game situation
     */
    @Nullable
    public String getExample() {
        return example;
    }

    /**
     * A note may contain a short info about what a player
     * might need to take into account
     */
    @Nullable
    public String getNote() {
        return note;
    }

    /**
     * An extra info tells the player about technical moments,
     * like amount of dropped items, health reduction on percents e.t.c.
     */
    @Nullable
    public String getExtraInfo() {
        return extraInfo;
    }

    public ItemInfo note(String note) {
        this.note = note;
        return this;
    }

    public ItemInfo explanation(String explanation) {
        this.explanation = explanation;
        return this;
    }

    public ItemInfo example(String example) {
        this.example = example;
        return this;
    }

    public ItemInfo extra(String extra) {
        this.extraInfo = extra;
        return this;
    }

    public List<String> getFormattedInfoArray() {
        List<String> info = new ArrayList<>();
        info.add(ChatColor.GRAY + getPrimaryInfo());
        if(getExtraInfo() != null)
            info.add(ChatColor.AQUA + "Подробно: " + ChatColor.GRAY + getExtraInfo());
        if(getExplanation() != null)
            info.add(ChatColor.DARK_AQUA + "Пояснение: " + ChatColor.GRAY + getExplanation());
        if(getNote() != null)
            info.add(ChatColor.DARK_GREEN + "Заметка: " + ChatColor.GRAY + getNote());
        if(getExample() != null)
            info.add(ChatColor.GOLD + "Пример: " + ChatColor.GRAY + getExample());
        return info;
    }

    public void applyToItem(ItemStack item) {
        for(String lore : getFormattedInfoArray()) {
            ItemUtils.addSplittedLore(item, lore);
        }
    }

}
