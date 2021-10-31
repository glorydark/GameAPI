package gameapi.event;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomReadyStartListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomReadyStartListener(Room room){
        this.room = room;
        if (room.getTime() >= room.getWaitTime()) {
            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameStart);
            room.setTime(0);
            room.setRound(room.getRound() + 1);
            Server.getInstance().getPluginManager().callEvent(new RoomGameStartEvent(room));
        } else {
            for (Player p : room.getPlayers()) {
                p.sendActionBar("游戏开始还剩" + (room.getWaitTime() - room.getTime()) + "秒");
            }
            room.setTime(room.getTime()+1);
        }
    }

    public Room getRoom(){ return room;}
}
