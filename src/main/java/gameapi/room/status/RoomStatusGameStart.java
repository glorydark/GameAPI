package gameapi.room.status;

import cn.nukkit.Server;
import gameapi.event.room.RoomGameStartEvent;
import gameapi.event.room.RoomGameStartTickEvent;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.status.base.InternalRoomStatus;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import gameapi.room.utils.reason.ResetAllReason;

/**
 * @author glorydark
 */
public class RoomStatusGameStart extends InternalRoomStatus {

    public RoomStatusGameStart() {
        super(RoomDefaultStatusFactory.ROOM_STATUS_GAME_START_ID, true);
    }

    @Override
    public void onEnter(Room room) {
        room.setRound(room.getRound() + 1);
        room.getStatusExecutor().beginGameStart();
        room.setStartMillis(System.currentTimeMillis());
        room.setGameStartTick(Server.getInstance().getTick());
        new RoomGameStartEvent(room).call();
        room.getStatusExecutor().beginGameStart();
    }

    @Override
    public boolean onTick(Room room) {
        if (!this.hasEnoughPlayerWithTeam(room)) {
            room.resetAll(ResetAllReason.NO_ENOUGH_PLAYERS);
            return true;
        }
        room.setGameDuration(room.getGameDuration() + 1);
        GameListenerRegistry.callEvent(room, new RoomGameStartTickEvent(room));
        this.onStateUpdate(room);
        return false;
    }

    public void onStateUpdate(Room room) {
        if (!room.getRoomRule().isNoTimeLimit() && room.getTime() >= room.getGameTime()) {
            if (room.getRound() >= room.getMaxRound()) {
                room.setCurrentRoomStatus(RoomDefaultStatusFactory.ROOM_STATUS_GAME_END, "internal");
            } else {
                room.setCurrentRoomStatus(RoomDefaultStatusFactory.ROOM_STATUS_NEXT_ROUND_PRESTART, "internal");
            }
        } else {
            room.getStatusExecutor().onGameStart();
            room.setTime(room.getTime() + 1);
        }
    }

    @Override
    public int getTime(Room room) {
        return room.getGameTime();
    }

    @Override
    public boolean isStageStateEnabled(Room room) {
        return true;
    }

    public boolean isAllowDefaultRespawnEnabled(Room room) {
        return true;
    }

    @Override
    public boolean isAllowPlaceBlock(Room room) {
        return true;
    }

    @Override
    public boolean isAllowBreakBlock(Room room) {
        return true;
    }

    @Override
    public boolean isAllowEntityDamageBySelf(Room room) {
        return true;
    }

    @Override
    public boolean isAllowEntityDamagedByEntity(Room room) {
        return true;
    }

    @Override
    public boolean isAllowPlayerDropItem(Room room) {
        return true;
    }
}
