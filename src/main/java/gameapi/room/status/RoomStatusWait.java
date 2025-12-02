package gameapi.room.status;

import gameapi.GameAPI;
import gameapi.event.room.RoomWaitTickEvent;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.status.base.CustomRoomStatus;
import gameapi.room.status.base.InternalRoomStatus;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import gameapi.room.utils.reason.ResetAllReason;

/**
 * @author glorydark
 */
public class RoomStatusWait extends InternalRoomStatus {

    public RoomStatusWait() {
        super(RoomDefaultStatusFactory.ROOM_STATUS_WAIT_ID, true);
    }

    @Override
    public boolean onTick(Room room) {
        if (room.isTemporary()) {
            if (room.isAutoDestroyOverTime()) {
                if (System.currentTimeMillis() >= room.getCreateMillis() + room.getMaxTempRoomWaitMillis()) {
                    GameAPI.getGameDebugManager().info("Detect that temp room " + room.getRoomName() + " has reached the maximum of waiting time, start destroying...");
                    room.resetAll(ResetAllReason.ROOM_AUTO_DESTROY);
                    return true;
                }
            }
        }
        GameListenerRegistry.callEvent(room, new RoomWaitTickEvent(room));
        this.onStateUpdate(room);
        return false;
    }

    public void onStateUpdate(Room room) {
        if (room.getPlayers().size() >= room.getMinPlayer()) {
            if (room.isAllowedToStart()) {
                CustomRoomStatus status = this.getNextRoomStatus(room);
                if (status == null) {
                    status = RoomDefaultStatusFactory.ROOM_STATUS_PRESTART;
                }
                room.setCurrentRoomStatus(status, "internal");
            } else {
                room.getStatusExecutor().onWait();
            }
        } else {
            room.getStatusExecutor().onWait();
        }
    }

    @Override
    public int getTime(Room room) {
        return 1;
    }
}
