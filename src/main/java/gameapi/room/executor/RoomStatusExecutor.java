package gameapi.room.executor;

import gameapi.room.Room;

public interface RoomStatusExecutor {

    Room room = null;

    void onWait();

    void onPreStart();

    void onReadyStart();

    void onGameStart();

    void onGameEnd();

    void onCeremony();

    void onNextRoundPreStart();
    
    void beginPreStart();

    void beginReadyStart();

    void beginGameStart();

    void beginGameEnd();
    
    void beginCeremony();
    
    void beginNextRoundPreStart();
    
}
