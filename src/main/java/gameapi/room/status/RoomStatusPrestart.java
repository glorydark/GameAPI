package gameapi.room.status;

import gameapi.event.room.RoomPreStartEvent;
import gameapi.event.room.RoomPreStartTickEvent;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.status.base.CustomRoomStatus;
import gameapi.room.status.base.InternalRoomStatus;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import gameapi.room.utils.reason.ResetAllReason;
import gameapi.utils.text.GameTranslationContainer;

/**
 * @author glorydark
 */
public class RoomStatusPrestart extends InternalRoomStatus {

    public RoomStatusPrestart() {
        super(RoomDefaultStatusFactory.ROOM_STATUS_PRESTART_ID, true);
    }

    @Override
    public void onEnter(Room room) {
        room.setRound(0);
        new RoomPreStartEvent(room).call();
        room.getStatusExecutor().beginPreStart();
    }

    @Override
    public boolean onTick(Room room) {
        if (!this.hasEnoughPlayer(room)) {
            room.resetAll(ResetAllReason.NO_ENOUGH_PLAYERS);
            return true;
        } else {
            GameListenerRegistry.callEvent(room, new RoomPreStartTickEvent(room));
            this.onStateUpdate(room);
            return false;
        }
    }

    public void onStateUpdate(Room room) {
        if (room.getTime() >= room.getWaitTime()) {
            CustomRoomStatus status = this.getNextRoomStatus(room);
            if (status == null) {
                status = RoomDefaultStatusFactory.ROOM_STATUS_READY_START;
            }
            room.setCurrentRoomStatus(status, "internal");
        } else {
            int leftWaitTime = room.getWaitTime() - room.getTime();
            if (leftWaitTime >= 15
                    && room.getPlayers().size() >= room.getAccelerateWaitCountDownPlayerCount()) {
                room.sendMessageToAll(new GameTranslationContainer("room.game.wait.time_accelerated"));
                room.setTime(room.getWaitTime() - 15);
            }
            room.getStatusExecutor().onPreStart();
            room.setTime(room.getTime() + 1);
        }
    }

    @Override
    public int getTime(Room room) {
        return room.getWaitTime();
    }
}
