package gameapi.event;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;
import gameapi.fireworkapi.CreateFireworkApi;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.scoreboard.UIScoreboard;

import java.util.concurrent.ThreadLocalRandom;

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
            for(Player p:room.getPlayers()) {
                UIScoreboard.removeScoreboard(p);
                UIScoreboard.scoreboardConcurrentHashMap.remove(p);
                //玩家先走
            }
            Server.getInstance().getPluginManager().callEvent(new RoomEndEvent(room));
        } else {
            room.setTime(room.getTime()+1);
            for (Player p : room.getPlayers()) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                Integer i1 = random.nextInt(14);
                Integer i2 = random.nextInt(4);
                CreateFireworkApi.spawnFirework(p.getPosition(), CreateFireworkApi.getColorByInt(i1), CreateFireworkApi.getExplosionTypeByInt(i2));
                p.sendActionBar("§l§e颁奖典礼结束还剩 §l§6" + (room.getCeremonyTime() - room.getTime()) + " §l§e秒！");
            }
        }
    }

    public Room getRoom(){ return room;}
}
