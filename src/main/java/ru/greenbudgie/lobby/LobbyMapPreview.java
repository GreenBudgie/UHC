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
import java.util.*;
import java.util.List;

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
        UHCPlugin.info("[MapPreview] init: region loaded, start=" +
                previewRegion.getStartLocation().getBlockX() + "," +
                previewRegion.getStartLocation().getBlockY() + "," +
                previewRegion.getStartLocation().getBlockZ() +
                " size=" + previewRegion.getXSideLength() + "x" +
                previewRegion.getYSideLength() + "x" + previewRegion.getZSideLength());
    }

    /**
     * Fill the item frames at specified region with the maps of
     * current game world
     */
    public static void setPreview() {
        UHCPlugin.info("[MapPreview] setPreview() called");
        if(mapPreviewRegion == null) {
            UHCPlugin.warning("[MapPreview] setPreview aborted: mapPreviewRegion is null");
            return;
        }
        if(WorldManager.getGameMap() == null) {
            UHCPlugin.warning("[MapPreview] setPreview aborted: game map is null");
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

        UHCPlugin.info("[MapPreview] grid=" + xLength + "x" + yLength +
                ", worldCenter=" + worldCenter.getBlockX() + "," + worldCenter.getBlockZ() +
                ", scaling=" + scaling + ", chunkSize=" + chunkSize);

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
        UHCPlugin.info("[MapPreview] force-loaded " + lobbyChunksLoaded.size() + " lobby chunks for frame scan");

        int framesFound = 0;
        int framesMissing = 0;
        for(int x = 0; x < xLength; x++) {
            for(int y = 0; y < yLength; y++) {
                Location realLocation = computeFrameLocation(x, y, doesXChange, doesYChange, doesZChange);
                ItemFrame itemFrame = getItemFrameAt(realLocation);
                if(itemFrame == null) {
                    framesMissing++;
                    UHCPlugin.warning("[MapPreview] no ItemFrame at " +
                            realLocation.getBlockX() + "," + realLocation.getBlockY() + "," + realLocation.getBlockZ());
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
                ItemStack chunkMap = getMapWithRenderedRegion(currentMapCenterLocation, scaling);
                itemFrame.setItem(chunkMap);

            }
        }
        UHCPlugin.info("[MapPreview] setPreview done: frames found=" + framesFound + ", missing=" + framesMissing);
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
    private static ItemStack getMapWithRenderedRegion(Location center, double scaling) {
        double scalingShift = 64 / scaling;
        World world = center.getWorld();
        MapView view = Bukkit.createMap(world);
        List<MapRenderer> existingRenderers = new ArrayList<>(view.getRenderers());
        for(MapRenderer renderer : existingRenderers) {
            view.removeRenderer(renderer);
        }
        int centerX = (int) Math.round(center.getBlockX() - scalingShift);
        int centerZ = (int) Math.round(center.getBlockZ() - scalingShift);
        view.setCenterX(centerX);
        view.setCenterZ(centerZ);

        byte[] buffer = computeBuffer(world, scaling, centerX, centerZ, view.getId());
        view.addRenderer(new CustomRenderer(buffer));

        UHCPlugin.info("[MapPreview] created MapView id=" + view.getId() +
                ", centerX=" + centerX + ", centerZ=" + centerZ +
                ", world=" + (world == null ? "null" : world.getName()) +
                ", removedRenderers=" + existingRenderers.size());

        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setMapView(view);
        item.setItemMeta(meta);
        return item;
    }

    private static byte[] computeBuffer(World world, double scaling, int centerX, int centerZ, int mapId) {
        long start = System.currentTimeMillis();
        int minChunkX = centerX >> 4;
        int maxChunkX = (int) Math.round(centerX + 127 / scaling) >> 4;
        int minChunkZ = centerZ >> 4;
        int maxChunkZ = (int) Math.round(centerZ + 127 / scaling) >> 4;
        int chunksLoaded = 0;
        for(int cx = minChunkX; cx <= maxChunkX; cx++) {
            for(int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                if(!world.isChunkLoaded(cx, cz)) {
                    world.loadChunk(cx, cz, true);
                    chunksLoaded++;
                }
            }
        }
        long afterLoad = System.currentTimeMillis();
        byte[] pixels = new byte[128 * 128];
        for(int x = 0; x < 128; x++) {
            for(int z = 0; z < 128; z++) {
                double scaledX = x / scaling;
                double scaledZ = z / scaling;
                int realX = (int) Math.round(scaledX + centerX);
                int realZ = (int) Math.round(scaledZ + centerZ);
                Block block = world.getHighestBlockAt(realX, realZ);
                pixels[x + z * 128] = getBlockColor(block);
            }
        }
        UHCPlugin.info("[MapPreview] buffer(id=" + mapId + "): chunks preloaded=" + chunksLoaded +
                " (" + (afterLoad - start) + "ms), pixels sampled in " +
                (System.currentTimeMillis() - afterLoad) + "ms");
        return pixels;
    }

    @SuppressWarnings("deprecation")
    private static byte getBlockColor(Block block) {
        org.bukkit.Color bukkitColor = block.getBlockData().getMapColor();
        Color color = new Color(bukkitColor.getRed(), bukkitColor.getGreen(), bukkitColor.getBlue());
        return MapPalette.matchColor(color);
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
            UHCPlugin.info("[MapPreview] render(id=" + map.getId() + "): blitted pre-computed buffer");
        }

    }

}
