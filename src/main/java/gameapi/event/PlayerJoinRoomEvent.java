package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;

public class PlayerJoinRoomEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;
    private final Player player;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerJoinRoomEvent(Room room, Player player){
        this.room = room;
        this.player = player;
    }

    public Room getRoom(){ return room;}

    public Player getPlayer() {
        return this.player;
    }
}
