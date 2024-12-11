package gameapi.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockIgniteEvent;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomBlockIgniteEvent extends RoomBlockEvent implements Cancellable {

    private final Block source;
    private final Entity entity;
    private final BlockIgniteEvent.BlockIgniteCause cause;

    public RoomBlockIgniteEvent(Room room, Block block, Block source, Entity entity, BlockIgniteEvent.BlockIgniteCause cause) {
        super(room, block);
        this.source = source;
        this.entity = entity;
        this.cause = cause;
    }

    public Block getSource() {
        return this.source;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public BlockIgniteEvent.BlockIgniteCause getCause() {
        return this.cause;
    }
}
