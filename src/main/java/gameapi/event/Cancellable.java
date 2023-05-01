package gameapi.event;

public interface Cancellable {
    boolean isCancelled();

    void setCancelled();

    void setCancelled(boolean var1);
}
