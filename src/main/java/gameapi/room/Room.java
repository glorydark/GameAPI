package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import com.sun.istack.internal.NotNull;
import gameapi.MainClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Room {
    public String roomName = null;
    public DefaultRoomRule roomRule;
    public RoomStatus roomStatus = RoomStatus.ROOM_STATUS_WAIT;
    public List<Player> players = new ArrayList<>();
    public int maxPlayer = 2; //最大人数
    public int minPlayer = 16; //最少人数
    public int waitTime = 10; //等待时间
    public int gameWaitTime = 3; //开始/结束缓冲时间
    public int gameTime = 10; //游戏开始时间
    public int ceremonyTime = 10; //颁奖典礼时间
    public int MaxRound; //回合数
    public int roundCache = 0;
    public int time = 0; //时间记录
    public Position waitSpawn = new Position();
    public Position startSpawn = new Position();
    public Position endSpawn = new Position();
    public HashMap<String, List<Player>> teamCache = new HashMap<>();

    /**
     * @description: 设置房间状态
     */
    public Room(DefaultRoomRule roomRule, int round){
        this.MaxRound = round;
        this.roomRule = roomRule;
    }

    /**
     * @description: 添加房间
     * @param room: 房间
     * @param players : 玩家列表
     * @param string : 队伍名称
     */
    public static void addTeam(Room room ,String string, @NotNull List<Player> players){
        room.teamCache.put(string,players);
    }

    /**
     * @description: 添加房间
     * @param room: 房间
     */
    public static void loadRoom(Room room){
        MainClass.RoomHashMap.add(room);
    }

    /**
     * @description: 移除房间
     * @param room: 房间
     */
    public static void removeRoom(Room room){
        MainClass.RoomHashMap.remove(room);
    }

    /**
     * @description: 增加房间玩家
     * @param room : 房间
     * @param player : 玩家
     */

    public static Boolean addRoomPlayer(Room room, Player player){
        if(MainClass.RoomHashMap == null){return false;}
        if(getRoom(player) != null){return false;}
        if(room.players.size() < room.maxPlayer){
            room.players.add(player);
            return true;
        }
        return false;
    }

    /**
     * @description: 移除房间玩家
     * @param room: 房间
     * @param player: 玩家
     * @return: Player[](无则返回null)
     */
    public static void removeRoomPlayer(Room room,Player player){
        room.players.remove(player);
    }

    public static Room getRoom(Player p){
        for(Room room: MainClass.RoomHashMap){
            if(room.players.contains(p)){
                return room;
            }
        }
        return null;
    }

    public static Room getRoom(String roomName){
        for(Room room: MainClass.RoomHashMap){
            if(room.roomName.equals(roomName)){
                return room;
            }
        }
        return null;
    }
}
