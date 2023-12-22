package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerItemHeldEvent extends RoomPlayerEvent implements Cancellable {

    protected Item item;

    public RoomPlayerItemHeldEvent(Room room, Player player, Item item) {
        this.room = room;
        this.player = player;
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

}
