package gameapi.event;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomReadyStartListener extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Room room;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RoomReadyStartListener(Room room){
        this.room = room;
        if (room.getTime() >= room.getWaitTime()) {
            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameStart);
            room.setTime(0);
            room.setRound(room.getRound() + 1);
            for(Player p:room.getPlayers()){
                p.teleport(Position.fromObject(room.getStartSpawn().getLocation(), Server.getInstance().getLevelByName(room.getRoomPlayLevel())));
            }
            room.setRound(room.getRound()+1);
            for (Player p : room.getPlayers()) {
                p.setGamemode(room.getRoomRule().gamemode);
                p.sendTitle("§l§e 游戏开始!", "Game Start!");
            }
            Server.getInstance().getPluginManager().callEvent(new RoomGameStartEvent(room));
        } else {
            for (Player p : room.getPlayers()) {
                Integer lastSec = room.getWaitTime() - room.getTime();
                if(lastSec > 10) {
                    p.getLevel().addSound(p.getPosition(), Sound.NOTE_HARP);
                    p.sendActionBar("§l§e游戏开始还剩 §l§6" + (room.getWaitTime() - room.getTime()) + " §l§e秒");
                }else{
                    if(lastSec == 1){
                        p.getLevel().addSound(p.getPosition(), Sound.NOTE_FLUTE);
                    }else{
                        p.getLevel().addSound(p.getPosition(), Sound.NOTE_BASS);
                    }
                    switch (lastSec){
                        case 10:
                            p.sendActionBar("§l§e游戏开始 §l§a▉▉▉▉▉▉▉▉▉▉ §l§f" + lastSec);
                            break;
                        case 9:
                            p.sendActionBar("§l§e游戏开始 §l§a▉▉▉▉▉▉▉▉▉§l§7▉ §l§f" + lastSec);
                            break;
                        case 8:
                            p.sendActionBar("§l§e游戏开始 §l§a▉▉▉▉▉▉▉▉§l§7▉▉ §l§f" + lastSec);
                            break;
                        case 7:
                            p.sendActionBar("§l§e游戏开始 §l§e▉▉▉▉▉▉▉§l§7▉▉▉ §l§f" + lastSec);
                            break;
                        case 6:
                            p.sendActionBar("§l§e游戏开始 §l§e▉▉▉▉▉▉§l§7▉▉▉▉ §l§f" + lastSec);
                            break;
                        case 5:
                            p.sendActionBar("§l§e游戏开始 §l§e▉▉▉▉▉§l§7▉▉▉▉▉ §l§f" + lastSec);
                            break;
                        case 4:
                            p.sendActionBar("§l§e游戏开始 §l§e▉▉▉▉§l§7▉▉▉▉▉▉ §l§f" + lastSec);
                            break;
                        case 3:
                            p.sendActionBar("§l§e游戏开始 §l§c▉▉▉§l§7▉▉▉▉▉▉▉ §l§f" + lastSec);
                            break;
                        case 2:
                            p.sendActionBar("§l§e游戏开始 §l§c▉▉§l§7▉▉▉▉▉▉▉▉ §l§f" + lastSec);
                            break;
                        case 1:
                            p.sendActionBar("§l§e游戏开始 §l§c▉§l§7▉▉▉▉▉▉▉▉▉ §l§f" + lastSec);
                            break;
                        case 0:
                            p.sendActionBar("§l§e游戏开始 §l§7▉▉▉▉▉▉▉▉▉▉ §l§f" + lastSec);
                            break;
                    }
                }
            }
            room.setTime(room.getTime()+1);
        }
    }

    public Room getRoom(){ return room;}
}
