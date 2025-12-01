package gameapi.room.status.base;

import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class DefaultCustomRoomStatus extends InternalRoomStatus {

    public DefaultCustomRoomStatus(String identifier, boolean allowSpectatorJoin) {
        super(identifier, allowSpectatorJoin);
    }

    public DefaultCustomRoomStatus(String identifier) {
        super(identifier);
    }

    @Override
    public int getTime(Room room) {
        return 1;
    }
}