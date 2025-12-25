package gameapi.event.chunk;

import cn.nukkit.level.format.FullChunk;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public abstract class RoomChunkEvent extends RoomEvent implements Cancellable {

    protected final FullChunk chunk;

    public RoomChunkEvent(Room room, FullChunk chunk) {
        super(room);
        this.chunk = chunk;
    }

    public FullChunk getChunk() {
        return chunk;
    }
}
