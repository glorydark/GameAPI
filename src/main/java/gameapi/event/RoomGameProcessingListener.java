package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.sound.Sound;

public class RoomGameProcessingListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomGameProcessingListener(Room room){
        RoomGameProcessingListener.room = room;
        if (room.time >= room.gameTime) {
            room.roomStatus = RoomStatus.ROOM_STATUS_GameEnd;
            room.time = 0;
            Server.getInstance().getPluginManager().callEvent(new RoomGameEndEvent(room));
            for (Player p : room.players) {
               Sound.playResourcePackOggMusic(p, "game_over");
            }
        }else{
            room.time++;
        }
    }

    public Room getRoom(){ return room;}
}
