package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerInteractEvent extends RoomPlayerEvent implements Cancellable {

    protected Block block;
    protected Vector3 touchVector;
    protected BlockFace blockFace;
    protected Item item;
    protected PlayerInteractEvent.Action action;

    public RoomPlayerInteractEvent(Room room, Player player, Block block, Vector3 touchVector, BlockFace blockFace, Item item, PlayerInteractEvent.Action action) {
        this.room = room;
        this.player = player;
        this.block = block;
        this.touchVector = touchVector;
        this.blockFace = blockFace;
        this.item = item;
        this.action = action;
    }

    public Block getBlock() {
        return block;
    }

    public Vector3 getTouchVector() {
        return touchVector;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    public Item getItem() {
        return item;
    }

    public PlayerInteractEvent.Action getAction() {
        return action;
    }

}
