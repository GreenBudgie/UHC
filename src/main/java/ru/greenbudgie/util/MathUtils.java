package ru.greenbudgie.util;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.bukkit.Location;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public final class MathUtils {

	/**
	 * Returns true or false based on random chance
	 *
	 * @param chance chance in percents between 0 and 100
	 */
	public static boolean chance(double chance) {
		if(chance <= 0) return false;
		if(chance >= 100) return true;
		return chance / 100 > Math.random();
	}

	public static int randomRange(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	public static double randomRangeDouble(double min, double max) {
		return min + (max - min) * Math.random();
	}

	public static int ticksToSeconds(int ticks) {
		return (int) Math.round(ticks / 20.0);
	}

	public static String formatTime(int seconds) {
		Date date = new Date(seconds * 1000L);
		return (seconds >= 3600 ? (int) Math.floor(seconds / 3600.0) + ":" : "") + new SimpleDateFormat("mm:ss").format(date);
	}

	public static double clamp(double num, double min, double max) {
		if(num < min) {
			return min;
		} else {
			return Math.min(num, max);
		}
	}

	public static int clamp(int num, int min, int max) {
		if(num < min) {
			return min;
		} else {
			return Math.min(num, max);
		}
	}

	public static double lengthSq(double x, double y, double z) {
		return (x * x) + (y * y) + (z * z);
	}

	public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
		return map.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), value)).map(Map.Entry::getKey).collect(Collectors.toSet());
	}

	public static String getRandomSequence(int length) {
		Random random = new Random();
		char[] symbols = new char[62];
		char[] chars = new char[52];
		char[] numbers = new char[10];
		if(length < 1) {
			throw new IllegalArgumentException("length < 1: " + length);
		}
		if(symbols[0] == (char) 0) {
			int symIdx = 0, numIdx = 0, charIdx = 0;
			for(int idx = 48; idx <= 57; ++idx) {
				symbols[symIdx++] = (char) (idx);
				numbers[numIdx++] = (char) (idx);
			}
			for(int idx = 97; idx < 26 + 97; ++idx) {
				symbols[symIdx++] = (char) (idx);
				chars[charIdx++] = (char) (idx);
			}
			for(int idx = 65; idx < 26 + 65; ++idx) {
				symbols[symIdx++] = (char) (idx);
				chars[charIdx++] = (char) (idx);
			}
		}
		StringBuilder returnString = new StringBuilder();
		for(int idx = 0; idx < length; ++idx) {
			returnString.append(symbols[random.nextInt(symbols.length)]);
		}
		return returnString.toString();
	}

	public static Location getOutlineLocation(Location l, double r) {
		double x = 0, y = 0, z = 0;
		int rand = MathUtils.randomRange(1, 3);
		switch(rand) {
		case 1:
			x = choose(r, -r);
			break;
		case 2:
			y = choose(r, -r);
			break;
		case 3:
			z = choose(r, -r);
			break;
		}
		if(x != 0) {
			y = randomRangeDouble(-r, r);
			z = randomRangeDouble(-r, r);
		}
		if(y != 0) {
			x = randomRangeDouble(-r, r);
			z = randomRangeDouble(-r, r);
		}
		if(z != 0) {
			x = randomRangeDouble(-r, r);
			y = randomRangeDouble(-r, r);
		}
		return l.clone().add(x, y, z);
	}

	public static double min(List<Double> x) {
		double min = Double.MAX_VALUE;
		for(double i : x) {
			if(i < min) min = i;
		}
		return min;
	}

	public static double min(double[] x) {
		double min = Double.MAX_VALUE;
		for(double i : x) {
			if(i < min) min = i;
		}
		return min;
	}

	public static double max(List<Double> x) {
		double max = Double.MIN_VALUE;
		for(double i : x) {
			if(i < max) max = i;
		}
		return max;
	}

	public static double max(double[] x) {
		double max = Double.MIN_VALUE;
		for(double i : x) {
			if(i > max) max = i;
		}
		return max;
	}

	public static boolean isInteger(double x) {
		return x == (int) x;
	}

	public static double decimal(double x, int decimalPlaces) {
		if(decimalPlaces <= 0) return x;
		String[] str = Double.toString(x).trim().replace(".", " ").split(" ");
		if(str[1].length() <= decimalPlaces) return x;
		String r = str[1].substring(0, decimalPlaces);
		x = Double.parseDouble(str[0] + "." + r);
		return x;
	}

	public static int percent(int x) {
		if(x < 0) return 0;
		if(x > 100) return 100;
		return x;
	}

	public static double percentDouble(double x) {
		if(x < 0) return 0;
		if(x > 100) return 100;
		return x;
	}

	public static <T> T choose(T... values) {
		if(values.length == 0) throw new IllegalArgumentException("Cannot choose an element from an empty array");
		return values[randomRange(0, values.length - 1)];
	}

	public static <T> T choose(List<T> values) {
		if(values.size() == 0) throw new IllegalArgumentException("Cannot choose an element from an empty list");
		return values.get(randomRange(0, values.size() - 1));
	}

	public static <T> T choose(Collection<T> values) {
		if(values.size() == 0) throw new IllegalArgumentException("Cannot choose an element from an empty collection");
		List<T> list = Lists.newArrayList(values);
		return list.get(randomRange(0, values.size() - 1));
	}

	public static boolean isVowel(char c) {
		return "ÀÅ¨ÈÎÓÛÝÞßàå¸èîóûýþÿAEIOUaeiou".indexOf(c) != -1;
	}

	public static List<String> getListOfStringsMatchingLastWord(String[] args, String... possibilities) {
		return getListOfStringsMatchingLastWord(args, Arrays.asList(possibilities));
	}

	public static List<String> getListOfStringsMatchingLastWord(String[] inputArgs, Collection<?> possibleCompletions) {
		String s = inputArgs[inputArgs.length - 1];
		List<String> list = Lists.<String>newArrayList();

		if(!possibleCompletions.isEmpty()) {
			for(String s1 : Iterables.transform(possibleCompletions, Functions.toStringFunction())) {
				if(doesStringStartWith(s, s1)) {
					list.add(s1);
				}
			}
		}

		return list;
	}

	public static boolean doesStringStartWith(String original, String region) {
		return region.regionMatches(true, 0, original, 0, original.length());
	}

}
