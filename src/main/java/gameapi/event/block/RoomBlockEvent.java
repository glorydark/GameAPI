package gameapi.event.block;

import cn.nukkit.block.Block;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;

public abstract class RoomBlockEvent extends RoomEvent implements Cancellable {

    protected Block block;

    public RoomBlockEvent() {
    }

    public Block getBlock() {
        return block;
    }
}
