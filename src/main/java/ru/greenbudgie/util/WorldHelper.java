package ru.greenbudgie.util;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPosition;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class WorldHelper {

	public static boolean isDay(World w) {
		return w.getEnvironment() == World.Environment.NORMAL && w.getTime() >= 0 && w.getTime() <= 12000;
	}

	public static BlockPosition toBlockPos(Location l) {
		return new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	public static boolean isBadMob(EntityType t) {
		return t == EntityType.ZOMBIE || t == EntityType.SKELETON || t == EntityType.WITHER_SKELETON || t == EntityType.SHULKER || t == EntityType.SLIME
				|| t == EntityType.SILVERFISH || t == EntityType.ENDERMAN || t == EntityType.SPIDER || t == EntityType.CAVE_SPIDER || t == EntityType.ZOMBIE_VILLAGER
				|| t == EntityType.ENDER_DRAGON || t == EntityType.CREEPER || t == EntityType.BLAZE || t == EntityType.ELDER_GUARDIAN || t == EntityType.GUARDIAN
				|| t == EntityType.ENDERMITE || t == EntityType.EVOKER || t == EntityType.GHAST || t == EntityType.GIANT || t == EntityType.HUSK || t == EntityType.ILLUSIONER
				|| t == EntityType.MAGMA_CUBE || t == EntityType.STRAY || t == EntityType.VEX || t == EntityType.VINDICATOR
				|| t == EntityType.WITCH || t == EntityType.WITHER || t == EntityType.PHANTOM || t == EntityType.DROWNED || t == EntityType.RAVAGER || t == EntityType.PILLAGER;
	}

	public static double distanceNoY(Location l, Location l2) {
		if(l == null || l2 == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null location");
		} else if(l.getWorld() == null || l2.getWorld() == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null world");
		} else if(l.getWorld() != l2.getWorld()) {
			throw new IllegalArgumentException("Cannot measure distance between " + l.getWorld().getName() + " and " + l2.getWorld().getName());
		}
		return Math.sqrt(NumberConversions.square(l.getX() - l2.getX()) + NumberConversions.square(l.getZ() - l2.getZ()));
	}

	public static boolean isOre(Material m) {
		return m == Material.DIAMOND_ORE || m == Material.GOLD_ORE || m == Material.EMERALD_ORE || m == Material.IRON_ORE || m == Material.COAL_ORE || m == Material.LAPIS_ORE
				|| m == Material.REDSTONE_ORE || m == Material.NETHER_QUARTZ_ORE;
	}

	public static List<Block> getBlocksArea(Location l, int d) {
		List<Block> blocks = new ArrayList<>();
		for(int x = -d; x <= d; x++) {
			for(int y = -d; y <= d; y++) {
				for(int z = -d; z <= d; z++) {
					Block b = l.clone().add(x, y, z).getBlock();
					if(b.getType() != Material.AIR) {
						blocks.add(b);
					}
				}
			}
		}
		return blocks;
	}

	public static Location center(Location l) {
		if(l == null) return null;
		return l.clone().add(0.5, 0.5, 0.5);
	}

	public static List<Block> getCertainBlocks(List<Block> blocks, Material... materials) {
		List<Block> blocks2 = new ArrayList<>();
		List<Material> list = Lists.newArrayList(materials);
		for(Block b : blocks) {
			if(list.contains(b.getType())) blocks2.add(b);
		}
		return blocks2;
	}

	public static List<Block> getCuboidAroundNoDown(Location l) {
		List<Block> blocks = new ArrayList<>();
		for(int x = -1; x <= 1; x++) {
			for(int y = 0; y <= 1; y++) {
				for(int z = -1; z <= 1; z++) {
					blocks.add(l.clone().add(x, y, z).getBlock());
				}
			}
		}
		return blocks;
	}

	public static List<Block> getCuboidAround(Location l) {
		List<Block> blocks = new ArrayList<>();
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				for(int z = -1; z <= 1; z++) {
					blocks.add(l.clone().add(x, y, z).getBlock());
				}
			}
		}
		return blocks;
	}

	public static List<Block> getBlocksAround(Location l) {
		Location[] l2 = {l.clone().add(0, 1, 0), l.clone().add(0, -1, 0), l.clone().add(1, 0, 0), l.clone().add(-1, 0, 0), l.clone().add(0, 0, 1), l.clone().add(0, 0, -1)};
		List<Block> blocks = new ArrayList<>();
		for(Location l3 : l2) {
			blocks.add(l3.getBlock());
		}
		return blocks;
	}

	public static List<Block> getBlocksAroundNoDown(Location l) {
		Location[] l2 = {l.clone().add(0, 1, 0), l.clone().add(1, 0, 0), l.clone().add(-1, 0, 0), l.clone().add(0, 0, 1), l.clone().add(0, 0, -1)};
		List<Block> blocks = new ArrayList<>();
		for(Location l3 : l2) {
			blocks.add(l3.getBlock());
		}
		return blocks;
	}

	public static List<Block> getBlocksAroundNoAir(Location l) {
		List<Block> blocks = getBlocksAround(l);
		blocks.removeIf(block -> block.getType() == Material.AIR);
		return blocks;
	}

	public static List<Material> getBlockTypesAround(Location l) {
		List<Material> list = new ArrayList<>();
		getBlocksAround(l).forEach(block -> list.add(block.getType()));
		return list;
	}

	public static void fill(Material m, Location s, Location e, boolean replace) {
		int x1 = s.getBlockX();
		int y1 = s.getBlockY();
		int z1 = s.getBlockZ();
		int x2 = e.getBlockX();
		int y2 = e.getBlockY();
		int z2 = e.getBlockZ();
		int b;
		if(x1 > x2) {
			b = x2;
			x2 = x1;
			x1 = b;
		}
		if(y1 > y2) {
			b = y2;
			y2 = y1;
			y1 = b;
		}
		if(z1 > z2) {
			b = z2;
			z2 = z1;
			z1 = b;
		}
		for(int i = x1; i < x2; i++) {
			for(int j = y1; j < y2; j++) {
				for(int k = z1; k < z2; k++) {
					Block bl = new Location(s.getWorld(), i, j, k).getBlock();
					if(bl.getType() == Material.AIR || replace) {
						bl.setType(m);
					}
				}
			}
		}
	}

	public static void setBlock(Location loc, Material m) {
		loc.getBlock().setType(m);
	}

	public static void makeSphere(Location pos, Material m, double radiusX, double radiusY, double radiusZ, boolean filled) {
		radiusX += 0.5;
		radiusY += 0.5;
		radiusZ += 0.5;

		final double invRadiusX = 1 / radiusX;
		final double invRadiusY = 1 / radiusY;
		final double invRadiusZ = 1 / radiusZ;

		final int ceilRadiusX = (int) Math.ceil(radiusX);
		final int ceilRadiusY = (int) Math.ceil(radiusY);
		final int ceilRadiusZ = (int) Math.ceil(radiusZ);

		double nextXn = 0;
		forX:
		for(int x = 0; x <= ceilRadiusX; ++x) {
			final double xn = nextXn;
			nextXn = (x + 1) * invRadiusX;
			double nextYn = 0;
			forY:
			for(int y = 0; y <= ceilRadiusY; ++y) {
				final double yn = nextYn;
				nextYn = (y + 1) * invRadiusY;
				double nextZn = 0;
				forZ:
				for(int z = 0; z <= ceilRadiusZ; ++z) {
					final double zn = nextZn;
					nextZn = (z + 1) * invRadiusZ;

					double distanceSq = MathUtils.lengthSq(xn, yn, zn);
					if(distanceSq > 1) {
						if(z == 0) {
							if(y == 0) {
								break forX;
							}
							break forY;
						}
					}

					if(!filled) {
						if(MathUtils.lengthSq(nextXn, yn, zn) <= 1 && MathUtils.lengthSq(xn, nextYn, zn) <= 1 && MathUtils.lengthSq(xn, yn, nextZn) <= 1) {
							continue;
						}
					}

					setBlock(pos.clone().add(x, y, z), m);
					setBlock(pos.clone().add(-x, y, z), m);
					setBlock(pos.clone().add(x, -y, z), m);
					setBlock(pos.clone().add(x, y, -z), m);
					setBlock(pos.clone().add(-x, -y, z), m);
					setBlock(pos.clone().add(x, -y, -z), m);
					setBlock(pos.clone().add(-x, y, -z), m);
					setBlock(pos.clone().add(-x, -y, -z), m);
				}
			}
		}
	}

	public static LookDirection getLookDirection(Player p) {
		float yaw = p.getLocation().getYaw();
		if(yaw <= 45 || yaw >= 315) return LookDirection.PosZ;
		if(yaw <= 135) return LookDirection.NegX;
		if(yaw <= 225) return LookDirection.NegZ;
		if(yaw <= 315) return LookDirection.PosX;
		return null;
	}

	public static Location getFaceLocation(Location l, BlockFace face) {
		return l.clone().add(face.getModX(), face.getModY(), face.getModZ());
	}

	public static LivingEntity getTarget(Player player, int range) {
		List<Entity> nearbyE = player.getNearbyEntities(range, range, range);
		ArrayList<LivingEntity> livingE = new ArrayList<>();

		for(Entity e : nearbyE) {
			if(e instanceof LivingEntity) {
				livingE.add((LivingEntity) e);
			}
		}

		LivingEntity target = null;
		BlockIterator bItr = new BlockIterator(player, range);
		Block block;
		Location loc;
		int bx, by, bz;
		double ex, ey, ez;
		while(bItr.hasNext()) {
			block = bItr.next();
			bx = block.getX();
			by = block.getY();
			bz = block.getZ();
			for(LivingEntity e : livingE) {
				loc = e.getLocation();
				ex = loc.getX();
				ey = loc.getY();
				ez = loc.getZ();
				if((bx - .75 <= ex && ex <= bx + 1.75) && (bz - .75 <= ez && ez <= bz + 1.75) && (by - 1 <= ey && ey <= by + 2.5)) {
					target = e;
					break;
				}
			}
		}
		return target;

	}

	public static List<Player> getPlayersInEnvironment(World.Environment dim) {
		List<Player> list = new ArrayList<>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getWorld().getEnvironment() == dim) list.add(p);
		}
		return list;
	}

	public static Location getPlayerLookLocation(Player p, int maxdist) {
		for(Block b : p.getLineOfSight(null, maxdist)) {
			if(b.getType() != Material.AIR) {
				return b.getLocation();
			}
		}
		return null;
	}

	public static Block getPlayerLookBlock(Player p, int maxdist) {
		Location loc = getPlayerLookLocation(p, maxdist);
		if(loc == null || loc.getBlock().getType() == Material.AIR) return null;
		return loc.getBlock();
	}

	public static boolean compareIntLocations(Location l1, Location l2) {
		if(l1.getBlockX() != l2.getBlockX()) return false;
		if(l1.getBlockY() != l2.getBlockY()) return false;
		if(l1.getBlockZ() != l2.getBlockZ()) return false;
		return l1.getWorld() == l2.getWorld();
	}

	public static String getEnvironmentName(World.Environment dim) {
		return switch (dim) {
			case THE_END -> ChatColor.DARK_PURPLE + "Энд";
			case NETHER -> ChatColor.RED + "Ад";
			case NORMAL -> ChatColor.GREEN + "Земля";
			default -> null;
		};
	}

	public static ChatColor getEnvironmentColor(World.Environment dim) {
		return switch (dim) {
			case THE_END -> ChatColor.DARK_PURPLE;
			case NETHER -> ChatColor.RED;
			case NORMAL -> ChatColor.GREEN;
			default -> null;
		};
	}

	public static String getEnvironmentNamePrepositional(World.Environment dim, ChatColor c) {
		return switch (dim) {
			case THE_END -> c + "в " + ChatColor.DARK_PURPLE + "Энде";
			case NETHER -> c + "в " + ChatColor.RED + "Аду";
			case NORMAL -> c + "на " + ChatColor.GREEN + "Земле";
			default -> null;
		};
	}

	public static World.Environment getEnvironmentFromName(String name) {
		String name2 = ChatColor.stripColor(name).toLowerCase();
		return switch (name2) {
			case "энд", "в энде" -> World.Environment.THE_END;
			case "ад", "в аду" -> World.Environment.NETHER;
			case "земля", "на земле" -> World.Environment.NORMAL;
			default -> null;
		};
	}

	public static void chorusTeleport(LivingEntity entity, int range, boolean playSound) {
		double x = entity.getLocation().getX();
		double y = entity.getLocation().getY();
		double z = entity.getLocation().getZ();
		Random rand = new Random();
		for(int i = 0; i < range; ++i) {
			double d3 = x + (rand.nextDouble() - 0.5D) * range;
			double d4 = MathUtils.clamp(y + (double) (rand.nextInt(range) - (range / 2)), 0.0D, entity.getWorld().getMaxHeight() - 1);
			double d5 = z + (rand.nextDouble() - 0.5D) * range;
			Location tpLoc = new Location(entity.getWorld(), d3, d4, d5);
			boolean aboveNetherCeiling =
					entity.getWorld().getEnvironment() == World.Environment.NETHER &&
					tpLoc.getY() > 127;
			if(entity.getWorld().getWorldBorder().isInside(tpLoc) && !aboveNetherCeiling) {
				if(((CraftLivingEntity) entity).getHandle().b(d3, d4, d5, false)) {
					if(playSound) {
						entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
						entity.getWorld().playSound(new Location(entity.getWorld(), x, y, z), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
					}
					break;
				}
			}
		}
	}

	public static void chorusTeleport(LivingEntity e, int range) {
		chorusTeleport(e, range, true);
	}

	public static boolean hasFullBlocksAbove(Location l) {
		int y = l.getBlockY();
		for(int i = y; i < l.getWorld().getMaxHeight(); i++) {
			Material block = l.clone().add(0, i - y, 0).getBlock().getType();
			if(!block.isTransparent() && !block.isOccluding()) return true;
		}
		return false;
	}

	public static boolean hasBlocksAbove(Location l) {
		int y = getClosestBlockYAbove(l);
		return y == -1 || y == l.getBlockY();
	}

	/**
	 * @param l The location
	 * @return The closest Y-coordinate with block above the given location, otherwise -1
	 */
	public static int getClosestBlockYAbove(Location l) {
		int y = l.getBlockY();
		for(int i = y; i < l.getWorld().getMaxHeight(); i++) {
			if(l.clone().add(0, i - y, 0).getBlock().getType() != Material.AIR) return i;
		}
		return -1;
	}

	/**
	 * @param l The location
	 * @param free The number of air blocks that must be above the closest block
	 * @return The closest Y-coordinate with block above the given location and some air blocks above it, otherwise -1
	 */
	public static int getClosestFreeBlockYAbove(Location l, int free) {
		int y0 = l.getBlockY();
		if(y0 >= l.getWorld().getMaxHeight()) return -1;
		int y1 = getClosestBlockYAbove(l);
		if(y1 == -1) return -1;
		int y2 = getClosestBlockYAbove(l.clone().add(0, y1 - y0 + 1, 0));
		if(y2 == -1) return y1;
		if(y2 - y1 > free) {
			return y1;
		} else {
			return getClosestFreeBlockYAbove(l.clone().add(0, y2 - y0, 0), free);
		}
	}

	/**
	 * @param l The location
	 * @param free The number of air blocks that must be under the closest block
	 * @return The closest Y-coordinate with block under the given location and some air blocks under it, otherwise -1
	 */
	public static int getClosestFreeBlockYUnder(Location l, int free) {
		int y0 = l.getBlockY();
		if(y0 < 0) return -1;
		int y1 = getClosestBlockYUnder(l);
		if(y1 == -1) return -1;
		int y2 = getClosestBlockYAbove(l.clone().subtract(0, y0 - y1 - 1, 0));
		if(y2 == -1) return y1;
		if(y2 - y1 > free) {
			return y1;
		} else {
			return getClosestFreeBlockYUnder(l.clone().subtract(0, y0 - y1 + 1, 0), free);
		}
	}

	/**
	 * @param l The location
	 * @return The closest Y-coordinate with block under the given location, otherwise -1
	 */
	public static int getClosestBlockYUnder(Location l) {
		int y = l.getBlockY();
		for(int i = y; i > 0; i--) {
			if(l.clone().subtract(0, y - i, 0).getBlock().getType() != Material.AIR) return i;
		}
		return -1;
	}

	public static List<Item> getItemEntitiesAtLocation(Location l) {
		Collection<Entity> e = l.getWorld().getNearbyEntities(l.clone().add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5);
		List<Item> item = new ArrayList<>();
		for(Entity ent : e) {
			if(ent instanceof Item) {
				item.add((Item) ent);
			}
		}
		return item;
	}

	public static List<ItemStack> getItemStacksAtLocation(Location l) {
		List<ItemStack> list = new ArrayList<>();
		getItemEntitiesAtLocation(l).forEach(item -> list.add(item.getItemStack()));
		return list;
	}

	public static List<Entity> getEntitiesDistance(Location l, double maxDist) {
		List<Entity> ent = new ArrayList<>();
		List<Entity> entities = l.getWorld().getEntities();
		for(Entity e : entities) {
			if(e.getLocation().distance(l) <= maxDist) {
				ent.add(e);
			}
		}
		return ent;
	}

	public static List<Player> playersNotmeDistance(Player p, double maxDist) {
		List<Player> pl = new ArrayList<>();
		List<Player> players = p.getWorld().getPlayers();
		for(Player player : players) {
			if(player != p && player.getLocation().distance(p.getLocation()) <= maxDist) {
				pl.add(player);
			}
		}
		return pl;
	}

	public static List<Player> playersNotme(Player p) {
		List<Player> pl = new ArrayList<>();
		List<Player> players = p.getWorld().getPlayers();
		for(Player player : players) {
			if(player != p) pl.add(player);
		}
		return pl;
	}

	public static List<Player> playersNotmeAnyDimension(Player p) {
		List<Player> pl = new ArrayList<>();
		@SuppressWarnings("unchecked") List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
		for(Player player : players) {
			if(player != p) pl.add(player);
		}
		return pl;
	}

	public static Player nearestPlayer(Location l) {
		List<Player> pl = l.getWorld().getPlayers();
		double dist = Double.MAX_VALUE;
		Player near = null;
		for (Player player : pl) {
			double d = player.getLocation().distance(l);
			if (d < dist) {
				dist = d;
				near = player;
			}
		}
		return near;
	}

	public static List<Player> nearestPlayers(Location l, int dist) {
		List<Player> list = new ArrayList<>();
		for(Player p : getPlayersInEnvironment(l.getWorld().getEnvironment())) {
			if(p.getLocation().distance(l) <= dist) list.add(p);
		}
		return list;
	}

	public static Player nearestPlayerNotme(Player p) {
		List<Player> pl = playersNotme(p);
		double dist = Double.MAX_VALUE;
		Player near = null;
		for(Player player : pl) {
			double d = player.getLocation().distance(p.getLocation());
			if(d < dist) {
				dist = d;
				near = player;
			}
		}
		return near;
	}

	public static boolean canSeeSky(Location l) {
		return l.getWorld().getHighestBlockYAt(l) <= l.getBlockY();
	}

	public static String getColoredLocationCoordinates(Location l) {
		return getColoredCoordinates(l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	public static String getColoredCoordinates(int x, int y, int z) {
		return ChatColor.RED + "" + x + " " + ChatColor.GREEN + y + " " + ChatColor.BLUE + z;
	}

	public static String getColoredCoordinates(String xyz) {
		String[] s = xyz.split(" ");
		return getColoredCoordinates(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
	}

	public static List<String> getAllPlayerNames() {
		return getAllPlayerNames(Bukkit.getOnlinePlayers());
	}

	public static List<String> getAllPlayerNames(Collection<? extends Player> players) {
		List<String> list = new ArrayList<>();
		for(Player p : players) {
			list.add(p.getName());
		}
		return list;
	}

	/**
	 * Translates the given string location, using the given world
	 *
	 * Raw string can only contain integer coordinates.
	 * Ex:
	 * Valid string: 234 -3 23, converts to location with given world, x=234, y=-3, z=23 and default yaw/pitch
	 * Valid string: 1 2 3 90 180, converts to: x=1, y=2, z=3, yaw=90, pitch=180
	 * @param world The world to create location in
	 * @param rawStr String to translate
	 * @return Translated location, or null if input string is invalid
	 */
	public static Location translateToLocation(World world, String rawStr) {
		String[] coords = rawStr.trim().split(" ");
		try {
			if(coords.length == 3) {
				return new Location(world,
						Integer.parseInt(coords[0]),
						Integer.parseInt(coords[1]),
						Integer.parseInt(coords[2]));
			} else if(coords.length == 5) {
				return new Location(world,
						Integer.parseInt(coords[0]),
						Integer.parseInt(coords[1]),
						Integer.parseInt(coords[2]),
						Integer.parseInt(coords[3]),
						Integer.parseInt(coords[4]));
			}
		} catch(Exception ignored) {}
		return null;
	}

	public static List<Player> getPlayersDistance(Location l, double maxDist) {
		List<Player> list = new ArrayList<>();
		for(Player p : l.getWorld().getPlayers()) {
			if(l.distance(p.getLocation()) <= maxDist) {
				list.add(p);
			}
		}
		return list;
	}

	public static List<Material> getUnbreakable() {
		List<Material> list = new ArrayList<>();
		list.add(Material.BEDROCK);
		list.add(Material.END_PORTAL_FRAME);
		list.add(Material.NETHER_PORTAL);
		list.add(Material.END_PORTAL);
		list.add(Material.DRAGON_EGG);
		list.add(Material.BARRIER);
		return list;
	}

	public static boolean isUnbreakable(Block b) {
		return getUnbreakable().contains(b.getType());
	}

	public static boolean isUnbreakable(Material m) {
		return getUnbreakable().contains(m);
	}

	public enum LookDirection {
		PosZ,
		NegZ,
		PosX,
		NegX
	}

}
