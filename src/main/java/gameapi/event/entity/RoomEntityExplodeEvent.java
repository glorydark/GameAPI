package gameapi.event.entity;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import gameapi.event.Cancellable;
import gameapi.room.Room;

import java.util.List;

/**
 * @author glorydark
 * @date {2023/12/30} {19:59}
 */
public class RoomEntityExplodeEvent extends RoomEntityEvent implements Cancellable {

    protected final Position position;
    protected List<Block> blocks;
    protected double yield;

    public RoomEntityExplodeEvent(Room room, Entity entity, Position position, List<Block> blocks, double yield) {
        super(room, entity);
        this.entity = entity;
        this.position = position;
        this.blocks = blocks;
        this.yield = yield;
    }

    public Position getPosition() {
        return this.position;
    }

    public List<Block> getBlockList() {
        return this.blocks;
    }

    public void setBlockList(List<Block> blocks) {
        this.blocks = blocks;
    }

    public double getYield() {
        return this.yield;
    }

    public void setYield(double yield) {
        this.yield = yield;
    }
}
