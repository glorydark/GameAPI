package gameapi.room.state;

import gameapi.annotation.Future;
import gameapi.room.Room;

/**
 * @author glorydark
 */
@Future
public class GameState {

    private String name;

    private String description = "";

    public int timer;

    public int maxTime;

    public Room room = null;

    public GameState(String name, int maxTime) {
        this.name = name;
        this.maxTime = maxTime;
    }

    public GameState(String name, String description, int maxTime) {
        this.name = name;
        this.description = description;
        this.maxTime = maxTime;
    }

    public void onStart() {

    }

    public void onEnd() {

    }

    public void updateTimer() {
        this.timer++;
        if (this.timer >= this.maxTime) {
            this.onEnd();
        }
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }
}
