package gameapi.room.state;

import gameapi.annotation.Future;
import gameapi.room.Room;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class StageState {

    public int timer;
    public int maxTime;
    public Room room = null;
    private String name;
    private String description = "";

    public StageState(String name, int maxTime) {
        this.name = name;
        this.maxTime = maxTime;
    }

    public StageState(String name, String description, int maxTime) {
        this.name = name;
        this.description = description;
        this.maxTime = maxTime;
    }

    public void onStart() {

    }

    public void onEnd() {

    }

    public void onTick() {

    }

    public void onUpdate() {
        this.timer++;
        if (this.isEnd()) {
            this.onEnd();
        } else {
            this.onTick();
        }
    }

    public boolean isEnd() {
        return this.timer > this.maxTime;
    }
}
