package gameapi.room.status;

import gameapi.GameAPI;
import gameapi.room.Room;
import gameapi.room.status.base.InternalRoomStatus;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import gameapi.room.utils.reason.ResetAllReason;

/**
 * @author glorydark
 */
public class RoomStatusPlayback extends InternalRoomStatus {

    public RoomStatusPlayback() {
        super(RoomDefaultStatusFactory.ROOM_PLAYBACK_ID, true);
    }

    @Override
    public boolean onTick(Room room) {
        if (room.getPlayersWithoutCreate().isEmpty()) {
            GameAPI.getGameDebugManager().info("Detect that temp room " + room.getRoomName() + " has no players whilst playback, start destroying...");
            room.resetAll(ResetAllReason.ROOM_PLAYBACK_LEAVE);
            return true;
        }
        return false;
    }

    @Override
    public int getTime(Room room) {
        return 10;
    }
}
