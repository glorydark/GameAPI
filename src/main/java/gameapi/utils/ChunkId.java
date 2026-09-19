package gameapi.utils;

/**
 * @author glorydark
 */
public record ChunkId(int x, int z) {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChunkId chunkId) {
            return chunkId.x == this.x && chunkId.z == this.z;
        }
        return false;
    }
}
