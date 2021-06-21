package gameapi.event;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class RoomCeremonyListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomCeremonyListener(Room room){
        RoomCeremonyListener.room = room;
        if (room.time >= room.ceremonyTime) {
            Server.getInstance().getPluginManager().callEvent(new RoomEndEvent(room));
            room.time = 0;
            room.roomStatus = RoomStatus.ROOM_STATUS_WAIT;
            room.players = new ArrayList<>();
            room.teamCache = new HashMap<>();
            room.roundCache = 0;
        } else {
            room.time++;
            for (Player p : room.players) {
                p.sendActionBar("颁奖典礼结束还剩" + (room.ceremonyTime - room.time) + "秒！");
            }
        }
    }

    public Room getRoom(){ return room;}
}
