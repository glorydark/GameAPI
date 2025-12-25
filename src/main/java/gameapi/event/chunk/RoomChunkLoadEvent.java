package gameapi.event.chunk;

import cn.nukkit.level.format.FullChunk;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomChunkLoadEvent extends RoomChunkEvent implements Cancellable {

    private final boolean newChunk;

    public RoomChunkLoadEvent(Room room, FullChunk chunk, boolean newChunk) {
        super(room, chunk);
        this.newChunk = newChunk;
    }

    public boolean isNewChunk() {
        return newChunk;
    }
}
