package gameapi.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import gameapi.room.Room;
import gameapi.scoreboard.UIScoreboard;

public class RoomEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomEndEvent(Room room){
        this.room = room;
        for(Player p:room.getPlayers()) {
            Position pos = Server.getInstance().getDefaultLevel().getSafeSpawn();
            p.teleport(pos.getLocation());
            UIScoreboard.removeScoreboard(p);
            UIScoreboard.scoreboardConcurrentHashMap.remove(p);
            //玩家先走
        }
        room.resetAll();
    }

    public Room getRoom(){ return room;}
}
