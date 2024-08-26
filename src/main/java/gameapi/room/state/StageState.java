package gameapi.room.state;

import gameapi.room.Room;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class StageState {

    // StageState will be executed per second. Here 1 tick equals to 1 seconds
    public int currentTick;
    public int maxTick;
    public Room room;

    public StageState(Room room, int maxTick) {
        this.maxTick = maxTick;
        this.room = room;
    }

    public void onStart() {

    }

    public void onEnd() {

    }

    public void onTick() {

    }

    public void onUpdate() {
        if (this.currentTick == 0) {
            this.onStart();
        }
        this.currentTick++;
        if (this.isEnd()) {
            this.onEnd();
        } else {
            this.onTick();
        }
    }

    public boolean isEnd() {
        return this.currentTick > this.maxTick;
    }
}
