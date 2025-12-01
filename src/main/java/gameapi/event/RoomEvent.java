package gameapi.event;

import gameapi.listener.base.GameListenerRegistry;
import gameapi.listener.base.exceptions.GameEventException;
import gameapi.room.Room;

public abstract class RoomEvent {
    protected String eventName = null;
    protected Room room;
    private boolean isCancelled = false;

    public RoomEvent(Room room) {
        this.room = room;
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

    public void setCancelled(boolean value) {
        if (!(this instanceof Cancellable)) {
            throw new GameEventException("Event is not Cancellable");
        } else {
            this.isCancelled = value;
        }
    }

    public void setCancelled() {
        this.setCancelled(true);
    }

    public Room getRoom() {
        return room;
    }

    public void call() {
        GameListenerRegistry.callEvent(this.room, this);
    }
}
