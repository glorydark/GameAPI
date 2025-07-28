package gameapi.room.task;

import gameapi.room.Room;

/**
 * @author glorydark
 */
public abstract class RoomPreciseUpdateTask {

    private final long startMillis;
    private boolean cancelled = false;
    private int tick;

    public RoomPreciseUpdateTask() {
        this.startMillis = System.currentTimeMillis();
    }

    public void onStart(Room room) {

    }

    public void onEnd(Room room) {

    }

    public abstract void onUpdate(Room room);

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }
}
