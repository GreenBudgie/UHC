package ru.greenbudgie.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a region between two {@link Location locations}.
 * Every method in this class is represented by integer (block) coordinates.
 * <p>
 * Region itself and its locations are immutable.
 * They are cloned in constructor and their clones are returned from getters.
 * <p>
 * A region might not have a world specified. It can still perform some calculations based on coordinates,
 * but it cannot get the data from world: blocks, entities, particles e.t.c.
 */
public class Region implements ConfigurationSerializable {

	private final Location start;
    private final Location end;
	private int x1, y1, z1, x2, y2, z2;
	private final boolean hasWorld;

	/**
	 * Creates a new region using start and end locations. Worlds in these locations can be null
	 * @param start Start location
	 * @param end End Location
	 */
	public Region(Location start, Location end) {
		if(start == null || end == null)
			throw new IllegalArgumentException("Cannot create a region: start and end locations cannot be null");
		this.start = start.clone();
		this.end = end.clone();
		setCoordinates();
		hasWorld = start.getWorld() != null && end.getWorld() != null && (start.getWorld() == end.getWorld());
	}

	/**
	 * Deserializes a region from the given config. If config is invalid it returns null
	 * @param args Config
	 * @param world The world with which to create this region. If null, it will be set from config.
	 *              If the config does not provide the world, it will be null.
	 * @return Deserialized region, or null
	 */
	@Nullable
	public static Region deserialize(Map<String, Object> args, @Nullable World world) {
		Location start, end;

		try {
			if(args.containsKey("start")) {
				String str = String.valueOf(args.get("start"));
				String[] pos = str.split(" ");
				if(pos.length == 3) {
					start = new Location(world, Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));
				} else return null;
			} else return null;
			if(args.containsKey("end")) {
				String str = String.valueOf(args.get("end"));
				String[] pos = str.split(" ");
				if(pos.length == 3) {
					end = new Location(world, Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));
				} else return null;
			} else return null;
			if(args.containsKey("world")) {
				World worldFromConfig = Bukkit.getWorld(String.valueOf(args.get("world")));
				start.setWorld(worldFromConfig);
				end.setWorld(worldFromConfig);
			}
            return new Region(start, end);
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Gets the world of the region
	 * @return World of the region, or null if was not specified on region creation
	 */
	@Nullable
	public World getWorld() {
		if(hasWorld) {
			return start.getWorld();
		}
		return null;
	}

	/**
	 * Gets the random location inside a region. If the region has a world, it will be set to this location.
	 * @return Random location inside a region
	 */
	public Location getRandomInsideLocation() {
		return new Location(
				getWorld(),
				MathUtils.randomRangeDouble(x1, x2 + 1),
				MathUtils.randomRangeDouble(y1, y2 + 1),
				MathUtils.randomRangeDouble(z1, z2 + 1)
		);
	}

	/**
	 * Gets the random location of a block (integer) inside a region.
	 * If the region has a world, it will be set to this location.
	 * @return Random block location inside a region
	 */
	public Location getRandomInsideBlockLocation() {
		return new Location(
				getWorld(),
				MathUtils.randomRange(x1, x2),
				MathUtils.randomRange(y1, y2),
				MathUtils.randomRange(z1, z2)
		);
	}

	/**
	 * Gets the random location of an air block (integer) inside a region.
	 * If the region has a world, it will be set to this location.
	 * @return Random air block location inside a region
	 */
	public Location getRandomInsideAirBlockLocation() {
		return MathUtils.choose(getAirBlocksInside()).getLocation();
	}

	/**
	 * Gets the start (min) X location
	 * @return Start X location
	 */
	public int getX1() {
		return x1;
	}

	/**
	 * Gets the start (min) Y location
	 * @return Start Y location
	 */
	public int getY1() {
		return y1;
	}

	/**
	 * Gets the start (min) Z location
	 * @return Start Z location
	 */
	public int getZ1() {
		return z1;
	}

	/**
	 * Gets the end (max) X location
	 * @return End X location
	 */
	public int getX2() {
		return x2;
	}

	/**
	 * Gets the end (max) Y location
	 * @return End Y location
	 */
	public int getY2() {
		return y2;
	}

	/**
	 * Gets the end (max) Z location
	 * @return End Z location
	 */
	public int getZ2() {
		return z2;
	}

	/**
	 * Gets a copy of the start location
	 * @return Copy of start location
	 */
	public Location getStartLocation() {
		return start.clone();
	}

	/**
	 * Gets a copy the end location
	 * @return Copy of end location
	 */
	public Location getEndLocation() {
		return end.clone();
	}

