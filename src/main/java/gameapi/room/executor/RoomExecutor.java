package gameapi.room.executor;

import gameapi.room.Room;

public abstract class RoomExecutor {

    Room room = null;

    public Room createRoom() {
        return null; // Here developers need to override it.
    }

    public Room restartRoom() {
        return null;
    }

    public void onWait() {

    }

    public void onPreStart() {

    }

    public void onReadyStart() {

    }

    public void onGameStart() {

    }

    public void onGameEnd() {

    }

    public void onCeremony() {

    }

    public void onNextRoundPreStart() {

    }

    public void beginPreStart() {

    }

    public void beginReadyStart() {

    }

    public void beginGameStart() {

    }

    public void beginGameEnd() {

    }

    public void beginCeremony() {

    }

    public void beginNextRoundPreStart() {

    }

}
