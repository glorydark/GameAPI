package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class RoomPlayerToggleCrawlEvent extends RoomPlayerEvent implements Cancellable {

    protected final boolean isCrawling;

    public RoomPlayerToggleCrawlEvent(Room room, Player player, boolean isCrawling) {
        super(room, player);
        this.isCrawling = isCrawling;
    }

    public boolean isCrawling() {
        return isCrawling;
    }

}
