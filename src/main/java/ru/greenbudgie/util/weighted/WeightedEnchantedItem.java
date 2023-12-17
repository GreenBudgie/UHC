package ru.greenbudgie.util.weighted;

import com.google.common.collect.Streams;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.item.Enchant;
import ru.greenbudgie.util.item.ItemUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class WeightedEnchantedItem extends WeightedItem {

    private final List<WeightedEnchantment> alwaysEnchant;
    private final WeightedEnchantmentList weightedEnchantments;
    private final int minNumber;
    private final int maxNumber;

    private WeightedEnchantedItem(
            ItemStack item,
            int weight,
            List<WeightedEnchantment> alwaysEnchant,
            WeightedEnchantmentList weightedEnchantments,
            int minNumber,
            int maxNumber
    ) {
        super(item, 1, 1, weight);
        if (minNumber < 0 || maxNumber < 0) {
            throw new IllegalArgumentException("Provided number of weighed enchantments that is less than 0");
        }
        if (minNumber > maxNumber) {
            throw new IllegalArgumentException("Provided minimum number of weighed enchantments is larger " +
                    "than maximum number");
        }
        int elementSize = weightedEnchantments.elements.size();
        if (elementSize > 0) {
            if (minNumber > elementSize || maxNumber > elementSize) {
                throw new IllegalArgumentException(
                        "Provided number of weighed enchantments that is larger than overall " +
                                "weighted enchantments number " + elementSize
                );
            }
        }
        this.alwaysEnchant = alwaysEnchant;
        this.weightedEnchantments = weightedEnchantments;
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
    }

    public boolean isEnchantedBook() {
        return element.getType() == Material.ENCHANTED_BOOK;
    }

    @Override
    public ItemStack getPreviewItem() {
        List<String> alwaysEnchantmentPreviews = alwaysEnchant.stream()
                .map(enchant -> enchant.getPreviewString(false))
                .toList();
        List<String> weightedEnchantmentPreviews = weightedEnchantments.getElements().stream()
                .map(enchant -> enchant.getPreviewString(true))
                .toList();
        String prefix = DARK_GRAY + "- ";
        String alwaysEnchantInfo = prefix + AQUA + BOLD + "Выпадают всегда" + DARK_GRAY + ":";
        String numberInfo;
        if (minNumber == maxNumber) {
            numberInfo = DARK_AQUA + "ровно " + AQUA + BOLD + minNumber + DARK_AQUA + " из";
        } else {
            numberInfo = DARK_AQUA + "от " + AQUA + BOLD + minNumber + DARK_AQUA +
                    " до " + AQUA + BOLD + maxNumber;
        }
        String weightedEnchantInfo = prefix + DARK_AQUA + "Выпадает " +
                numberInfo + DARK_GRAY + ":";
        return ItemUtils.builder(element.clone())
                .withLore(GRAY + "Шанс " + AQUA + getFormattedChancePercent())
                .ifFalse(alwaysEnchantmentPreviews.isEmpty())
                    .withLore(alwaysEnchantInfo)
                .ifFalse(alwaysEnchantmentPreviews.isEmpty())
                    .withLore(alwaysEnchantmentPreviews)
                .ifFalse(weightedEnchantmentPreviews.isEmpty())
                    .withLore(weightedEnchantInfo)
                .ifFalse(weightedEnchantmentPreviews.isEmpty())
                    .withLore(weightedEnchantmentPreviews)
                .withGlow()
                .build();
    }

    public ItemStack getItem() {
        List<Enchant> alwaysEnchantmentList = alwaysEnchant.stream().map(WeightedEnchantment::getEnchantment).toList();
        int weightedEnchantmentsToApply = getRandomWeightedEnchantmentsNumber();
        List<Enchant> weightedEnchantmentList;
        if (weightedEnchantmentsToApply == 0 || weightedEnchantments.getElements().isEmpty()) {
            weightedEnchantmentList = new ArrayList<>();
        } else {
            weightedEnchantmentList = weightedEnchantments
                    .getRandomElementsWeighted(weightedEnchantmentsToApply).stream()
                    .map(WeightedEnchantment::getEnchantment)
                    .toList();
        }
        List<Enchant> allEnchantments = Streams.concat(
                alwaysEnchantmentList.stream(),
                weightedEnchantmentList.stream()
        ).toList();
        if (isEnchantedBook()) {
            return ItemUtils.addEnchantmentsToBook(element.clone(), allEnchantments);
        }
        return ItemUtils.addEnchantments(element.clone(), allEnchantments);
    }

    public int getRandomWeightedEnchantmentsNumber() {
        return MathUtils.randomRange(minNumber, maxNumber);
    }

    public static Builder book() {
        return new Builder(Material.ENCHANTED_BOOK);
    }

    public static Builder item(Material type) {
        return new Builder(type);
    }

    public static Builder item(ItemStack item) {
        return new Builder(item);
    }

    public static class Builder {

        private final ItemStack item;

        private final List<WeightedEnchantment> alwaysEnchant = new ArrayList<>();
        private final List<WeightedEnchantment> weightedEnchantments = new ArrayList<>();
        private int minNumber = 1;
        private int maxNumber = 1;

        private int weight = 1;

        public Builder(@Nonnull Material type) {
            this(new ItemStack(type));
        }

        public Builder(@Nonnull ItemStack item) {
            this.item = item;
        }

        public Builder alwaysEnchant(Enchant... enchantments) {
            List<WeightedEnchantment> convertedEnchantments = Arrays.stream(enchantments)
                    .map(enchant ->
                            WeightedEnchantment.builder(enchant.getEnchantment())
                                    .level(enchant.getLevel())
                                    .build()
                    )
                    .toList();
            this.alwaysEnchant.addAll(convertedEnchantments);
            return this;
        }

        public Builder alwaysEnchant(WeightedEnchantment... enchantments) {
            this.alwaysEnchant.addAll(Arrays.asList(enchantments));
            return this;
        }

        public Builder weightedEnchantments(WeightedEnchantment... enchantments) {
            this.weightedEnchantments.addAll(Arrays.asList(enchantments));
            return this;
        }

        public Builder number(int number) {
            return number(number, number);
        }

        public Builder number(int minNumber, int maxNumber) {
            this.minNumber = minNumber;
            this.maxNumber = maxNumber;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public WeightedEnchantedItem build() {
            return new WeightedEnchantedItem(
                    item,
                    weight,
                    alwaysEnchant,
                    new WeightedEnchantmentList(weightedEnchantments.toArray(new WeightedEnchantment[0])),
                    minNumber,
                    maxNumber
            );
        }

    }


}
