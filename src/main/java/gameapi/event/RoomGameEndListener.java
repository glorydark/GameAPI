package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomGameEndListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomGameEndListener(Room room){
        this.room = room;
        if (room.getTime() >= room.getWaitTime()) {
            room.setRoomStatus(RoomStatus.ROOM_STATUS_Ceremony);
            Server.getInstance().getPluginManager().callEvent(new RoomCeremonyEvent(room));
            room.setTime(0);
            /*
            if(room.getRound() >= room.getMaxRound()) {
                room.setRoomStatus(RoomStatus.ROOM_STATUS_Ceremony);
                Server.getInstance().getPluginManager().callEvent(new RoomCeremonyEvent(room));
                room.setTime(0);
            }else{
                room.setRoomStatus(RoomStatus.ROOM_STATUS_NextRoundPreStart);
                Server.getInstance().getPluginManager().callEvent(new RoomNextRoundPreStartEvent(room));
            }

             */
        }else {
            room.setTime(room.getTime()+1);
            for (Player p : room.getPlayers()) {
                p.sendActionBar("颁奖典礼还有" + (room.getWaitTime() - room.getTime()) + "秒开始！");
            }
        }
    }

    public Room getRoom(){ return room;}
}
