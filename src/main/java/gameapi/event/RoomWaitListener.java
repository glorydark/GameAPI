package gameapi.event;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomWaitListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomWaitListener(Room room){
        this.room = room;
        room.setTime(0);
        room.setRound(0);
        for (Player p : room.getPlayers()) {
            p.sendActionBar("§l§e正在等待玩家 【"+room.getPlayers().size()+"/"+room.getMinPlayer()+"】");
        }
        if (room.getPlayers().size() >= room.getMinPlayer()) {
            room.setRoomStatus(RoomStatus.ROOM_STATUS_PreStart);
            Server.getInstance().getPluginManager().callEvent(new RoomPreStartEvent(room));
        }
    }

    public Room getRoom(){ return room;}
}
