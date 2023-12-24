package ru.greenbudgie.util.weighted;

import com.google.common.collect.Streams;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.item.ItemUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.bukkit.ChatColor.*;

public class WeightedPotionEffectItem extends WeightedItem {

    private final List<WeightedPotionEffect> persistedEffects;
    private final WeightedPotionEffectList weightedPotionEffects;
    private final int minNumber;
    private final int maxNumber;

    private WeightedPotionEffectItem(
            ItemStack item,
            int min,
            int max,
            int weight,
            List<WeightedPotionEffect> persistedEffects,
            WeightedPotionEffectList weightedPotionEffects,
            int minNumber,
            int maxNumber
    ) {
        super(item, min, max, weight);
        if (!(item.getItemMeta() instanceof PotionMeta)) {
            throw new IllegalArgumentException("Provided item cannot have potion effects");
        }
        if (minNumber < 0 || maxNumber < 0) {
            throw new IllegalArgumentException("Provided number of weighted potion effects that is less than 0");
        }
        if (minNumber > maxNumber) {
            throw new IllegalArgumentException("Provided minimum number of weighted effects is larger " +
                    "than maximum number");
        }
        int elementSize = weightedPotionEffects.elements.size();
        if (elementSize > 0) {
            if (minNumber > elementSize || maxNumber > elementSize) {
                throw new IllegalArgumentException(
                        "Provided number of weighted effects that is larger than overall " +
                                "weighted effects number " + elementSize
                );
            }
        }
        this.persistedEffects = persistedEffects;
        this.weightedPotionEffects = weightedPotionEffects;
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
    }

    @Override
    public ItemStack getPreviewItem() {
        List<String> persistedEffectPreviews = persistedEffects.stream()
                .map(effect -> effect.getPreviewString(false))
                .toList();
        List<String> weightedEffectPreviews = weightedPotionEffects.getElements().stream()
                .map(effect -> effect.getPreviewString(true))
                .toList();
        String prefix = DARK_GRAY + "- ";
        String persistedEffectInfo = prefix + AQUA + BOLD + "Выпадают всегда" + DARK_GRAY + ":";
        String numberInfo;
        if (minNumber == maxNumber) {
            numberInfo = DARK_AQUA + "ровно " + AQUA + BOLD + minNumber + DARK_AQUA + " из";
        } else {
            numberInfo = DARK_AQUA + "от " + AQUA + BOLD + minNumber + DARK_AQUA +
                    " до " + AQUA + BOLD + maxNumber;
        }
        String weightedEffectInfo = prefix + DARK_AQUA + "Выпадает " +
                numberInfo + DARK_GRAY + ":";
        ItemStack previewItem = ItemUtils.builder(super.getPreviewItem())
                .ifFalse(persistedEffectPreviews.isEmpty())
                    .withLore(persistedEffectInfo)
                .ifFalse(persistedEffectPreviews.isEmpty())
                    .withLore(persistedEffectPreviews)
                .ifFalse(weightedEffectPreviews.isEmpty())
                    .withLore(weightedEffectInfo)
                .ifFalse(weightedEffectPreviews.isEmpty())
                    .withLore(weightedEffectPreviews)
                .build();
        List<Color> colors = Stream.concat(persistedEffects.stream(), weightedPotionEffects.getElements().stream())
                .map(weightedEffect -> weightedEffect.element.getColor())
                .toList();
        PotionMeta meta = (PotionMeta) previewItem.getItemMeta();
        Objects.requireNonNull(meta);
        meta.setColor(mixedColor(colors));
        previewItem.setItemMeta(meta);
        return previewItem;
    }

    public ItemStack getItem() {
        List<PotionEffect> persistedEffects = this.persistedEffects.stream()
                .map(WeightedPotionEffect::getPotionEffect)
                .toList();
        int weightedEffectsToApply = getRandomWeightedEffectsNumber();
        List<PotionEffect> weightedEffects;
        if (weightedEffectsToApply == 0 || weightedPotionEffects.getElements().isEmpty()) {
            weightedEffects = new ArrayList<>();
        } else {
            weightedEffects = weightedPotionEffects
                    .getRandomElementsWeighted(weightedEffectsToApply).stream()
                    .map(WeightedPotionEffect::getPotionEffect)
                    .toList();
        }
        List<PotionEffect> allEffects = Streams.concat(
                persistedEffects.stream(),
                weightedEffects.stream()
        ).toList();
        List<Color> colors = allEffects.stream().map(effect -> effect.getType().getColor()).toList();
        ItemStack item = super.getItem();
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        Objects.requireNonNull(meta);
        meta.setColor(mixedColor(colors));
        for (PotionEffect effect : allEffects) {
            meta.addCustomEffect(effect, true);
        }
        item.setItemMeta(meta);
        return item;
    }

    public int getRandomWeightedEffectsNumber() {
        return MathUtils.randomRange(minNumber, maxNumber);
    }

    public static Builder arrow() {
        return new Builder(ItemUtils.builder(Material.TIPPED_ARROW).withName(WHITE + "Tipped Arrow").build());
    }

    public static Builder potion(ItemStack item) {
        return new Builder(item);
    }

    private Color mixedColor(List<Color> colors) {
        Color firstColor = colors.get(0);
        List<Color> otherColors = new ArrayList<>();
        for (int i = 1; i < colors.size(); i++) {
            otherColors.add(colors.get(i));
        }
        return firstColor.mixColors(otherColors.toArray(new Color[0]));
    }

    public static class Builder {

        private final ItemStack item;

        private final List<WeightedPotionEffect> persistedEffects = new ArrayList<>();
        private final List<WeightedPotionEffect> weightedEffects = new ArrayList<>();
        private int minNumber = 1;
        private int maxNumber = 1;

        private int minAmount = 1;
        private int maxAmount = 1;
        private int weight = 1;

        public Builder(@Nonnull Material type) {
            this(new ItemStack(type));
        }

        public Builder(@Nonnull ItemStack item) {
            this.item = item;
        }

        public Builder persistedEffects(WeightedPotionEffect... effects) {
            this.persistedEffects.addAll(Arrays.asList(effects));
            return this;
        }

        public Builder weightedEffects(WeightedPotionEffect... effects) {
            this.weightedEffects.addAll(Arrays.asList(effects));
            return this;
        }

        public Builder effectNumber(int number) {
            return effectNumber(number, number);
        }

        public Builder effectNumber(int minNumber, int maxNumber) {
            this.minNumber = minNumber;
            this.maxNumber = maxNumber;
            return this;
        }

        public Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Builder amount(int amount) {
            amount(amount, amount);
            return this;
        }

        public Builder amount(int min, int max) {
            this.minAmount = min;
            this.maxAmount = max;
            return this;
        }

        public WeightedPotionEffectItem build() {
            return new WeightedPotionEffectItem(
                    item,
                    minAmount,
                    maxAmount,
                    weight,
                    persistedEffects,
                    new WeightedPotionEffectList(weightedEffects.toArray(new WeightedPotionEffect[0])),
                    minNumber,
                    maxNumber
            );
        }

    }


}
