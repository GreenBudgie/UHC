package ru.util;

import com.google.common.collect.Lists;
import de.slikey.effectlib.effect.LineEffect;
import net.minecraft.server.v1_16_R1.BlockPosition;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import ru.main.UHCPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class WorldHelper {

	public static boolean isDay(World w) {
		return w.getEnvironment() == World.Environment.NORMAL ? w.getTime() >= 0 && w.getTime() <= 12000 : false;
	}

	public static boolean isInEnvironment(Player p, World.Environment d) {
		return p.getWorld().getEnvironment() == d;
	}

	public static boolean isInAnotherEnvironment(Player y, Player a) {
		return y.getWorld().getEnvironment() != a.getWorld().getEnvironment();
	}

	public static boolean isInAnotherEnvironment(Location c, Location a) {
		return c.getWorld().getEnvironment() != a.getWorld().getEnvironment();
	}

	public static boolean isInSameEnvironment(Player p, Player p2) {
		return p.getWorld().getEnvironment() == p2.getWorld().getEnvironment();
	}

	public static boolean isInSameEnvironment(Location l1, Location l2) {
		return l1.getWorld().getEnvironment() == l2.getWorld().getEnvironment();
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
				|| t == EntityType.WITCH || t == EntityType.WITHER || t == EntityType.PHANTOM || t == EntityType.DROWNED;
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
		List<Block> blocks = new ArrayList<Block>();
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
		List<Block> blocks2 = new ArrayList<Block>();
		List<Material> list = Lists.<Material>newArrayList(materials);
		for(Block b : blocks) {
			if(list.contains(b.getType())) blocks2.add(b);
		}
		return blocks2;
	}

	public static List<Block> getCuboidAroundNoDown(Location l) {
		List<Block> blocks = new ArrayList<Block>();
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
		List<Block> blocks = new ArrayList<Block>();
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
		List<Block> blocks = new ArrayList<Block>();
		for(Location l3 : l2) {
			blocks.add(l3.getBlock());
		}
		return blocks;
	}

	public static List<Block> getBlocksAroundNoDown(Location l) {
		Location[] l2 = {l.clone().add(0, 1, 0), l.clone().add(1, 0, 0), l.clone().add(-1, 0, 0), l.clone().add(0, 0, 1), l.clone().add(0, 0, -1)};
		List<Block> blocks = new ArrayList<Block>();
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
		List<Material> list = new ArrayList<Material>();
		getBlocksAround(l).forEach(block -> list.add(block.getType()));
		return list;
	}

	@SuppressWarnings("deprecation")
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
						break forZ;
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
		if(yaw <= 135 && yaw >= 45) return LookDirection.NegX;
		if(yaw <= 225 && yaw >= 135) return LookDirection.NegZ;
		if(yaw <= 315 && yaw >= 225) return LookDirection.PosX;
		return null;
	}

	public static Location getFaceLocation(Location l, BlockFace face) {
		return l.clone().add(face.getModX(), face.getModY(), face.getModZ());
	}

	public static LivingEntity getTarget(Player player, int range) {
		List<Entity> nearbyE = player.getNearbyEntities(range, range, range);
		ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();

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
		List<Player> list = new ArrayList<Player>();
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

	public static boolean compareLocations(Location l1, Location l2) {
		if(l1.getBlockX() != l2.getBlockX()) return false;
		if(l1.getBlockY() != l2.getBlockY()) return false;
		if(l1.getBlockZ() != l2.getBlockZ()) return false;
		if(l1.getWorld() != l2.getWorld()) return false;
		return true;
	}

	public static String getEnvironmentName(World.Environment dim) {
		switch(dim) {
		case THE_END:
			return ChatColor.DARK_PURPLE + "Энд";
		case NETHER:
			return ChatColor.RED + "Ад";
		case NORMAL:
			return ChatColor.GREEN + "Земля";
		}
		return null;
	}

	public static ChatColor getEnvironmentColor(World.Environment dim) {
		switch(dim) {
		case THE_END:
			return ChatColor.DARK_PURPLE;
		case NETHER:
			return ChatColor.RED;
		case NORMAL:
			return ChatColor.GREEN;
		}
		return null;
	}

	public static String getEnvironmentNamePrepositional(World.Environment dim, ChatColor c) {
		switch(dim) {
		case THE_END:
			return c + "в " + ChatColor.DARK_PURPLE + "Энде";
		case NETHER:
			return c + "в " + ChatColor.RED + "Аду";
		case NORMAL:
			return c + "на " + ChatColor.GREEN + "Земле";
		}
		return null;
	}

	public static World.Environment getEnvironmentFromName(String name) {
		String name2 = ChatColor.stripColor(name).toLowerCase();
		switch(name2) {
		case "энд":
		case "в энде":
			return World.Environment.THE_END;
		case "ад":
		case "в аду":
			return World.Environment.NETHER;
		case "земля":
		case "на земле":
			return World.Environment.NORMAL;
		}
		return null;
	}

	public static void chorusTeleport(LivingEntity e, int range) {
		double x = e.getLocation().getX();
		double y = e.getLocation().getY();
		double z = e.getLocation().getZ();
		Random rand = new Random();
		for(int i = 0; i < range; ++i) {
			double d3 = x + (rand.nextDouble() - 0.5D) * range;
			double d4 = MathUtils.clamp(y + (double) (rand.nextInt(range) - (range / 2)), 0.0D, (double) (e.getWorld().getMaxHeight() - 1));
			double d5 = z + (rand.nextDouble() - 0.5D) * range;
			Location tpLoc = new Location(e.getWorld(), d3, d4, d5);
			if(e.getWorld().getWorldBorder().isInside(tpLoc)) {
				if(((CraftLivingEntity) e).getHandle().a(d3, d4, d5, false)) {
					lineEffect(e.getLocation().clone().add(0, e.getHeight() / 2, 0), new Location(e.getWorld(), x, y + e.getHeight() / 2, z), Particle.REDSTONE, Color.PURPLE,
							3);
					e.getWorld().playSound(e.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
					e.getWorld().playSound(new Location(e.getWorld(), x, y, z), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
					break;
				}
			}
		}
	}

	public static void lineEffect(Location l1, Location l2, Particle part, Color c, int amountPerBlock) {
		LineEffect ef = new LineEffect(UHCPlugin.em);
		ef.particle = part;
		ef.particles = (int) Math.round(l1.distance(l2) * amountPerBlock);
		if(c != null) ef.color = c;
		ef.iterations = 1;
		ef.setLocation(l1);
		ef.setTargetLocation(l2);
		ef.start();
	}

	public static void spawnParticlesAround(Entity ent, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			ParticleEffectPoint ef = new ParticleEffectPoint();
			ef.particle = effect;
			if(color != null) ef.color = color;
			double h = ent.getHeight();
			double w = ent.getWidth() / 1.5;
			ef.setLocation(ent.getLocation().clone().add(MathUtils.randomRangeDouble(-w, w), MathUtils.randomRangeDouble(0, h), MathUtils.randomRangeDouble(-w, w)));
			ef.start();
		}
	}

	public static void spawnParticlesInsideSphere(Location l, double radius, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			ParticleEffectPoint ef = new ParticleEffectPoint();
			ef.particle = effect;
			if(color != null) ef.color = color;
			double u = Math.random();
			double v = Math.random();
			double theta = u * 2.0 * Math.PI;
			double phi = Math.acos(2.0 * v - 1.0);
			double sinTheta = Math.sin(theta);
			double cosTheta = Math.cos(theta);
			double sinPhi = Math.sin(phi);
			double cosPhi = Math.cos(phi);
			double r = Math.random() * radius;
			double x = r * sinPhi * cosTheta;
			double y = r * sinPhi * sinTheta;
			double z = r * cosPhi;
			ef.setLocation(l.clone().add(x, y, z));
			ef.start();
		}
	}

	public static void spawnParticlesOutlineSphere(Location l, double radius, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			ParticleEffectPoint ef = new ParticleEffectPoint();
			ef.particle = effect;
			if(color != null) ef.color = color;
			double u = Math.random();
			double v = Math.random();
			double theta = 2 * Math.PI * u;
			double phi = Math.acos(2 * v - 1);
			double x = radius * Math.sin(phi) * Math.cos(theta);
			double y = radius * Math.sin(phi) * Math.sin(theta);
			double z = radius * Math.cos(phi);
			ef.setLocation(l.clone().add(x, y, z));
			ef.start();
		}
	}

	public static void spawnParticlesInRange(Location l, double radius, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			ParticleEffectPoint ef = new ParticleEffectPoint();
			ef.particle = effect;
			if(color != null) ef.color = color;
			ef.setLocation(
					l.clone().add(MathUtils.randomRangeDouble(-radius, radius), MathUtils.randomRangeDouble(-radius, radius), MathUtils.randomRangeDouble(-radius, radius)));
			ef.start();
		}
	}

	public static void spawnParticle(Location l, Particle effect, Color color) {
		ParticleEffectPoint ef = new ParticleEffectPoint();
		ef.particle = effect;
		if(color != null) ef.color = color;
		ef.setLocation(l);
		ef.start();
	}

	public static void spawnParticlesInside(Block b, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			ParticleEffectPoint ef = new ParticleEffectPoint();
			ef.particle = effect;
			if(color != null) ef.color = color;
			ef.setLocation(b.getLocation().clone().add(MathUtils.randomRangeDouble(0, 1), MathUtils.randomRangeDouble(0, 1), MathUtils.randomRangeDouble(0, 1)));
			ef.start();
		}
	}

	public static void spawnParticlesOutline(Block b, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			ParticleEffectPoint ef = new ParticleEffectPoint();
			ef.particle = effect;
			if(color != null) ef.color = color;
			ef.setLocation(MathUtils.getOutlineLocation(b.getLocation().clone().add(0.5, 0.5, 0.5), 0.6));
			ef.start();
		}
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
		List<Item> item = new ArrayList<Item>();
		for(Entity ent : e) {
			if(ent instanceof Item) {
				item.add((Item) ent);
			}
		}
		return item;
	}

	public static List<ItemStack> getItemStacksAtLocation(Location l) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		getItemEntitiesAtLocation(l).forEach(item -> list.add(item.getItemStack()));
		return list;
	}

	public static List<Entity> getEntitiesDistance(Location l, double maxDist) {
		List<Entity> ent = new ArrayList<Entity>();
		List<Entity> entities = l.getWorld().getEntities();
		for(Entity e : entities) {
			if(e.getLocation().distance(l) <= maxDist) {
				ent.add(e);
			}
		}
		return ent;
	}

	public static List<Player> playersNotmeDistance(Player p, double maxDist) {
		List<Player> pl = new ArrayList<Player>();
		List<Player> players = p.getWorld().getPlayers();
		for(Player player : players) {
			if(player != p && player.getLocation().distance(p.getLocation()) <= maxDist) {
				pl.add(player);
			}
		}
		return pl;
	}

	public static List<Player> playersNotme(Player p) {
		List<Player> pl = new ArrayList<Player>();
		List<Player> players = p.getWorld().getPlayers();
		for(Player player : players) {
			if(player != p) pl.add(player);
		}
		return pl;
	}

	public static List<Player> playersNotmeAnyDimension(Player p) {
		List<Player> pl = new ArrayList<Player>();
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
		for(int i = 0; i < pl.size(); i++) {
			double d = pl.get(i).getLocation().distance(l);
			if(d < dist) {
				dist = d;
				near = pl.get(i);
			}
		}
		return near;
	}

	public static List<Player> nearestPlayers(Location l, int dist) {
		List<Player> list = new ArrayList<Player>();
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

	public static boolean isChunkInUse(Location loc) {
		return loc.getWorld().isChunkInUse(loc.getChunk().getX(), loc.getChunk().getZ());
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
		return getColoredCoordinates(Integer.valueOf(s[0]), Integer.valueOf(s[1]), Integer.valueOf(s[2]));
	}

	public static List<String> getAllPlayerNames() {
		return getAllPlayerNames(Bukkit.getOnlinePlayers());
	}

	public static List<String> getAllPlayerNames(Collection<? extends Player> players) {
		List<String> list = new ArrayList<String>();
		for(Player p : players) {
			list.add(p.getName());
		}
		return list;
	}

	public static String locationAsStringNoWorld(Location l) {
		return String.valueOf(l.getBlockX()) + " " + String.valueOf(l.getBlockY()) + " " + String.valueOf(l.getBlockZ());
	}

	public static String locationAsString(Location l) {
		return String.valueOf(l.getBlockX()) + " " + String.valueOf(l.getBlockY()) + " " + String.valueOf(l.getBlockZ()) + " " + l.getWorld().getName();
	}

	public static Location translateToLocation(String str) {
		String[] l = str.trim().split(" ");
		if(l.length == 4) {
			try {
				return new Location(Bukkit.getWorld(l[3]), Integer.valueOf(l[0]), Integer.valueOf(l[1]), Integer.valueOf(l[2]));
			} catch(NumberFormatException e) {
			}
		}
		return null;
	}

	public static Location translateToLocation(World world, String rawStr, int yDefault) {
		String[] coords = rawStr.trim().split(" ");
		if(coords.length >= 2) {
			try {
				boolean f = coords.length == 2;
				int xc = Integer.valueOf(coords[0]);
				int yc = f ? yDefault : Integer.valueOf(coords[1]);
				int zc = Integer.valueOf(coords[f ? 1 : 2]);
				return new Location(world, xc, yc, zc);
			} catch(NumberFormatException e) {
			}
		}
		return null;
	}

	public static Location translateToLocation(World world, String rawStr) {
		return translateToLocation(world, rawStr, 64);
	}

	public static boolean canTranslateToLocation(String rawStr) {
		String[] coords = rawStr.trim().split(" ");
		if(coords.length >= 2) {
			try {
				boolean f = coords.length == 2;
				int xc = Integer.valueOf(coords[0]);
				int yc = f ? 64 : Integer.valueOf(coords[1]);
				int zc = Integer.valueOf(coords[f ? 1 : 2]);
				return true;
			} catch(NumberFormatException e) {
			}
		}
		return false;
	}

	public static List<Player> getPlayersDistance(Location l, double maxDist) {
		List<Player> list = new ArrayList<Player>();
		for(Player p : l.getWorld().getPlayers()) {
			if(l.distance(p.getLocation()) <= maxDist) {
				list.add(p);
			}
		}
		return list;
	}

	public static List<Material> getUnbreakable() {
		List<Material> list = new ArrayList<Material>();
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

	public static enum LookDirection {
		PosZ,
		NegZ,
		PosX,
		NegX
	}

}
