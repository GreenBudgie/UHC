package ru.greenbudgie.util;

import com.google.common.collect.ListMultimap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.List;

public class ParticleUtils {

	/**
	 * Creates particles on the edges of the given region
	 * @param region The region
	 * @param particle Particle type to spawn
	 * @param density How much particles to spawn per block
	 * @param color Color of particle, might be null
	 */
	public static void createParticlesOnRegionEdges(Region region, Particle particle, double density, @Nullable Color color) {
		region.validateWorlds();
		ListMultimap<Location, Location> edges = region.getEdges();
		for(Location start : edges.keySet()) {
			List<Location> ends = edges.get(start);
			for(Location end : ends) {
				createLine(start, end, particle, density, color);
			}
		}
	}

	/**
	 * Creates a flash effect at the given location
	 * @param location Location to create a flash
	 */
	public static void flash(Location location) {
		createParticle(location, Particle.FLASH, null);
	}

	/**
	 * Creates particles on the faces of the given region
	 * @param region The region
	 * @param particle Particle type to spawn
	 * @param density How much particles to spawn at each side
	 * @param color Color of particle, might be null
	 */
	public static void createParticlesOnRegionFaces(Region region, Particle particle, int density, @Nullable Color color) {
		region.validateWorlds();
		ListMultimap<Location, Location> faces = region.getFaces();
		for(Location start : faces.keySet()) {
			List<Location> ends = faces.get(start);
			for(Location end : ends) {
				createParticlesBetween(start, end, particle, density, color);
			}
		}
	}

	/**
	 * Creates particles inside of the given region
	 * @param region The region
	 * @param particle Particle type to spawn
	 * @param amount How much particles to spawn
	 * @param color Color of particle, might be null
	 */
	public static void createParticlesInsideRegion(Region region, Particle particle, int amount, @Nullable Color color) {
		region.validateWorlds();
		for(int i = 0; i < amount; i++) {
			createParticle(region.getRandomInsideLocation(), particle, color);
		}
	}

	/**
	 * Creates a line of particles
	 * @param from Line start location
	 * @param to Line end location
	 * @param particle A particle to create
	 * @param color Color of a particle, might be null
	 * @param density How much particles to spawn per block
	 */
	public static void createLine(Location from, Location to, Particle particle, double density, @Nullable Color color) {
		if(from.getWorld() != to.getWorld()) throw new IllegalArgumentException("Locations must be in the same world!");
		Vector vector = to.toVector().subtract(from.toVector()); //Setting up a from-to vector
		vector.normalize(); //Converting to a unit vector to apply density
		double length = from.distance(to);
		int particleCount = (int) Math.round(length * density);
		Vector shift = vector.multiply(1D / density);
		Location currentLocation = from.clone();

		for(int i = 0; i < particleCount; i++) {
			createParticle(currentLocation, particle, color);
			currentLocation.add(shift);
		}
	}

	/**
	 * Creates a flat XZ-plane circle with no rotation
	 * @param center The center of a circle
	 * @param particle Particle to show
	 * @param color Color to use, might be null
	 * @param radius Radius of a circle
	 * @param particles Amount of particles to create
	 */
	public static void createCircle(Location center, Particle particle, @Nullable Color color, double radius, int particles) {
		double angleDelta = (Math.PI * 2) / particles;
		for(double rotation = 0; rotation < Math.PI * 2; rotation += angleDelta) {
			Location point = center.clone().add(radius * Math.cos(rotation), 0, radius * Math.sin(rotation));
			createParticle(point, particle, color);
		}
	}

