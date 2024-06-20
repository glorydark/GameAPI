package gameapi.event.block;

import cn.nukkit.block.Block;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

public abstract class RoomBlockEvent extends RoomEvent implements Cancellable {

    protected Block block;

    public RoomBlockEvent(Room room, Block block) {
        super(room);
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
