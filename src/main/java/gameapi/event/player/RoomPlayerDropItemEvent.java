package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerDropItemEvent extends RoomPlayerEvent implements Cancellable {

    protected Item item;

    public RoomPlayerDropItemEvent(Room room, Player player, Item item) {
        super(room, player);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

}
