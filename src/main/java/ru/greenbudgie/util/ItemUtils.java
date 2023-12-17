package ru.greenbudgie.util;

import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helps to make new items and change existing ones
 */
public class ItemUtils {

	/**
	 * Sets a name for a specific item
	 * @param item An item
	 * @param name Name
	 */
	public static ItemStack setName(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Sets a lore for a specific item
	 * @param item An item
	 * @param lore Lore
	 */
	public static ItemStack setLore(ItemStack item, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Makes an item unbreakable
	 * @param item An item
	 */
	public static ItemStack setUnbreakable(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable(true);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Sets a lore as array for a specific item
	 * @param item An item
	 * @param lore Lore
	 */
	public static ItemStack setLore(ItemStack item, String... lore) {
		setLore(item, Lists.newArrayList(lore));
		return item;
	}

	private static List<String> splitLongString(String str, int max) {
		List<String> list = new ArrayList<>();
		int prev = 0;
		for(int i = 0; i < str.length(); i++) {
			if(i - prev >= max && str.charAt(i) == ' ') {
				list.add(str.substring(prev, i));
				prev = i;
			}
			if(i == str.length() - 1) {
				list.add(str.substring(prev, i + 1));
			}
		}
		return list;
	}

	/**
	 * Adds a lore for specific item splitting it by length into multiple rows
	 * @param item An item
	 * @param lore A lore
	 * @param maxLength	Maximum length of a string in a row
	 */
	public static ItemStack addSplittedLore(ItemStack item, String lore, int maxLength) {
		List<String> splitted = splitLongString(lore, maxLength);
		for(int i = 0; i < splitted.size(); i++) {
			String str = splitted.get(i);
			String prevColor = "";
			if(i > 0) {
				prevColor = ChatColor.getLastColors(splitted.get(0));
			}
			addLore(item, false, prevColor + str);
		}
		return item;
	}

	/**
	 * Adds a lore for specific item splitting it by length into multiple rows. This method uses default length of 25
	 * @param item An item
	 * @param lore A lore
	 */
	public static ItemStack addSplittedLore(ItemStack item, String lore) {
		return addSplittedLore(item, lore, 25);
	}

	/**
	 * Makes item glow: adds an oxygen enchantment and hides it
	 * @param item An item
	 */
	public static ItemStack addGlow(ItemStack item) {
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addEnchant(Enchantment.OXYGEN, 1, true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(itemMeta);
		return item;
	}

	/**
	 * Writes a custom string value to item NBT
	 * Note that this method DOES NOT CHANGE the item stack itself, it creates a new one
	 * @param item Item to use
	 * @param name Name of a value
	 * @param value The value
	 * @return Changed item
	 */
	public static ItemStack setCustomValue(ItemStack item, String name, String value) {
		name = "custom_" + name;
		var nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound nbt = nmsItem.w();
		nbt.a(name, value);
		nmsItem.b(nbt);
		return CraftItemStack.asBukkitCopy(nmsItem);
	}

	/**
	 * Removes a custom value from item NBT
	 * Note that this method DOES NOT CHANGE the item stack itself, it creates a new one
	 * @param item Item to use
	 * @param name Name of a value to remove
	 * @return Changed item
	 */
	public static ItemStack removeCustomValue(ItemStack item, String name) {
		name = "custom_" + name;
		net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound nbt = nmsItem.w();
		nbt.r(name);
		nmsItem.b(nbt);
		return CraftItemStack.asBukkitCopy(nmsItem);
	}

	/**
	 * Checks whether the item has the specified value
	 * @param item Item to check
	 * @param name Name of a value
	 * @return Whether the item has the specified value
	 */
	public static boolean hasCustomValue(ItemStack item, String name) {
		name = "custom_" + name;
		net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound nbt = nmsItem.w();
		return nbt.e(name);
	}

	/**
	 * Gets the custom string value by the given name
	 * @param item item to use
	 * @param name Custom value name
	 * @return Custom value by the given name, or null if not present
	 */
	@Nullable
	public static String getCustomValue(ItemStack item, String name) {
		name = "custom_" + name;
		var nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound nbt = nmsItem.w();
		return nbt.e(name) ? nbt.l(name) : null;
	}

	public static ItemStack getHead(OfflinePlayer player) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		if(player == null) return head;
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		if(meta == null) return head;
		meta.setOwningPlayer(player);
		head.setItemMeta(meta);
		return head;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getHead(String playerName) {
		return getHead(Bukkit.getOfflinePlayer(playerName));
	}

	public static Builder builder(Material item) {
		return new Builder(item);
	}

	public static Builder builder(ItemStack item) {
		return new Builder(item);
	}

	public static PotionBuilder potionBuilder() {
		return new PotionBuilder();
	}

	public static class Enchant {

		public Enchantment enchantment;
		public int level;

		public Enchant(Enchantment enchantment, int level) {
			this.enchantment = enchantment;
			this.level = level;
		}

		public Enchant(Enchantment enchantment) {
			this.enchantment = enchantment;
			this.level = 1;
		}

		public ItemStack enchant(ItemStack item) {
			item.addUnsafeEnchantment(enchantment, level);
			return item;
		}

	}

	/**
	 * Enchants item with specified enchantments
	 * @param item An item
	 * @param enchantments Enchantments
	 */
	public static ItemStack addEnchantments(ItemStack item, Enchant... enchantments) {
		Lists.newArrayList(enchantments).forEach(ench -> ench.enchant(item));
		return item;
	}

	/**
	 * Gets the item lore or returns an empty list to prevent NullPointerException
	 * @param item An item
	 */
	public static List<String> getLore(ItemStack item) {
		return item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();
	}

	/**
	 * Add an array of strings to existing lore or creates new one
	 * @param item An item
	 * @param toStart Whether to add new strings to start of pre existing lore
	 * @param strings List of strings to add
	 */
	public static ItemStack addLore(ItemStack item, boolean toStart, List<String> strings) {
		return addLore(item, toStart, strings.toArray(new String[0]));
	}

	/**
	 * Add an array of strings to existing lore or creates new one, putting it to the end of the lore as default
	 * @param item An item
	 * @param strings List of strings to add
	 */
	public static ItemStack addLore(ItemStack item, String... strings) {
		return addLore(item, false, strings);
	}

	/**
	 * Add an array of strings to existing lore or creates new one
	 * @param item An item
	 * @param toStart Whether to add new strings to start of pre existing lore
	 * @param strings List of strings to add
	 */
	public static ItemStack addLore(ItemStack item, boolean toStart, String... strings) {
		List<String> prevLore = getLore(item);
		List<String> lore = Stream.of(strings).filter(Objects::nonNull).collect(Collectors.toList());
		if(!toStart) {
			prevLore.addAll(lore);
			setLore(item, prevLore);
		} else {
			lore.addAll(Lists.newArrayList(prevLore));
			setLore(item, lore);
		}
		return item;
	}

	/**
	 * Adds a random ID to an item which makes it unstackable
	 * @param item An item
	 */
	public static ItemStack setUnstackable(ItemStack item) {
		return addLore(item, false, ChatColor.DARK_GRAY + "ID: " + MathUtils.getRandomSequence(16));
	}

	/**
	 * Builds a potion with effects
	 */
	public static class PotionBuilder {

		private PotionMeta meta;
		private boolean drinkable = true;
		private boolean splash = false;
		private boolean lingering = false;


		public PotionBuilder() {
			ItemStack item = new ItemStack(Material.POTION);
			meta = (PotionMeta) item.getItemMeta();
		}

		public PotionBuilder withColor(Color color) {
			meta.setColor(color);
			return this;
		}

		public PotionBuilder withEffects(PotionEffect... effects) {
			Lists.newArrayList(effects).forEach(ef -> meta.addCustomEffect(ef, true));
			return this;
		}

		public PotionBuilder withType(PotionType type) {
			meta.setBasePotionType(type);
			return this;
		}

		public PotionBuilder asDrinkable() {
			splash = false;
			drinkable = true;
			lingering = false;
			return this;
		}

		public PotionBuilder asSplash() {
			splash = true;
			drinkable = false;
			lingering = false;
			return this;
		}

		public PotionBuilder asLingering() {
			splash = false;
			drinkable = false;
			lingering = true;
			return this;
		}

		public PotionBuilder withName(String name) {
			meta.setDisplayName(name);
			return this;
		}

		public ItemStack build() {
			ItemStack potion = new ItemStack(Material.POTION);
			if(splash) {
				potion.setType(Material.SPLASH_POTION);
			}
			if(lingering) {
				potion.setType(Material.LINGERING_POTION);
			}
			potion.setItemMeta(meta);
			return potion;
		}

	}

	/**
	 * Builds an item with name, lore, enchantments e.t.c.
	 */
	public static class Builder {

		protected ItemStack item;
		protected ItemMeta meta;

		private boolean condition = true;

		public Builder(Material type) {
			item = new ItemStack(type);
			meta = item.getItemMeta();
		}

		public Builder(ItemStack item) {
			this.item = item.clone();
			meta = item.getItemMeta();
		}

		/**
		 * Applies the following change to the ItemStack if the provided value is true.
		 * For example, {@code ifTrue(false).withLore("hi")} will not add the lore to the ItemStack.
		 */
		public Builder ifTrue(boolean value) {
			condition = value;
			return this;
		}

		/**
		 * Applies the following change to the ItemStack if the provided value is false.
		 * For example, {@code ifFalse(true).withLore("hi")} will not add the lore to the ItemStack.
		 */
		public Builder ifFalse(boolean value) {
			condition = !value;
			return this;
		}

		private boolean doNotApply() {
			if (!condition) {
				condition = true;
				return true;
			}
			return false;
		}

		public Builder withFlags(ItemFlag... flags) {
			if (doNotApply()) return this;
			meta.addItemFlags(flags);
			return this;
		}

		public Builder withName(String name) {
			if (doNotApply()) return this;
			meta.setDisplayName(name);
			return this;
		}

		public Builder withLore(List<String> lore) {
			if (doNotApply()) return this;
			List<String> prevLore = meta.getLore();
			if(prevLore == null) prevLore = new ArrayList<>();
			prevLore.addAll(lore);
			meta.setLore(prevLore);
			return this;
		}

		public Builder withLore(String... lore) {
			if (doNotApply()) return this;
			withLore(Arrays.asList(lore));
			return this;
		}

		public Builder unbreakable() {
			if (doNotApply()) return this;
			meta.setUnbreakable(true);
			return this;
		}

		public Builder withSplittedLore(String lore, int length) {
			if (doNotApply()) return this;
			List<String> splitted = splitLongString(lore, length);
			for(int i = 0; i < splitted.size(); i++) {
				String str = splitted.get(i);
				String prevColor = "";
				if(i > 0) {
					prevColor = ChatColor.getLastColors(splitted.get(0));
				}
				List<String> prevLore = meta.getLore();
				if(prevLore == null) prevLore = new ArrayList<>();
				prevLore.add(prevColor + str);
				meta.setLore(prevLore);
			}
			return this;
		}

		public Builder withSplittedLore(String lore) {
			if (doNotApply()) return this;
			withSplittedLore(lore, 25);
			return this;
		}

		public Builder withGlow() {
			if (doNotApply()) return this;
			meta.addEnchant(Enchantment.OXYGEN, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			return this;
		}

		public Builder withGlow(boolean glow) {
			if (doNotApply()) return this;
			if(glow) withGlow();
			return this;
		}

		public Builder withEnchantments(Enchant... enchantments) {
			if (doNotApply()) return this;
			for(Enchant enchant : enchantments) {
				meta.addEnchant(enchant.enchantment, enchant.level, true);
			}
			return this;
		}

		@Deprecated
		public Builder unstackable() {
			if (doNotApply()) return this;
			setUnstackable(item);
			return this;
		}

		public Builder withAmount(int amount) {
			if (doNotApply()) return this;
			item.setAmount(amount);
			return this;
		}

		public Builder withValue(String name, String value) {
			if (doNotApply()) return this;
			item.setItemMeta(meta);
			item = setCustomValue(item, name, value);
			meta = item.getItemMeta();
			return this;
		}

		public ItemStack build() {
			item.setItemMeta(meta);
			return item;
		}

	}

}
