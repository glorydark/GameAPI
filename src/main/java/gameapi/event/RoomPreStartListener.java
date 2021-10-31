package gameapi.event;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.utils.TextFormat;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomPreStartListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomPreStartListener(Room room){
        this.room = room;
        if (room.getTime() >= room.getWaitTime()) {
            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameReadyStart);
            Server.getInstance().getPluginManager().callEvent(new RoomReadyStartEvent(room));
            room.setTime(0);
            for(Player p:room.getPlayers()){
                p.getInventory().clearAll();
            }
        } else {
            if (room.getPlayers().size() < room.getMinPlayer()) {
                room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
                room.setTime(0);
                return;
            }
            for (Player p : room.getPlayers()) {
                p.sendTitle(TextFormat.LIGHT_PURPLE+String.valueOf(room.getWaitTime() - room.getTime()),"游戏即将开始！");
            }
            room.setTime(room.getTime()+1);
        }
    }

    public Room getRoom(){ return room;}
}
