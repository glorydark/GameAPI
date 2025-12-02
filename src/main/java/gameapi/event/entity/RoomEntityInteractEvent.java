package gameapi.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import gameapi.room.Room;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class RoomEntityInteractEvent extends RoomEntityEvent implements Cancellable {

    private final Block block;

    public RoomEntityInteractEvent(Room room, Entity entity, Block block) {
        super(room, entity);
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
