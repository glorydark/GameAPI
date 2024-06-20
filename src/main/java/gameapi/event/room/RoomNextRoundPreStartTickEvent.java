package gameapi.event.room;


import cn.nukkit.Player;
import gameapi.event.Cancellable;
import gameapi.event.RoomEvent;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomNextRoundPreStartTickEvent extends RoomEvent implements Cancellable {

    public RoomNextRoundPreStartTickEvent(Room room) {
        super(room);
        if (room.getTime() >= room.getWaitTime()) {
            room.setRoomStatus(RoomStatus.ROOM_STATUS_Ceremony);
            room.setTime(0);
        } else {
            for (Player p : room.getPlayers()) {
                p.sendActionBar("§l§e下一场游戏开始还剩 §l§6" + (room.getWaitTime() - room.getTime()) + " §l§e秒");
            }
            room.setTime(room.getTime() + 1);
            room.setRoomStatus(RoomStatus.ROOM_STATUS_PreStart);
            GameListenerRegistry.callEvent(room, new RoomPreStartEvent(room));
        }
    }

    public Room getRoom() {
        return room;
    }
}
