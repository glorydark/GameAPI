package gameapi.room.executor;

import gameapi.room.Room;

public interface RoomExecutor {

    Room room = null;

    default Room createRoom() {
        return null; // Here developers need to override it.
    }

    default Room restartRoom() {
        return null;
    }

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
