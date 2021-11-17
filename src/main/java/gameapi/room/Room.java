package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import com.sun.istack.internal.NotNull;
import gameapi.MainClass;
import gameapi.arena.Arena;
import gameapi.event.PlayerJoinRoomEvent;
import gameapi.event.PlayerLeaveRoomEvent;
import gameapi.inventory.Inventory;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Room {
    private String roomName = null;
    private RoomRule roomRule;
    private RoomStatus roomStatus = RoomStatus.ROOM_STATUS_WAIT;
    private List<Player> players = new ArrayList<>();
    private int maxPlayer = 2; //最大人数
    private int minPlayer = 16; //最少人数
    private int waitTime = 10; //等待时间
    private int gameWaitTime = 3; //开始/结束缓冲时间
    private int gameTime = 10; //游戏开始时间
    private int ceremonyTime = 10; //颁奖典礼时间
    private int MaxRound; //回合数
    private int roundCache = 0;
    private int time = 0; //时间记录
    private Position waitSpawn = new Position();
    private Position startSpawn = new Position();
    private Position endSpawn = new Position();
    private String waitLevel;
    private String startLevel;
    private String endLevel;
    private HashMap<String, List<Player>> teamCache = new HashMap<>();
    private final HashMap<Player, Float> playersHealth = new HashMap<>();
    private String roomLevelBackup;
    private String roomPlayLevel;
    private final LinkedHashMap<Player, LinkedHashMap<String, Object>> playerProperties = new LinkedHashMap<>();

    public Room(RoomRule roomRule, String roomLevelBackup, String roomPlayLevel, int round){
        this.MaxRound = round;
        this.roomRule = roomRule;
        this.roomLevelBackup = roomLevelBackup;
        this.roomPlayLevel = roomPlayLevel;
    }

    public String getWaitLevel() {
        return waitLevel;
    }

    public String getStartLevel() {
        return startLevel;
    }

    public String getEndLevel() {
        return endLevel;
    }

    public Object getPlayerProperties(Player player, String key) {
        if(playerProperties.containsKey(player)){
            return playerProperties.get(player).getOrDefault(key, "null");
        }else{
            return "null";
        }
    }

    public void setPlayerProperties(Player player, String key, String value) {
        if(playerProperties.containsKey(player)){
            if(playerProperties.get(player).containsKey(key)){
                LinkedHashMap<String, Object> cache = playerProperties.get(player);
                cache.put(key, value);
                playerProperties.put(player, cache);
            }else{
                LinkedHashMap<String, Object> cache = new LinkedHashMap<>();
                cache.put(key, value);
                playerProperties.put(player, cache);
            }
        }else{
            LinkedHashMap<String, Object> cache = new LinkedHashMap<>();
            cache.put(key, value);
            playerProperties.put(player, cache);
        }
    }

    public void setRoomPlayLevel(String roomPlayLevel) {
        this.roomPlayLevel = roomPlayLevel;
    }

    public String getRoomPlayLevel() {
        return roomPlayLevel;
    }

    public String getRoomLevelBackup() {
        return roomLevelBackup;
    }

    public void setRoomLevelBackup(String roomLevelBackup) {
        this.roomLevelBackup = roomLevelBackup;
    }

    private void initLevel() {
        this.getStartSpawn().level.setThundering(false);
        this.getStartSpawn().level.setRaining(false);
    }

    public static void addTeam(Room room , String string, @NotNull List<Player> players){
        room.teamCache.put(string,players);
    }

    public static void loadRoom(Room room){
        MainClass.RoomHashMap.add(room);
    }

    public static void removeRoom(Room room){
        MainClass.RoomHashMap.remove(room);
    }

    public Boolean addPlayer(Player player){
        if(this.players.size() < this.maxPlayer){
            if(this.players.contains(player)){
                return false;
            }else{
                Server.getInstance().getPluginManager().callEvent(new PlayerJoinRoomEvent(this,player));
                Inventory.saveBag(player);
                this.players.add(player);
                return true;
            }
        }
        return false;
    }

    public void removePlayer(Player player,Boolean saveBag){
        Server.getInstance().getPluginManager().callEvent(new PlayerLeaveRoomEvent(this,player));
        Inventory.loadBag(player);
        this.players.remove(player);
    }

    public static Room getRoom(Player p){
        for(Room room: MainClass.RoomHashMap){
            if(room.players.contains(p)){
                return room;
            }
        }
        return null;
    }

    public static Room getRoom(Level level){
        for(Room room: MainClass.RoomHashMap){
            if(room.startSpawn.level.equals(level)){
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

    public void reset(){
        this.players = new ArrayList<>();
        this.roomStatus = RoomStatus.ROOM_STATUS_WAIT;
        this.roundCache = 0;
        this.teamCache = new HashMap<>();
        this.time = 0;
    }

    public void resetAll(){
        //Server.getInstance().getScheduler().scheduleAsyncTask(MainClass.plugin,new AsyncBlockCleanTask(this));
        for(Player player: players){
            Inventory.loadBag(player);
        }
        this.players = new ArrayList<>();
        this.roundCache = 0;
        this.teamCache = new HashMap<>();
        this.time = 0;
        Arena.reloadLevel(this);
        initLevel();
        this.roomStatus = RoomStatus.ROOM_STATUS_WAIT;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRound(int roundCache) {
        this.roundCache = roundCache;
    }

    public int getRound() {
        return roundCache;
    }

    public void setWaitSpawn(Position waitSpawn) {
        this.waitSpawn = waitSpawn;
        waitLevel = waitSpawn.getLevel().getName();
    }

    public Position getWaitSpawn() {
        if(waitSpawn.getLevel() == null){
            Level level = Server.getInstance().getLevelByName(waitLevel);
            if(level != null) {
                Position position = Position.fromObject(waitSpawn, level);
                return position;
            }else{
                return Position.fromObject(Server.getInstance().getDefaultLevel().getSafeSpawn(), null);
            }
        }else{
            return waitSpawn;
        }
    }

    public void setEndSpawn(Position endSpawn) {
        this.endSpawn = endSpawn;
        endLevel = endSpawn.getLevel().getName();
    }

    public Position getEndSpawn() {
        if(endSpawn.getLevel() == null){
            Level level = Server.getInstance().getLevelByName(endLevel);
            if(level != null) {
                Position position = Position.fromObject(endSpawn, level);
                return position;
            }else{
                return Position.fromObject(Server.getInstance().getDefaultLevel().getSafeSpawn(), null);
            }
        }else{
            return endSpawn;
        }
    }

    public Position getStartSpawn() {
        if(startSpawn.getLevel() == null){
            Level level = Server.getInstance().getLevelByName(startLevel);
            if(level != null) {
                Position position = Position.fromObject(startSpawn, level);
                return position;
            }else{
                return Position.fromObject(Server.getInstance().getDefaultLevel().getSafeSpawn(), null);
            }
        }else{
            return startSpawn;
        }
    }

    public void setStartSpawn(Position startSpawn) {
        this.startSpawn = startSpawn;
        startLevel = startSpawn.getLevel().getName();
    }

    public void setTeamCache(HashMap<String, List<Player>> teamCache) {
        this.teamCache = teamCache;
    }

    public HashMap<String, List<Player>> getTeamCache() {
        return teamCache;
    }

    public List<Player> getPlayers() {
        return players;
    }


    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public int getGameTime() {
        return gameTime;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public RoomRule getRoomRule() {
        return roomRule;
    }

    public int getCeremonyTime() {
        return ceremonyTime;
    }

    public int getGameWaitTime() {
        return gameWaitTime;
    }

    public int getMaxRound() {
        return MaxRound;
    }

    public int getMinPlayer() {
        return minPlayer;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setCeremonyTime(int ceremonyTime) {
        this.ceremonyTime = ceremonyTime;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public void setMinPlayer(int minPlayer) {
        this.minPlayer = minPlayer;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public void setRoomRule(RoomRule roomRule) {
        this.roomRule = roomRule;
    }

    public void setMaxRound(int maxRound) {
        MaxRound = maxRound;
    }

    public void setGameWaitTime(int gameWaitTime) {
        this.gameWaitTime = gameWaitTime;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomName='" + roomName + '\'' +
                ", roomRule=" + roomRule +
                ", roomStatus=" + roomStatus +
                ", players=" + players +
                ", maxPlayer=" + maxPlayer +
                ", minPlayer=" + minPlayer +
                ", waitTime=" + waitTime +
                ", gameWaitTime=" + gameWaitTime +
                ", gameTime=" + gameTime +
                ", ceremonyTime=" + ceremonyTime +
                ", MaxRound=" + MaxRound +
                ", roundCache=" + roundCache +
                ", time=" + time +
                ", waitSpawn=" + waitSpawn +
                ", startSpawn=" + startSpawn +
                ", endSpawn=" + endSpawn +
                ", teamCache=" + teamCache +
                '}';
    }

    public void setGameEnd(){
        this.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd);
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public float getPlayerHealth(Player player) {
        if(playersHealth.containsKey(player)){
            return playersHealth.get(player);
        }else{
            return this.getRoomRule().defaultHealth;
        }
    }

    public void setPlayerHealth(Player player, float health) {
        this.playersHealth.put(player,health);
    }

    public void resetAllPlayersHealth(float health) {
        this.playersHealth.replaceAll((p, v) -> health);
    }

    public void addPlayerHealth(Player player,float add){
        if(this.playersHealth.containsKey(player)){
            this.playersHealth.put(player, this.playersHealth.get(player)+add);
        }else{
            this.playersHealth.put(player, this.getRoomRule().defaultHealth+add);
        }
    }

    public void reducePlayerHealth(Player player,float reduce){
        if(this.playersHealth.containsKey(player)){
            this.playersHealth.put(player, this.playersHealth.get(player)-reduce);
        }else{
            this.playersHealth.put(player, this.getRoomRule().defaultHealth-reduce);
        }
    }

    public void setSpectatorMode(Player player){
        player.setGamemode(3,false);
        player.sendPopup("您已经进入观察状态!");
    }
}
