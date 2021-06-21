package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import gameapi.room.Room;
import gameapi.sound.Sound;

public class RoomGameStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomGameStartEvent(Room room){
        RoomGameStartEvent.room = room;
        room.roundCache++;
        for (Player p : room.players) {
            p.setGamemode(room.roomRule.gamemode);
            p.sendTitle("游戏开始", "Game Start!");
            Sound.playResourcePackOggMusic(p, "game_begin");
        }
    }

    public Room getRoom(){ return room;}
}
