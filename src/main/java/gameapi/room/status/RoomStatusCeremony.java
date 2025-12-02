package gameapi.room.status;

import gameapi.event.room.RoomCeremonyEvent;
import gameapi.event.room.RoomCeremonyTickEvent;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.status.base.CustomRoomStatus;
import gameapi.room.status.base.InternalRoomStatus;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import gameapi.room.utils.reason.ResetAllReason;

/**
 * @author glorydark
 */
public class RoomStatusCeremony extends InternalRoomStatus {

    public RoomStatusCeremony() {
        super(RoomDefaultStatusFactory.ROOM_STATUS_CEREMONY_ID, true);
    }

    @Override
    public void onEnter(Room room) {
        new RoomCeremonyEvent(room).call();
        room.getStatusExecutor().beginCeremony();
    }

    @Override
    public boolean onTick(Room room) {
        if (!this.hasEnoughPlayer(room)) {
            room.resetAll(ResetAllReason.NO_ENOUGH_PLAYERS);
            return true;
        }
        GameListenerRegistry.callEvent(room, new RoomCeremonyTickEvent(room));
        this.onStateUpdate(room);
        return false;
    }

    public void onStateUpdate(Room room) {
        if (room.getTime() >= room.getCeremonyTime()) {
            CustomRoomStatus status = this.getNextRoomStatus(room);
            if (status == null) {
                status = RoomDefaultStatusFactory.ROOM_STATUS_ROOM_END;
            }
            room.setCurrentRoomStatus(status, "internal");
        } else {
            room.getStatusExecutor().onCeremony();
            room.setTime(room.getTime() + 1);
        }
    }

    @Override
    public int getTime(Room room) {
        return room.getCeremonyTime();
    }
}
