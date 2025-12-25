package gameapi.event.level;

import cn.nukkit.level.Level;
import gameapi.annotation.Future;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.room.Room;

@Future
public abstract class RoomLevelEvent extends RoomEvent implements Cancellable {

    protected Level level;

    public RoomLevelEvent(Room room, Level level) {
        super(room);
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
