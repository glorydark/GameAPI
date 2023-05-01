package gameapi.event;

import gameapi.listener.base.exceptions.GameEventException;
import gameapi.room.Room;
import lombok.Getter;

@Getter
public abstract class RoomEvent {
    protected String eventName = null;
    private boolean isCancelled = false;

    protected Room room;

    public RoomEvent(Room room) {
        this.room = room;
    }

    protected RoomEvent() {
    }

    public final String getEventName() {
        return this.eventName == null ? this.getClass().getName() : this.eventName;
    }

    public boolean isCancelled() {
        if (this instanceof Cancellable) {
            return this.isCancelled;
        } else {
            throw new GameEventException("Event is not Cancellable");
        }
    }

    public void setCancelled() {
        this.setCancelled(true);
    }

    public void setCancelled(boolean value) {
        if (!(this instanceof Cancellable)) {
            throw new GameEventException("Event is not Cancellable");
        } else {
            this.isCancelled = value;
        }
    }
}
