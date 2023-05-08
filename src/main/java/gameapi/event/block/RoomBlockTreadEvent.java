package gameapi.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import gameapi.room.Room;

public class RoomBlockTreadEvent extends RoomBlockEvent {

    protected Player player;

    public RoomBlockTreadEvent(Room room, Block block, Player player) {
        this.room = room;
        this.player = player;
        this.block = block;
    }

    public Player getPlayer() {
        return player;
    }
}