	public void validateWorlds() {
		if(!hasWorld) {
			throw new IllegalStateException("Worlds must be present in locations to use this method!");
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("start", x1 + " " + y1 + " " + z1);
		result.put("end", x2 + " " + y2 + " " + z2);
		if(hasWorld) result.put("world", start.getWorld().getName());
		return result;
	}

	/**
	 * Writes the coordinates to variables to maintain region easier
	 */
	void setCoordinates() {
		x1 = Math.min(start.getBlockX(), end.getBlockX());
		y1 = Math.min(start.getBlockY(), end.getBlockY());
		z1 = Math.min(start.getBlockZ(), end.getBlockZ());
		x2 = Math.max(start.getBlockX(), end.getBlockX());
		y2 = Math.max(start.getBlockY(), end.getBlockY());
		z2 = Math.max(start.getBlockZ(), end.getBlockZ());
		start.setX(x1);
		start.setY(y1);
		start.setZ(z1);
		end.setX(x2);
		end.setY(y2);
		end.setZ(z2);
	}

	private ListMultimap<Location, Location> faces;

	/**
	 * Gets the faces of the region
	 * @return A multimap containing linked locations representing the start and the end locations of the face rectangle
	 */
	public ListMultimap<Location, Location> getFaces() {
		if (faces != null) {
			return faces;
		}
		ListMultimap<Location, Location> map = ArrayListMultimap.create();
		Location start = this.start.clone();

		map.put(start, start.clone().add(getXSideLength(), getHeight(), 0));
		map.put(start, start.clone().add(0, getHeight(), getZSideLength()));
		map.put(start, start.clone().add(getXSideLength(), 0, getZSideLength()));
		Location end = start.clone().add(getXSideLength(), getHeight(), getZSideLength());
		map.put(start.clone().add(getXSideLength(), 0, 0), end);
		map.put(start.clone().add(0, getHeight(), 0), end);
		map.put(start.clone().add(0, 0, getZSideLength()), end);

		faces = map;
		return map;
	}

	private ListMultimap<Location, Location> edges;

	/**
	 * Gets the edges of the region
	 * @return A multimap containing linked locations representing line segments of each edge
	 */
	public ListMultimap<Location, Location> getEdges() {
		if (edges != null) {
			return edges;
		}
		ListMultimap<Location, Location> map = ArrayListMultimap.create();
		Location start = this.start.clone();

		if(isPoint()) {
			edges = map;
			return map;
		}
		if(isLine()) {
			map.put(this.start.clone(), end.clone());
			edges = map;
			return map;
		}
		if(isFlat()) {
			map.put(start.clone(), start.clone().add(getXSideLength(), 0, 0));
			start.add(getXSideLength(), 0, 0);
			map.put(start.clone(), start.clone().add(0, 0, getZSideLength()));
			start.add(0, 0, getZSideLength());
			map.put(start.clone(), start.clone().add(-getXSideLength(), 0, 0));
			start.add(-getXSideLength(), 0, 0);
			map.put(start.clone(), start.clone().add(0, 0, -getZSideLength()));
			edges = map;
			return map;
		}

		map.put(start.clone(), start.clone().add(0, getHeight(), 0));
		map.put(start.clone(), start.clone().add(getXSideLength(), 0, 0));

		start.add(getXSideLength(), 0, 0);

		map.put(start.clone(), start.clone().add(0, getHeight(), 0));
		map.put(start.clone(), start.clone().add(0, 0, getZSideLength()));

		start.add(0, 0, getZSideLength());

		map.put(start.clone(), start.clone().add(0, getHeight(), 0));
		map.put(start.clone(), start.clone().add(-getXSideLength(), 0, 0));

		start.add(-getXSideLength(), 0, 0);

		map.put(start.clone(), start.clone().add(0, getHeight(), 0));
		map.put(start.clone(), start.clone().add(0, 0, -getZSideLength()));

		start.add(0, getHeight(), -getZSideLength());

		map.put(start.clone(), start.clone().add(getXSideLength(), 0, 0));
		start.add(getXSideLength(), 0, 0);

		map.put(start.clone(), start.clone().add(0, 0, getZSideLength()));
		start.add(0, 0, getZSideLength());

		map.put(start.clone(), start.clone().add(-getXSideLength(), 0, 0));
		start.add(-getXSideLength(), 0, 0);

		map.put(start.clone(), start.clone().add(0, 0, -getZSideLength()));

		edges = map;
		return map;
	}

	/**
	 * Gets the length of the X-Axis side, in other words the count of blocks at this side
	 * @return Length of the X-Axis side
	 */
	public int getXSideLength() {
		return Math.abs(x2 - x1) + 1;
	}

	/**
	 * Gets the length of the Y-Axis side, in other words the count of blocks at this side
	 * @return Length of the Y-Axis side
	 */
	public int getYSideLength() {
		return Math.abs(y2 - y1) + 1;
	}

	/**
	 * Gets the length of the Z-Axis side, in other words the count of blocks at this side
	 * @return Length of the Z-Axis side
	 */
	public int getZSideLength() {
		return Math.abs(z2 - z1) + 1;
	}

	/**
	 * Gets the height of the region, or length of the Y-Axis side, in other words the count of blocks at this side
	 * @return Height of the region
	 */
	public int getHeight() {
		return Math.abs(y2 - y1) + 1;
	}

	/**
	 * Checks if the region represents a parallelepiped
	 * @return Whether the region is a 3D shape (parallelepiped)
	 */
	public boolean is3D() {
		return x1 != x2 && y1 != y2 && z1 != z2;
	}

	/**
	 * Checks if the region is flat by one of the axes
	 * @return Whether the region is a 2D shape (rectangle)
	 */
	public boolean isFlat() {
		return (x1 == x2 && y1 != y2 && z1 != z2) || (x1 != x2 && y1 == y2 && z1 != z2) || (x1 != x2 && y1 != y2 && z1 == z2);
	}

	/**
	 * Checks if the region represents a line
	 * @return Whether the region is a 1D shape (line)
	 */
	public boolean isLine() {
		return (x1 == x2 && y1 == y2 && z1 != z2) || (x1 != x2 && y1 == y2 && z1 == z2) || (x1 == x2 && y1 != y2 && z1 == z2);
	}

	/**
	 * Checks if the region represents a single point (start location coordinates are equal to end location coordinates)
	 * @return Whether the region is a point
	 */
	public boolean isPoint() {
		return x1 == x2 && y1 == y2 && z1 == z2;
	}

	/**
	 * Gets the amount of blocks inside or on the edges of a region
	 * @return Amount of blocks
	 */
	public int getBlockCount() {
		return getXSideLength() * getHeight() * getZSideLength();
	}

	private Set<Location> locationsInside;

	/**
	 * Gets the list of locations that are inside or on the edges of a region.
	 * @return Set of locations
	 */
	public Set<Location> getLocationsInside() {
		if (locationsInside != null) {
			return locationsInside;
		}
		Set<Location> locations = new HashSet<>();
		for(int x = x1; x <= x2; x++) {
			for(int y = y1; y <= y2; y++) {
				for(int z = z1; z <= z2; z++) {
					locations.add(new Location(start.getWorld(), x, y, z));
				}
			}
		}
		locationsInside = locations;
		return locations;
	}

	/**
	 * Gets the list of blocks that are inside or on the edges of a region.
	 * This method requires a world to be present.
	 * @return Set of blocks
	 */
	public Set<Block> getBlocksInside() {
		validateWorlds();
		return getLocationsInside().stream().map(Location::getBlock).collect(Collectors.toSet());
	}

	/**
	 * Gets the list of air blocks that are inside or on the edges of a region.
	 * This method requires a world to be present.
	 * @return Set of air blocks
	 */
	public Set<Block> getAirBlocksInside() {
		validateWorlds();
		return getLocationsInside().stream()
				.map(Location::getBlock)
				.filter(block -> block.getType() == Material.AIR)
				.collect(Collectors.toSet());
	}

	/**
	 * Gets the list of entities that are inside or on the edges of a region.
	 * This method requires a world to be present.
	 * @return Set of entities
	 */
	public Set<Entity> getEntitiesInside() {
		validateWorlds();
        return getWorld().getEntities().stream()
				.filter(entity -> isInside(entity.getLocation()))
				.collect(Collectors.toSet());
	}

	/**
	 * Checks if the given entity is inside the region. This method does not check worlds!
	 * @param entity The Entity
	 * @return Whether the given entity is inside the region
	 */
	public boolean isInside(Entity entity) {
		return isInside(entity.getLocation());
	}

	/**
	 * Checks if the given location is inside the region. This method does not check worlds!
	 * @param location The location
	 * @return Whether the given location is inside the region
	 */
	public boolean isInside(Location location) {
		int x = location.getBlockX();
		if (x < x1 || x > x2) {
			return false;
		}
		int y = location.getBlockY();
		if (y < y1 || y > y2) {
			return false;
		}
		int z = location.getBlockZ();
        return z >= z1 && z <= z2;
    }

}