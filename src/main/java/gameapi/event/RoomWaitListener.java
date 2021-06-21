package gameapi.event;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomWaitListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomWaitListener(Room room){
        RoomWaitListener.room = room;
        room.time = 0;
        room.roundCache = 0;
        for (Player p : room.players) {
            p.sendActionBar("正在等待玩家");
        }
        if (room.players.size() >= room.minPlayer) {
            room.roomStatus = RoomStatus.ROOM_STATUS_PreStart;
            Server.getInstance().getPluginManager().callEvent(new RoomPreStartEvent(room));
        }
    }

    public Room getRoom(){ return room;}
}
