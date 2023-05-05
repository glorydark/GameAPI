package gameapi.listener.base.interfaces;

import gameapi.event.RoomEvent;
import gameapi.listener.base.exceptions.GameEventException;

public interface GameEventExecutor {
    void execute(GameListener listener, RoomEvent var2) throws GameEventException;
}

