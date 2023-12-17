package ru.greenbudgie.util.item;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EnchantmentLocalizerTest {

    @Test
    public void testLocalizeName() {
        assertEquals("Luck Of The Sea", EnchantmentLocalizer.localizeName(Enchantment.LUCK));
        assertEquals("Thorns", EnchantmentLocalizer.localizeName(Enchantment.THORNS));
        assertEquals("Binding Curse", EnchantmentLocalizer.localizeName(Enchantment.BINDING_CURSE));
    }

    @Test
    public void testLocalizeFullLevelOne() {
        Enchantment luck = mock(Enchantment.class);
        when(luck.getMaxLevel()).thenReturn(3);
        when(luck.getKey()).thenReturn(NamespacedKey.minecraft("luck_of_the_sea"));
        Enchantment silkTouch = mock(Enchantment.class);
        when(silkTouch.getMaxLevel()).thenReturn(1);
        when(silkTouch.getKey()).thenReturn(NamespacedKey.minecraft("silk_touch"));

        assertEquals("Luck Of The Sea I", EnchantmentLocalizer.localize(luck, 1));
        assertEquals("Silk Touch", EnchantmentLocalizer.localize(silkTouch, 1));
    }

}