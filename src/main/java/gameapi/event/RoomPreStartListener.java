package gameapi.event;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomPreStartListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomPreStartListener(Room room){
        RoomPreStartListener.room = room;
        if (room.time >= room.waitTime) {
            room.roomStatus = RoomStatus.ROOM_STATUS_GameReadyStart;
            Server.getInstance().getPluginManager().callEvent(new RoomReadyStartEvent(room));
            room.time = 0;
        } else {
            if (room.players.size() < room.minPlayer) {
                room.roomStatus = RoomStatus.ROOM_STATUS_WAIT;
                room.time = 0;
                return;
            }
            for (Player p : room.players) {
                p.sendActionBar("距离游戏开始还剩" + (room.waitTime - room.time) + "秒");
            }
            room.time++;
        }
    }

    public Room getRoom(){ return room;}
}
