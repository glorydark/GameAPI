package gameapi.event.player;

import cn.nukkit.Player;
import gameapi.room.Room;

/**
 * eprecated use {@link gameapi.event.entity.RoomEntityDamageByEntityEvent} instead
 */
@Deprecated
public class RoomPlayerInteractPlayerEvent extends RoomPlayerEvent {

    private final Player interactedPlayer;

    public RoomPlayerInteractPlayerEvent(Room room, Player player, Player interactedPlayer) {
        super(room, player);
        this.interactedPlayer = interactedPlayer;
    }

    public Player getInteractedPlayer() {
        return interactedPlayer;
    }
}
