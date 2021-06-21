package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomGameEndListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomGameEndListener(Room room){
        RoomGameEndListener.room = room;
        if (room.time >= room.gameWaitTime) {
            if(room.roundCache <= room.MaxRound) {
                room.roomStatus = RoomStatus.ROOM_STATUS_Ceremony;
                Server.getInstance().getPluginManager().callEvent(new RoomCeremonyEvent(room));
                room.time = 0;
            }else{
                room.roomStatus = RoomStatus.ROOM_STATUS_NextRoundPreStart;
                Server.getInstance().getPluginManager().callEvent(new RoomNextRoundPreStartEvent(room));
            }
        }else {
            room.time++;
            for (Player p : room.players) {
                p.sendActionBar("颁奖典礼还有" + (room.gameWaitTime - room.time) + "秒开始！");
            }
        }
    }

    public Room getRoom(){ return room;}
}
