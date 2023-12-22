package gameapi.event;

public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean var1);

    void setCancelled();
}
