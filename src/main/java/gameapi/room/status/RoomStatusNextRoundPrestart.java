package gameapi.room.status;

import gameapi.event.room.RoomNextRoundPreStartEvent;
import gameapi.event.room.RoomNextRoundPreStartTickEvent;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.status.base.InternalRoomStatus;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import gameapi.room.utils.reason.ResetAllReason;

/**
 * @author glorydark
 */
public class RoomStatusNextRoundPrestart extends InternalRoomStatus {

    public RoomStatusNextRoundPrestart() {
        super(RoomDefaultStatusFactory.ROOM_STATUS_NEXT_ROUND_PRESTART_ID, true);
    }

    @Override
    public void onEnter(Room room) {
        new RoomNextRoundPreStartEvent(room).call();
        room.getStatusExecutor().beginNextRoundPreStart();
    }

    @Override
    public boolean onTick(Room room) {
        if (!this.hasEnoughPlayerWithTeam(room)) {
            room.resetAll(ResetAllReason.NO_ENOUGH_PLAYERS);
            return true;
        }
        GameListenerRegistry.callEvent(room, new RoomNextRoundPreStartTickEvent(room));
        this.onStateUpdate(room);
        return false;
    }

    public void onStateUpdate(Room room) {
        if (room.getTime() >= room.getNextRoundPreStartTime()) {
            room.setCurrentRoomStatus(RoomDefaultStatusFactory.ROOM_STATUS_GAME_START, "internal");
        } else {
            room.getStatusExecutor().onNextRoundPreStart();
            room.setTime(room.getTime() + 1);
        }
    }

    @Override
    public int getTime(Room room) {
        return room.getNextRoundPreStartTime();
    }
}
