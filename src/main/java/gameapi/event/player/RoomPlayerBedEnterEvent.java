package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomPlayerBedEnterEvent extends RoomPlayerEvent {

    private final Block bed;


    public RoomPlayerBedEnterEvent(Room room, Player player, Block bed) {
        super(room, player);
        this.bed = bed;
    }

    public Block getBed() {
        return bed;
    }
}
