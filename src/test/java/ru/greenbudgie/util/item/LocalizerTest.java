package ru.greenbudgie.util.item;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LocalizerTest {

    @Test
    public void testLocalizeEnchantmentName() {
        assertEquals("Luck Of The Sea", Localizer.localizeEnchantmentName(Enchantment.LUCK));
        assertEquals("Thorns", Localizer.localizeEnchantmentName(Enchantment.THORNS));
        assertEquals("Binding Curse", Localizer.localizeEnchantmentName(Enchantment.BINDING_CURSE));
    }

    @Test
    public void testLocalizePotionTypeName() {
        assertEquals("Hero Of The Village", Localizer.localizePotionEffectType(PotionEffectType.HERO_OF_THE_VILLAGE));
        assertEquals("Blindness", Localizer.localizePotionEffectType(PotionEffectType.BLINDNESS));
    }

    @Test
    public void testLocalizeFullLevelOne() {
        Enchantment luck = mock(Enchantment.class);
        when(luck.getMaxLevel()).thenReturn(3);
        when(luck.getKey()).thenReturn(NamespacedKey.minecraft("luck_of_the_sea"));
        Enchantment silkTouch = mock(Enchantment.class);
        when(silkTouch.getMaxLevel()).thenReturn(1);
        when(silkTouch.getKey()).thenReturn(NamespacedKey.minecraft("silk_touch"));

        assertEquals("Luck Of The Sea I", Localizer.localize(luck, 1));
        assertEquals("Silk Touch", Localizer.localize(silkTouch, 1));
    }

}