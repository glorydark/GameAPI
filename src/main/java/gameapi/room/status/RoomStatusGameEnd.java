package gameapi.room.status;

import gameapi.event.room.RoomGameEndEvent;
import gameapi.event.room.RoomGameEndTickEvent;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.status.base.InternalRoomStatus;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import gameapi.room.utils.reason.ResetAllReason;

/**
 * @author glorydark
 */
public class RoomStatusGameEnd extends InternalRoomStatus {

    public RoomStatusGameEnd() {
        super(RoomDefaultStatusFactory.ROOM_STATUS_GAME_END_ID, true);
    }

    @Override
    public void onEnter(Room room) {
        new RoomGameEndEvent(room).call();
        room.getStatusExecutor().beginGameEnd();
    }

    @Override
    public boolean onTick(Room room) {
        if (!this.hasEnoughPlayerWithTeam(room)) {
            room.resetAll(ResetAllReason.NO_ENOUGH_PLAYERS);
            return true;
        }
        GameListenerRegistry.callEvent(room, new RoomGameEndTickEvent(room));
        return this.onStateUpdate(room);
    }

    public boolean onStateUpdate(Room room) {
        if (room.getTime() >= room.getGameEndTime()) {
            if (room.getRoomRule().isHasCeremony()) {
                room.setCurrentRoomStatus(RoomDefaultStatusFactory.ROOM_STATUS_CEREMONY, "internal");
            } else {
                room.setCurrentRoomStatus(RoomDefaultStatusFactory.ROOM_STATUS_ROOM_END, "internal");
                room.resetAll(ResetAllReason.ROOM_GAME_FINISH);
            }
            return true;
        } else {
            room.getStatusExecutor().onGameEnd();
            room.setTime(room.getTime() + 1);
        }
        return false;
    }

    @Override
    public int getTime(Room room) {
        return room.getGameEndTime();
    }
}