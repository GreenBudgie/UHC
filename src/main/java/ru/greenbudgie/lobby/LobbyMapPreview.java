package ru.greenbudgie.lobby;

import net.minecraft.world.level.material.MaterialMapColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;
import org.bukkit.util.NumberConversions;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.main.UHCLogger;
import ru.greenbudgie.util.Region;

import java.awt.*;
import java.util.Map;

public class LobbyMapPreview {

    private static Region mapPreviewRegion;
    protected static void init() {
        ConfigurationSection mapRegionSection = Lobby.getLobbyConfig().getConfigurationSection("mapPreviewRegion");
        if(mapRegionSection == null) {
            UHCLogger.sendWarning("No map preview region specified in config");
            return;
        }
        Map<String, Object> rawRegion = mapRegionSection.getValues(false);
        Region previewRegion = Region.deserialize(rawRegion);
        if(previewRegion == null) {
            UHCLogger.sendWarning("Invalid map preview region notation");
            return;
        }
        if(previewRegion.is3D()) {
            UHCLogger.sendWarning("Map preview region is not a flat area");
            return;
        }
        mapPreviewRegion = previewRegion;
    }

    /**
     * Fill the item frames at specified region with the maps of
     * current game world
     */
    public static void setPreview() {
        if(mapPreviewRegion == null || WorldManager.getGameMap() == null) return;
        mapPreviewRegion.setWorld(Lobby.getLobby());
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

        Location worldCenter = WorldManager.getGameMap().getSpawnLocation();

        int maxLength = Math.max(xLength, yLength);
        int chunksToShow = 12; //How many chunks in row to render on the entire preview
        double chunksPerMap = chunksToShow / (double) maxLength;

        double scaling = (1D / chunksPerMap) * 8;

        int chunkSize = (int) Math.round(128 / scaling);

        for(int x = 0; x < xLength; x++) {
            for(int y = 0; y < yLength; y++) {
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
                Location realLocation = mapPreviewRegion.getStartLocation().clone().
                        add(realXShift, realYShift, realZShift);
                ItemFrame itemFrame = getItemFrameAt(realLocation);
                if(itemFrame == null) continue;

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
        MapView view = Bukkit.createMap(center.getWorld());
        view.getRenderers().forEach(view::removeRenderer);
        view.setCenterX((int) Math.round(center.getBlockX() - scalingShift));
        view.setCenterZ((int) Math.round(center.getBlockZ() - scalingShift));
        view.addRenderer(new CustomRenderer(scaling));

        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setMapView(view);
        item.setItemMeta(meta);
        return item;
    }

    @SuppressWarnings("deprecation")
    private static byte getBlockColor(Block block) {
        var nmsBlock = CraftMagicNumbers.getBlock(block.getType());
        MaterialMapColor mapColor = nmsBlock.s();
        Color color = new Color(mapColor.al);
        return MapPalette.matchColor(color);
    }

    private static class CustomRenderer extends org.bukkit.map.MapRenderer {

        private boolean needToRender = true;
        /**
         * How many blocks to render per map pixel.
         * Larger values make map
         */
        private double scaling;

        protected CustomRenderer(double scaling) {
            this.scaling = scaling;
        }

        @Override
        public void render(MapView map, MapCanvas canvas, Player player) {
            if(needToRender) {
                for(int x = 0; x < 128; x++) {
                    for(int z = 0; z < 128; z++) {
                        double scaledX = x / scaling;
                        double scaledZ = z / scaling;
                        int realX = (int) Math.round(scaledX + map.getCenterX());
                        int realZ = (int) Math.round(scaledZ + map.getCenterZ());
                        World world = map.getWorld();
                        if(world != null) {
                            Block block = world.getHighestBlockAt(realX, realZ);
                            Location blockLocation = block.getLocation();
                            Location spawnLocation = map.getWorld().getSpawnLocation();
                            double distanceToSpawnSq =
                                    NumberConversions.square(blockLocation.getX() - spawnLocation.getX()) +
                                    NumberConversions.square(blockLocation.getZ() - spawnLocation.getZ());
                            byte color;
                            if(distanceToSpawnSq <= 16 / scaling) {
                                color = MapPalette.matchColor(Color.RED);
                            } else {
                                color = getBlockColor(block);
                            }
                            canvas.setPixel(x, z, color);
                        }
                    }
                }
                needToRender = false;
            }
        }

    }

}
