package gameapi.room.status;

import gameapi.event.room.RoomReadyStartTickEvent;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.status.base.CustomRoomStatus;
import gameapi.room.status.base.InternalRoomStatus;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import gameapi.room.utils.reason.ResetAllReason;

/**
 * @author glorydark
 */
public class RoomStatusReadyStart extends InternalRoomStatus {

    public RoomStatusReadyStart() {
        super(RoomDefaultStatusFactory.ROOM_STATUS_READY_START_ID, true);
    }

    @Override
    public boolean onTick(Room room) {
        if (!this.hasEnoughPlayer(room)) {
            room.resetAll(ResetAllReason.NO_ENOUGH_PLAYERS);
        } else {
            GameListenerRegistry.callEvent(room, new RoomReadyStartTickEvent(room));
            this.onStateUpdate(room);
        }
        return false;
    }

    public void onStateUpdate(Room room) {
        if (room.getTime() >= room.getGameWaitTime()) {
            room.setRound(room.getRound() + 1);
            room.getStatusExecutor().beginGameStart();
            room.setStartMillis(System.currentTimeMillis());
            CustomRoomStatus status = this.getNextRoomStatus(room);
            if (status == null) {
                status = RoomDefaultStatusFactory.ROOM_STATUS_GAME_START;
            }
            room.setCurrentRoomStatus(status, "internal");
        } else {
            room.getStatusExecutor().onReadyStart();
            room.setTime(room.getTime() + 1);
        }
    }

    @Override
    public int getTime(Room room) {
        return room.getWaitTime();
    }

    @Override
    public boolean isAllowEntityDamagedByEntity(Room room) {
        return room.getRoomRule().isAllowAttackPlayerBeforeStart();
    }
}
