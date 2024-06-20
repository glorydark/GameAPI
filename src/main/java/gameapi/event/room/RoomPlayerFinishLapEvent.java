package gameapi.event.room;

import cn.nukkit.Player;
import gameapi.event.player.RoomPlayerEvent;
import gameapi.room.Room;

/**
 * @author glorydark
 * @date {2023/12/31} {18:02}
 */
public class RoomPlayerFinishLapEvent extends RoomPlayerEvent {

    private final int lapNumber;

    public RoomPlayerFinishLapEvent(Room room, Player player, int lapNumber) {
        super(room, player);
        this.lapNumber = lapNumber;
    }

    public int getLapNumber() {
        return lapNumber;
    }
}
