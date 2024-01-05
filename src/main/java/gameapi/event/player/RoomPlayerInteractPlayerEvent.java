package gameapi.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;

/**
 * @author glorydark
 * @date {2023/12/28} {20:43}
 */
public class RoomPlayerInteractPlayerEvent extends RoomPlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Player interactedPlayer;

    public RoomPlayerInteractPlayerEvent(Room room, Player player, Player interactedPlayer) {
        this.room = room;
        this.player = player;
        this.interactedPlayer = interactedPlayer;
    }

    public Player getInteractedPlayer() {
        return interactedPlayer;
    }
}
