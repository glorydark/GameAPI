package gameapi.event;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomCeremonyListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomCeremonyListener(Room room){
        this.room = room;
        if (room.getTime() >= room.getCeremonyTime()) {
            room.setRoomStatus(RoomStatus.ROOM_STATUS_End);
            room.setTime(0);
            Server.getInstance().getPluginManager().callEvent(new RoomEndEvent(room));
        } else {
            room.setTime(room.getTime()+1);
            for (Player p : room.getPlayers()) {
                p.sendActionBar("颁奖典礼结束还剩" + (room.getCeremonyTime() - room.getTime()) + "秒！");
            }
        }
    }

    public Room getRoom(){ return room;}
}
