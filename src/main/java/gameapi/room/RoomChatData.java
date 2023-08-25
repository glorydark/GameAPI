package gameapi.room;

import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
public class RoomChatData {
    String playerName;

    String message;

    long timeMillis;

    public RoomChatData(String playerName, String message){
        this.playerName = playerName;
        this.message = message;
        this.timeMillis = System.currentTimeMillis();
    }

    public RoomChatData(String playerName, String message, long timeMillis){
        this.playerName = playerName;
        this.message = message;
        this.timeMillis = timeMillis;
    }

    public String getDefaultChatMsg(){
        return playerName+": "+ message;
    }

    @Override
    public String toString() {
        return "["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeMillis))+"]"+playerName+": "+ message;
    }
}
