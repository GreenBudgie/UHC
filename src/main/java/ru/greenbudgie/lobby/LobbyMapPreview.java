package ru.greenbudgie.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.Region;
import ru.greenbudgie.util.WorldHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LobbyMapPreview {

    private static Region mapPreviewRegion;

    public static void init() {
        ConfigurationSection mapRegionSection = Lobby.getLobbyConfig().getConfigurationSection("mapPreviewRegion");
        if(mapRegionSection == null) {
            UHCPlugin.warning("No map preview region specified in config");
            return;
        }
        Map<String, Object> rawRegion = mapRegionSection.getValues(false);
        Region previewRegion = Region.deserialize(rawRegion, Lobby.getLobby());
        if(previewRegion == null) {
            UHCPlugin.warning("Invalid map preview region notation");
            return;
        }
        if(previewRegion.is3D()) {
            UHCPlugin.warning("Map preview region is not a flat area");
            return;
        }
        mapPreviewRegion = previewRegion;
    }

    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final long RETRY_DELAY_TICKS = 20L;

    /**
     * Fill the item frames at specified region with the maps of
     * current game world
     */
    public static void setPreview() {
        setPreview(0);
    }

    private static void setPreview(int attempt) {
        if(mapPreviewRegion == null) {
            return;
        }
        if(WorldManager.getGameMap() == null) {
            return;
        }
        int xRealLength = mapPreviewRegion.getXSideLength();
        int yRealLength = mapPreviewRegion.getYSideLength();
        int zRealLength = mapPreviewRegion.getZSideLength();
        boolean doesXChange = xRealLength != 1;
        boolean doesYChange = yRealLength != 1;
        boolean doesZChange = zRealLength != 1;
        int xLength = 1, yLength = 1;
        if(!doesXChange) {
            xLength = zRealLength;
            yLength = yRealLength;
        }
        if(!doesYChange) {
            xLength = xRealLength;
            yLength = zRealLength;
        }
        if(!doesZChange) {
            xLength = xRealLength;
            yLength = yRealLength;
        }

        Location worldCenter = WorldHelper.getSpawnLocation(WorldManager.getGameMap());

        int maxLength = Math.max(xLength, yLength);
        int chunksToShow = 12; //How many chunks in row to render on the entire preview
        double chunksPerMap = chunksToShow / (double) maxLength;

        double scaling = (1D / chunksPerMap) * 8;

        int chunkSize = (int) Math.round(128 / scaling);

        World lobbyWorld = mapPreviewRegion.getStartLocation().getWorld();
        Set<Long> lobbyChunksLoaded = new HashSet<>();
        for(int x = 0; x < xLength; x++) {
            for(int y = 0; y < yLength; y++) {
                Location loc = computeFrameLocation(x, y, doesXChange, doesYChange, doesZChange);
                int cx = loc.getBlockX() >> 4;
                int cz = loc.getBlockZ() >> 4;
                long key = (((long) cx) << 32) | (cz & 0xFFFFFFFFL);
                if(lobbyChunksLoaded.add(key)) {
                    lobbyWorld.loadChunk(cx, cz, false);
                }
            }
        }

        int framesFound = 0;
        for(int x = 0; x < xLength; x++) {
            for(int y = 0; y < yLength; y++) {
                Location realLocation = computeFrameLocation(x, y, doesXChange, doesYChange, doesZChange);
                ItemFrame itemFrame = getItemFrameAt(realLocation);
                if(itemFrame == null) {
                    continue;
                }
                framesFound++;

                BlockFace face = itemFrame.getAttachedFace().getOppositeFace();
                int xSign = 1;
                if(face == BlockFace.EAST || face == BlockFace.NORTH) xSign = -1;
                int ySign = -1;
                if(face == BlockFace.UP) ySign = 1;

                int mapXShift = xSign * chunkSize * (x - xLength / 2);
                int mapYShift = ySign * chunkSize * (y - yLength / 2);
                Location currentMapCenterLocation = worldCenter.clone().add(mapXShift, 0, mapYShift);
                ItemStack chunkMap = getMapWithRenderedRegion(currentMapCenterLocation, scaling, itemFrame);
                itemFrame.setItem(chunkMap);

            }
        }
        if(framesFound == 0 && attempt + 1 < MAX_RETRY_ATTEMPTS) {
            Bukkit.getScheduler().runTaskLater(UHCPlugin.instance, () -> setPreview(attempt + 1), RETRY_DELAY_TICKS);
        }
    }

    private static Location computeFrameLocation(int x, int y, boolean doesXChange, boolean doesYChange, boolean doesZChange) {
        int realXShift = 0, realYShift = 0, realZShift = 0;
        if(!doesXChange) {
            realZShift = x;
            realYShift = y;
        }
        if(!doesYChange) {
            realXShift = x;
            realZShift = y;
        }
        if(!doesZChange) {
            realXShift = x;
            realYShift = y;
        }
        return mapPreviewRegion.getStartLocation().clone().add(realXShift, realYShift, realZShift);
    }

    private static ItemFrame getItemFrameAt(Location location) {
        for(Entity entity : location.getWorld().getNearbyEntities(location.clone().
                add(0.5, 0.5, 0.5), 0.5, 0.5, 0.5)) {
            if(entity instanceof ItemFrame itemFrame) return itemFrame;
        }
        return null;
    }

    /**
     * Gets the map with rendered region of 128*128 blocks.
     * @param center The center of the region
     * @return Map item
     */
    private static ItemStack getMapWithRenderedRegion(Location center, double scaling, ItemFrame existingFrame) {
        double scalingShift = 64 / scaling;
        World world = center.getWorld();

        MapView view = extractExistingMapView(existingFrame);
        if(view == null) {
            view = Bukkit.createMap(world);
        } else {
            view.setWorld(world);
        }

        for(MapRenderer renderer : new ArrayList<>(view.getRenderers())) {
            view.removeRenderer(renderer);
        }
        int centerX = (int) Math.round(center.getBlockX() - scalingShift);
        int centerZ = (int) Math.round(center.getBlockZ() - scalingShift);
        view.setCenterX(centerX);
        view.setCenterZ(centerZ);

        byte[] buffer = computeBuffer(world, scaling, centerX, centerZ);
        view.addRenderer(new CustomRenderer(buffer));

        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setMapView(view);
        item.setItemMeta(meta);
        return item;
    }

    private static MapView extractExistingMapView(ItemFrame frame) {
        ItemStack currentItem = frame.getItem();
        if(currentItem.getType() != Material.FILLED_MAP) {
            return null;
        }
        if(!currentItem.hasItemMeta()) {
            return null;
        }
        MapMeta meta = (MapMeta) currentItem.getItemMeta();
        if(!meta.hasMapView()) {
            return null;
        }
        return meta.getMapView();
    }

    private static byte[] computeBuffer(World world, double scaling, int centerX, int centerZ) {
        int minChunkX = centerX >> 4;
        int maxChunkX = (int) Math.round(centerX + 127 / scaling) >> 4;
        int minChunkZ = centerZ >> 4;
        int maxChunkZ = (int) Math.round(centerZ + 127 / scaling) >> 4;
        for(int cx = minChunkX; cx <= maxChunkX; cx++) {
            for(int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if(!world.isChunkLoaded(cx, cz)) {
                    world.loadChunk(cx, cz, true);
                }
            }
        }
        int[] heights = new int[128 * 128];
        Color[] colors = new Color[128 * 128];
        for(int x = 0; x < 128; x++) {
            for(int z = 0; z < 128; z++) {
                double scaledX = x / scaling;
                double scaledZ = z / scaling;
                int realX = (int) Math.round(scaledX + centerX);
                int realZ = (int) Math.round(scaledZ + centerZ);
                Block block = world.getHighestBlockAt(realX, realZ);
                int idx = x + z * 128;
                heights[idx] = block.getY();
                org.bukkit.Color raw = block.getBlockData().getMapColor();
                colors[idx] = new Color(raw.getRed(), raw.getGreen(), raw.getBlue());
            }
        }
        byte[] pixels = new byte[128 * 128];
        for(int x = 0; x < 128; x++) {
            for(int z = 0; z < 128; z++) {
                int idx = x + z * 128;
                double brightness = 1.0;
                if(z > 0) {
                    int diff = heights[idx] - heights[x + (z - 1) * 128];
                    if(diff > 0) brightness = 1.18;
                    else if(diff < 0) brightness = 0.82;
                }
                pixels[idx] = shadeAndMatch(colors[idx], brightness);
            }
        }
        return pixels;
    }

    @SuppressWarnings("deprecation")
    private static byte shadeAndMatch(Color color, double brightness) {
        int r = clampByte((int) Math.round(color.getRed() * brightness));
        int g = clampByte((int) Math.round(color.getGreen() * brightness));
        int b = clampByte((int) Math.round(color.getBlue() * brightness));
        return MapPalette.matchColor(new Color(r, g, b));
    }

    private static int clampByte(int value) {
        return Math.min(255, Math.max(0, value));
    }

    private static class CustomRenderer extends org.bukkit.map.MapRenderer {

        private final byte[] buffer;
        private boolean applied = false;

        protected CustomRenderer(byte[] buffer) {
            super(false);
            this.buffer = buffer;
        }

        @Override
        public void render(MapView map, MapCanvas canvas, Player player) {
            if(applied) return;
            for(int x = 0; x < 128; x++) {
                for(int z = 0; z < 128; z++) {
                    canvas.setPixel(x, z, buffer[x + z * 128]);
                }
            }
            applied = true;
        }

    }

}
