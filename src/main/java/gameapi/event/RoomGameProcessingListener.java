package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.inventory.Inventory;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomGameProcessingListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomGameProcessingListener(Room room){
        this.room = room;
        if (room.getTime() >= room.getGameTime()) {
            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd);
            room.setTime(0);
            for(Player player:room.getPlayers()){
                Inventory.loadBag(player);
                player.setGamemode(0,false);
            }
            Server.getInstance().getPluginManager().callEvent(new RoomGameEndEvent(room));
        }else{
            if(!room.getRoomRule().noTimeLimit) {
                room.setTime(room.getTime() + 1);
            }
        }
    }

    public Room getRoom(){ return room;}
}