	/**
	 * Creates a particles between two locations, in cube
	 * @param from Surface start location (first corner)
	 * @param to Surface end location (second corner)
	 * @param particle A particle to create
	 * @param color Color of a particle, can be null
	 * @param density How much particles to spawn per block
	 */
	public static void createParticlesBetween(Location from, Location to, Particle particle, double density, @Nullable Color color) {
		if(from.getWorld() != to.getWorld()) throw new IllegalArgumentException("Locations must be in the same world!");
		double x1 = Math.min(from.getX(), to.getX());
		double y1 = Math.min(from.getY(), to.getY());
		double z1 = Math.min(from.getZ(), to.getZ());
		double x2 = Math.max(from.getX(), to.getX());
		double y2 = Math.max(from.getY(), to.getY());
		double z2 = Math.max(from.getZ(), to.getZ());
		int sizeX = (int) Math.ceil(x2 - x1) + 1;
		int sizeY = (int) Math.ceil(y2 - y1) + 1;
		int sizeZ = (int) Math.ceil(z2 - z1) + 1;
		int count = (int) (sizeX * sizeY * sizeZ * density);
		for(int i = 0; i < count; i++) {
			Location loc = new Location(from.getWorld(), MathUtils.randomRangeDouble(x1, x2), MathUtils.randomRangeDouble(y1, y2), MathUtils.randomRangeDouble(z1, z2));
			createParticle(loc, particle, color);
		}
	}

	/**
	 * Determines if the particle can be colorized
	 * @param particle Particle to check
	 * @return Whether the particle has a color option
	 */
	public static boolean hasColor(Particle particle) {
		return particle == Particle.REDSTONE || particle == Particle.SPELL_MOB || particle == Particle.SPELL_MOB_AMBIENT;
	}

	/**
	 * Creates a single particle at the given location
	 * @param location The location
	 * @param particle A particle to create
	 * @param color Color of a particle, might be null
	 */
	public static void createParticle(Location location, Particle particle, @Nullable Color color) {
		World world = location.getWorld();
		if(world == null) return;
		if(color == null || !hasColor(particle)) {
			world.spawnParticle(particle, location,1, 0, 0, 0, 0);
		} else {
			if(particle == Particle.REDSTONE) {
				Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
				world.spawnParticle(particle, location, 1, dustOptions);
			}
			if(particle == Particle.SPELL_MOB || particle == Particle.SPELL_MOB_AMBIENT) {
				double red = color.getRed() / 255D;
				double green = color.getGreen() / 255D;
				double blue = color.getBlue() / 255D;
				world.spawnParticle(particle, location, 0, red, green, blue, 1);
			}
		}
	}

