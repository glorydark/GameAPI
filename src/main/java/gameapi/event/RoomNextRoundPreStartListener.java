package gameapi.event;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomNextRoundPreStartListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomNextRoundPreStartListener(Room room){
        RoomNextRoundPreStartListener.room = room;
        if (room.time >= room.gameWaitTime) {
            room.roomStatus = RoomStatus.ROOM_STATUS_Ceremony;
            room.time = 0;
        } else {
            for (Player p : room.players) {
                p.sendActionBar("下一场游戏开始还剩" + (room.gameWaitTime - room.time) + "秒");
            }
            room.time++;
            room.roomStatus = RoomStatus.ROOM_STATUS_PreStart;
            Server.getInstance().getPluginManager().callEvent(new RoomPreStartEvent(room));
        }
    }

    public Room getRoom(){ return room;}
}
