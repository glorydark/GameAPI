package gameapi.room.task;

/**
 * @author glorydark
 */
public abstract class EasyTask {

    private boolean cancelled = false;

    public EasyTask() {

    }

    public abstract void onRun(int tick);

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancel() {
        this.setCancelled(true);
    }
}
