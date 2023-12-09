package ru.greenbudgie.UHC.configuration;

/**
 * A map size configuration represented as chunks per player.
 * For example, 25 chunks size means that world border will have a side of sqrt(25)=5 chunks when only one player
 * is logged in. For two players, it will be sqrt(25*2)=sqrt(50) chunks on side per player.
 */
public enum MapSize {

    SMALL(30),
    DEFAULT(45),
    BIG(60);

    private final int chunksPerPlayer;

    MapSize(int chunksPerPlayer) {
        this.chunksPerPlayer = chunksPerPlayer;
    }

    public int getChunksPerPlayer() {
        return chunksPerPlayer;
    }

    public double getWorldBorderSideSize(int numberOfPlayers) {
        return Math.sqrt(numberOfPlayers * chunksPerPlayer * BLOCKS_PER_CHUNK);
    }

    private static final int BLOCKS_PER_CHUNK = 16 * 16;

}
