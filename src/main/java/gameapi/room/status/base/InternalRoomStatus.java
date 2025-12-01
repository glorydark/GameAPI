package gameapi.room.status.base;

import gameapi.room.Room;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Glorydark
 */
public abstract class InternalRoomStatus extends CustomRoomStatus {

    public InternalRoomStatus(String identifier, boolean allowSpectatorJoin) {
        super(identifier, allowSpectatorJoin);
    }

    public InternalRoomStatus(String identifier) {
        super(identifier);
    }

    // Internally, time counter is replaced by onStateUpdate();
    @Override
    public boolean isTimeCounterEnabled(Room room) {
        return false;
    }

    public boolean hasEnoughPlayerWithTeam(Room room) {
        if (room.getPlayersWithoutCreate().isEmpty()) {
            return false;
        } else {
            if (room.getTeams().size() > 1) {
                AtomicInteger hasPlayer = new AtomicInteger(0);
                room.getTeams().forEach(team -> {
                    if (!team.getPlayers().isEmpty()) {
                        hasPlayer.addAndGet(1);
                    }
                });
                return hasPlayer.get() > 1;
            } else {
                return room.getPlayers().size() >= room.getMinPlayer();
            }
        }
    }

    public boolean hasEnoughPlayer(Room room) {
        if (room.getPlayersWithoutCreate().isEmpty()) {
            return false;
        } else {
            return room.getPlayers().size() >= room.getMinPlayer();
        }
    }
}