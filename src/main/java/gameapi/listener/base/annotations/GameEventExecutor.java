package gameapi.listener.base.annotations;

import gameapi.event.RoomEvent;
import gameapi.listener.base.exceptions.GameEventException;

public interface GameEventExecutor {
    void execute(GameListener listener, RoomEvent var2) throws GameEventException;
}

