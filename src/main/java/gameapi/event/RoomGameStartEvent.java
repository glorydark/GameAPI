package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.sound.Sound;

public class RoomGameStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomGameStartEvent(Room room){
        this.room = room;
        room.setRound(room.getRound()+1);
        for (Player p : room.getPlayers()) {
            p.setGamemode(room.getRoomRule().gamemode);
            p.sendTitle("游戏开始", "Game Start!");
        }
    }

    public Room getRoom(){ return room;}
}
