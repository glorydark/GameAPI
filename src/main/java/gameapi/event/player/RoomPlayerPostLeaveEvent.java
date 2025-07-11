package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.room.Room;
import gameapi.room.utils.reason.QuitRoomReason;

/**
 * @author Glorydark
 */
public class RoomPlayerPostLeaveEvent extends RoomPlayerEvent {

    private final QuitRoomReason reason;

    public RoomPlayerPostLeaveEvent(Room room, Player player, QuitRoomReason reason) {
        super(room, player);
        this.reason = reason;
    }

    public QuitRoomReason getReason() {
        return reason;
    }
}