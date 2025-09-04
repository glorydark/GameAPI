package gameapi.room.state;

import gameapi.room.Room;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class StageState {

    // StageState will be executed per second. Here 1 tick equals to 1 seconds
    public int time;
    public int maxTime;
    private boolean end = false;

    protected boolean executeStartAction = false;
    protected boolean executeEndAction = false;

    public StageState(int maxTime) {
        this.maxTime = maxTime;
    }

    public void onStart(Room room) {

    }

    public void onEnd(Room room) {

    }

    public void onTick(Room room) {

    }

    public boolean isEnd() {
        return this.end || this.time > this.maxTime;
    }
}
