package gameapi.room;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class RoomChatData {
    String playerName;

    String message = "";

    String prefix = "";

    String rawMessage = "";

    String suffix = "";

    long timeMillis;

    public RoomChatData(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
        this.rawMessage = message;
        this.timeMillis = System.currentTimeMillis();
    }

    public RoomChatData(String playerName, String message, long timeMillis) {
        this.playerName = playerName;
        this.message = message;
        this.timeMillis = timeMillis;
    }

    public String getDefaultChatMsg() {
        return prefix + playerName + ": " + message + suffix;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return "[" + new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS毫秒").format(new Date(timeMillis)) + "]" + playerName + ": " + message;
    }
}
