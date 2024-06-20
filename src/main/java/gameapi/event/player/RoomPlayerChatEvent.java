package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.room.Room;
import gameapi.room.RoomChatData;

public class RoomPlayerChatEvent extends RoomPlayerEvent implements Cancellable {

    protected RoomChatData roomChatData;

    public RoomPlayerChatEvent(Room room, Player player, RoomChatData roomChatData) {
        super(room, player);
        this.roomChatData = roomChatData;
    }

    public RoomChatData getRoomChatData() {
        return roomChatData;
    }
}
