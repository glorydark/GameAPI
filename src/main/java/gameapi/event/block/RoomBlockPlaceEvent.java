package gameapi.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import gameapi.event.Cancellable;
import gameapi.room.Room;

public class RoomBlockPlaceEvent extends RoomBlockEvent implements Cancellable {

    protected Item item;

    protected Player player;

    protected Block blockReplace;

    protected Block blockAgainst;

    public RoomBlockPlaceEvent(Room room, Block block, Player player, Item item, Block blockReplace, Block blockAgainst) {
        this.room = room;
        this.player = player;
        this.block = block;
        this.item = item;
        this.blockReplace = blockReplace;
        this.blockAgainst = blockAgainst;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getItem() {
        return item;
    }

    public Block getBlockAgainst() {
        return blockAgainst;
    }

    public Block getBlockReplace() {
        return blockReplace;
    }
}
