package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import gameapi.room.Room;
import gameapi.room.utils.QuitRoomReason;

/**
 * @author Glorydark
 */
public class RoomPlayerLeaveEvent extends RoomPlayerEvent implements Cancellable {

    private final QuitRoomReason reason;

    public RoomPlayerLeaveEvent(Room room, Player player, QuitRoomReason reason) {
        super(room, player);
        this.reason = reason;
    }

    public QuitRoomReason getReason() {
        return reason;
    }
}