	public static void createParticlesAround(Entity ent, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			double h = ent.getHeight();
			double w = ent.getWidth() / 1.5;
			createParticle(ent.getLocation().clone().add(MathUtils.randomRangeDouble(-w, w), MathUtils.randomRangeDouble(0, h), MathUtils.randomRangeDouble(-w, w)), effect, color);
		}
	}

	public static void createParticlesInsideSphere(Location l, double radius, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
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
			createParticle(l.clone().add(x, y, z), effect, color);
		}
	}

	public static void createParticlesOutlineSphere(Location l, double radius, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			double u = Math.random();
			double v = Math.random();
			double theta = 2 * Math.PI * u;
			double phi = Math.acos(2 * v - 1);
			double x = radius * Math.sin(phi) * Math.cos(theta);
			double y = radius * Math.sin(phi) * Math.sin(theta);
			double z = radius * Math.cos(phi);
			createParticle(l.clone().add(x, y, z), effect, color);
		}
	}

	public static void createParticlesInRange(Location l, double radius, Particle particle, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			createParticle(l.clone().add(
						MathUtils.randomRangeDouble(-radius, radius),
						MathUtils.randomRangeDouble(-radius, radius),
						MathUtils.randomRangeDouble(-radius, radius)),
					particle, color);
		}
	}

	public static void createParticlesInside(Block b, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			createParticle(b.getLocation().clone().add(MathUtils.randomRangeDouble(0, 1), MathUtils.randomRangeDouble(0, 1), MathUtils.randomRangeDouble(0, 1)), effect, color);
		}
	}

	public static void createParticlesOutline(Block b, Particle effect, Color color, int amount) {
		for(int i = 0; i < amount; i++) {
			createParticle(getOutlineLocation(b.getLocation().clone().add(0.5, 0.5, 0.5), 0.6), effect, color);
		}
	}

	private static Location getOutlineLocation(Location l, double r) {
		double x = 0, y = 0, z = 0;
		int rand = MathUtils.randomRange(1, 3);
		switch(rand) {
			case 1 -> x = MathUtils.choose(r, -r);
			case 2 -> y = MathUtils.choose(r, -r);
			case 3 -> z = MathUtils.choose(r, -r);
		}
		if(x != 0) {
			y = MathUtils.randomRangeDouble(-r, r);
			z = MathUtils.randomRangeDouble(-r, r);
		}
		if(y != 0) {
			x = MathUtils.randomRangeDouble(-r, r);
			z = MathUtils.randomRangeDouble(-r, r);
		}
		if(z != 0) {
			x = MathUtils.randomRangeDouble(-r, r);
			y = MathUtils.randomRangeDouble(-r, r);
		}
		return l.clone().add(x, y, z);
	}

	public static ChatColor toChatColor(Color c) {
		if(c == Color.BLACK) {
			return ChatColor.BLACK;
		}
		if(c == Color.WHITE) {
			return ChatColor.WHITE;
		}
		if(c == Color.ORANGE) {
			return ChatColor.GOLD;
		}
		if(c == Color.YELLOW) {
			return ChatColor.YELLOW;
		}
		if(c == Color.AQUA) {
			return ChatColor.AQUA;
		}
		if(c == Color.BLUE) {
			return ChatColor.BLUE;
		}
		if(c == Color.FUCHSIA) {
			return ChatColor.LIGHT_PURPLE;
		}
		if(c == Color.PURPLE) {
			return ChatColor.DARK_PURPLE;
		}
		if(c == Color.GRAY) {
			return ChatColor.DARK_GRAY;
		}
		if(c == Color.GREEN) {
			return ChatColor.DARK_GREEN;
		}
		if(c == Color.LIME) {
			return ChatColor.GREEN;
		}
		if(c == Color.MAROON) {
			return ChatColor.DARK_RED;
		}
		if(c == Color.NAVY) {
			return ChatColor.DARK_BLUE;
		}
		if(c == Color.OLIVE) {
			return ChatColor.DARK_GREEN;
		}
		if(c == Color.RED) {
			return ChatColor.RED;
		}
		if(c == Color.SILVER) {
			return ChatColor.GRAY;
		}
		if(c == Color.TEAL) {
			return ChatColor.DARK_AQUA;
		}
		return null;
	}

	public static Color toColor(ChatColor c) {
		if(c == ChatColor.BLACK) {
			return Color.BLACK;
		}
		if(c == ChatColor.WHITE) {
			return Color.WHITE;
		}
		if(c == ChatColor.GOLD) {
			return Color.ORANGE;
		}
		if(c == ChatColor.YELLOW) {
			return Color.YELLOW;
		}
		if(c == ChatColor.AQUA) {
			return Color.AQUA;
		}
		if(c == ChatColor.BLUE) {
			return Color.BLUE;
		}
		if(c == ChatColor.LIGHT_PURPLE) {
			return Color.FUCHSIA;
		}
		if(c == ChatColor.DARK_PURPLE) {
			return Color.PURPLE;
		}
		if(c == ChatColor.DARK_GRAY) {
			return Color.GRAY;
		}
		if(c == ChatColor.DARK_GREEN) {
			return Color.GREEN;
		}
		if(c == ChatColor.GREEN) {
			return Color.LIME;
		}
		if(c == ChatColor.DARK_RED) {
			return Color.MAROON;
		}
		if(c == ChatColor.DARK_BLUE) {
			return Color.NAVY;
		}
		if(c == ChatColor.RED) {
			return Color.RED;
		}
		if(c == ChatColor.GRAY) {
			return Color.SILVER;
		}
		if(c == ChatColor.DARK_AQUA) {
			return Color.TEAL;
		}
		return null;
	}

}
