package gameapi.listener.base;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.EventPriority;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.EventException;
import gameapi.annotation.Experimental;
import gameapi.event.RoomEvent;
import gameapi.listener.base.interfaces.GameListener;
import lombok.Getter;

@Experimental
public class RoomListener implements GameListener {
    @Getter
    private final GameListener listener;
    @Getter
    private final EventPriority priority;
    @Getter
    private final Plugin plugin;
    private final MethodGameEventExecutor executor;
    private final boolean ignoreCancelled;
    @Getter
    private final String gameName;

    public RoomListener(String gameName, GameListener listener, MethodGameEventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
        this.gameName = gameName;
    }

    public void callEvent(String gameName, RoomEvent event) throws EventException {
        if (!gameName.equals(getGameName())) {
            return;
        }
        if (!(event instanceof Cancellable) || !event.isCancelled() || !this.isIgnoringCancelled()) {
            this.executor.execute(this.listener, event);
        }
    }

    public boolean isIgnoringCancelled() {
        return this.ignoreCancelled;
    }
}

