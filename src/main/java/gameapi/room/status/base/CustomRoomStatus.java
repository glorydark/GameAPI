package gameapi.room.status.base;

import gameapi.room.Room;
import gameapi.room.status.factory.RoomDefaultStatusFactory;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Glorydark
 */
public abstract class CustomRoomStatus {

    private final String identifier;
    private final boolean allowSpectatorJoin;

    public CustomRoomStatus(String identifier) {
        this(identifier, false);
    }

    public CustomRoomStatus(String identifier, boolean allowSpectatorJoin) {
        this.identifier = identifier;
        RoomDefaultStatusFactory.REGISTRY.put(identifier, this);
        this.allowSpectatorJoin = allowSpectatorJoin;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isAllowSpectatorJoin(Room room) {
        return this.allowSpectatorJoin;
    }

    public boolean isTimeCounterEnabled(Room room) {
        return true;
    }

    public boolean isStageStateEnabled(Room room) {
        return false;
    }

    public boolean isAllowDefaultRespawnEnabled(Room room) {
        return false;
    }

    public boolean isAllowPlaceBlock(Room room) {
        return false;
    }

    public boolean isAllowBreakBlock(Room room) {
        return false;
    }

    public boolean isAllowEntityDamageBySelf(Room room) {
        return false;
    }

    public boolean isAllowEntityDamagedByEntity(Room room) {
        return false;
    }

    public boolean isAllowPlayerDropItem(Room room) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CustomRoomStatus that = (CustomRoomStatus) obj;
        return this.identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Override
    public String toString() {
        return "CustomRoomStatus{" +
                "identifier='" + identifier + '\'' +
                '}';
    }

    /**
     * Check if status is changed
     * @return status changed
     */
    public boolean onTick(Room room) {
        if (room.getRoomStatusList().isEmpty()) {
            return false;
        }
        if (!room.getRoomRule().isNoTimeLimit() && room.getTime() >= room.getGameTime()) {
            CustomRoomStatus status = this.getNextRoomStatus(room);
            if (status != null) {
                room.setCurrentRoomStatus(status);
            }
        }
        room.setTime(room.getTime() + 1);
        return false;
    }

    public abstract int getTime(Room room);

    public @Nullable CustomRoomStatus getNextRoomStatus(Room room) {
        List<CustomRoomStatus> customRoomStatusList = room.getRoomStatusList();
        if (customRoomStatusList.contains(this)) {
            int index = customRoomStatusList.indexOf(this);
            if (index + 1 < customRoomStatusList.size()) {
                return customRoomStatusList.get(index + 1);
            }
        }
        return null;
    }
}