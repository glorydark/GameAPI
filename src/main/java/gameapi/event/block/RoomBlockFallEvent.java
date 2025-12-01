package gameapi.event.block;

import cn.nukkit.block.Block;
import gameapi.room.Room;

public class RoomBlockFallEvent extends RoomBlockEvent {

    public RoomBlockFallEvent(Room room, Block block) {
        super(room, block);
    }
}
