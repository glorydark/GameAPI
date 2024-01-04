package gameapi.event.room;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.event.player.RoomPlayerEvent;

/**
 * @author glorydark
 * @date {2023/12/31} {18:02}
 */
public class RoomPlayerFinishLapEvent extends RoomPlayerEvent {

    private final int lapNumber;

    public RoomPlayerFinishLapEvent(Player player, int lapNumber) {
        this.player = player;
        this.lapNumber = lapNumber;
    }

    public int getLapNumber() {
        return lapNumber;
    }
}
