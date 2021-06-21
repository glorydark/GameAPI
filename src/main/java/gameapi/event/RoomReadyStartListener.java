package gameapi.event;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomReadyStartListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomReadyStartListener(Room room){
        RoomReadyStartListener.room = room;
        if (room.time >= room.gameWaitTime) {
            room.roomStatus = RoomStatus.ROOM_STATUS_GameStart;
            room.time = 0;
            room.roundCache++;
            Server.getInstance().getPluginManager().callEvent(new RoomGameStartEvent(room));
        } else {
            for (Player p : room.players) {
                p.sendActionBar("游戏开始还剩" + (room.gameWaitTime - room.time) + "秒");
            }
            room.time++;
        }
    }

    public Room getRoom(){ return room;}
}
