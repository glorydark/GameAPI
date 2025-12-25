package gameapi.event.chunk;

import cn.nukkit.level.format.FullChunk;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomChunkUnloadEvent extends RoomChunkEvent implements Cancellable {

    public RoomChunkUnloadEvent(Room room, FullChunk chunk) {
        super(room, chunk);
    }
}
