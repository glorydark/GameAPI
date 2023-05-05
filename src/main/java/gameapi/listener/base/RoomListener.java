package gameapi.listener.base;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.EventPriority;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.EventException;
import gameapi.annotation.Future;
import gameapi.event.RoomEvent;
import gameapi.listener.base.interfaces.GameListener;

@Future
public class RoomListener implements GameListener {
    private final GameListener listener;
    private final EventPriority priority;
    private final Plugin plugin;
    private final MethodGameEventExecutor executor;
    private final boolean ignoreCancelled;
    private final String gameName;

    public RoomListener(String gameName, GameListener listener, MethodGameEventExecutor executor, EventPriority priority, Plugin plugin, boolean ignoreCancelled) {
        this.listener = listener;
        this.priority = priority;
        this.plugin = plugin;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
        this.gameName = gameName;
    }

    public GameListener getListener() {
        return this.listener;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public EventPriority getPriority() {
        return this.priority;
    }

    public void callEvent(String gameName, RoomEvent event) throws EventException {
        if(!gameName.equals(getGameName())){
            return;
        }
        if (!(event instanceof Cancellable) || !event.isCancelled() || !this.isIgnoringCancelled()) {
            this.executor.execute(this.listener, event);
        }
    }

    public String getGameName() {
        return gameName;
    }

    public boolean isIgnoringCancelled() {
        return this.ignoreCancelled;
    }
}

