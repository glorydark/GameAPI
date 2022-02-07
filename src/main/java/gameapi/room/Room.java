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
    private int gameWaitTime = 10; //开始/结束缓冲时间
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
    private final String gameName;
    private LinkedHashMap<Player, LinkedHashMap<String, Object>> playerProperties = new LinkedHashMap<>();

    public Room(String gameName, RoomRule roomRule, String roomLevelBackup, int round){
        this.MaxRound = round;
        this.roomRule = roomRule;
        this.roomLevelBackup = roomLevelBackup;
        this.gameName = gameName;
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
            return playerProperties.get(player).getOrDefault(key, null);
        }else{
            return null;
        }
    }

    public void setPlayerProperties(Player player, String key, Object value) {
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

    public String getRoomLevelBackup() {
        return roomLevelBackup;
    }

    public void setRoomLevelBackup(String roomLevelBackup) {
        this.roomLevelBackup = roomLevelBackup;
    }

    private void initLevel() {
        Level level;
        if(startSpawn == null || !startSpawn.isValid()){
            level = Server.getInstance().getLevelByName(startLevel);
        }else{
            level = startSpawn.getLevel();
        }
        if(level == null){
            MainClass.plugin.getLogger().error("Can not find the level!");
            return;
        }
        level.setThundering(false);
        level.setRaining(false);
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
        RoomStatus roomStatus = this.getRoomStatus();
        if(roomStatus != RoomStatus.ROOM_STATUS_WAIT && roomStatus != RoomStatus.ROOM_STATUS_PreStart){
            player.sendMessage("房间游戏已经开始！");
            return false;
        }
        if(this.players.size() < this.maxPlayer){
            if(this.players.contains(player)){
                player.sendMessage("您已经在房间中了！");
                return false;
            }else{
                Server.getInstance().getPluginManager().callEvent(new PlayerJoinRoomEvent(this,player));
                Inventory.saveBag(player);
                playerProperties.put(player, new LinkedHashMap<>());
                this.players.add(player);
                for(Player p: this.players){
                    p.sendMessage(player.getName() + " §l§a加入房间 【"+this.players.size()+"/"+this.maxPlayer+"】");
                }
                return true;
            }
        }
        return false;
    }

    public void setStartLevel(String startLevel) {
        this.startLevel = startLevel;
    }

    public void removePlayer(Player player, Boolean saveBag){
        Server.getInstance().getPluginManager().callEvent(new PlayerLeaveRoomEvent(this,player));
        Inventory.loadBag(player);
        player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn(), null);
        this.players.remove(player);
    }

    public static Room getRoom(String gameName, Player p){
        for(Room room: MainClass.RoomHashMap){
            if(room.players.contains(p) && room.gameName.equals(gameName)){
                return room;
            }
        }
        return null;
    }

    @SuppressWarnings("This method is not provided for other plugins development!")
    public static Room getRoom(Player p){
        for(Room room: MainClass.RoomHashMap){
            if(room.players.contains(p)){
                return room;
            }
        }
        return null;
    }

    public static Room getRoom(String gameName, String roomName){
        for(Room room: MainClass.RoomHashMap){
            if(room.roomName.equals(roomName) && room.gameName.equals(gameName)){
                return room;
            }
        }
        return null;
    }

    public void detectToReset(Boolean resetMap){
        this.players = new ArrayList<>();
        this.roundCache = 0;
        this.teamCache = new HashMap<>();
        this.time = 0;
        if(resetMap) {
            if(this.getRoomStatus() != RoomStatus.ROOM_MapInitializing && this.getRoomStatus() != RoomStatus.ROOM_STATUS_WAIT) {
                MainClass.plugin.getLogger().alert("检测到房间内无玩家，正在重置地图，房间:"+this.getRoomName());
                if(Arena.reloadLevel(this)){
                    initLevel();
                }
            }
        }
        this.roomStatus = RoomStatus.ROOM_STATUS_WAIT;
    }

    public String getGameName() {
        return gameName;
    }

    public void resetAll(){
        //Server.getInstance().getScheduler().scheduleAsyncTask(MainClass.plugin,new AsyncBlockCleanTask(this));
        for(Player player: players){
            Inventory.loadBag(player);
            Server.getInstance().getPluginManager().callEvent(new PlayerLeaveRoomEvent(this,player));
            player.teleport(getEndSpawn(), null);
        }
        this.players = new ArrayList<>();
        this.roundCache = 0;
        this.teamCache = new HashMap<>();
        this.time = 0;
        this.playerProperties = new LinkedHashMap<>();
        MainClass.plugin.getLogger().alert("检测到房间游戏结束，正在重置地图，房间:"+this.getRoomName());
        this.setRoomStatus(RoomStatus.ROOM_MapInitializing);
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
        Level level = Server.getInstance().getLevelByName(waitLevel);
        if(level != null) {
            return Position.fromObject(waitSpawn, level);
        }else{
            return Server.getInstance().getDefaultLevel().getSafeSpawn();
        }
    }

    public void setEndSpawn(Position endSpawn) {
        this.endSpawn = endSpawn;
        endLevel = endSpawn.getLevel().getName();
    }

    public Position getEndSpawn() {
        Level level = Server.getInstance().getLevelByName(endLevel);
        if(level != null) {
            return Position.fromObject(endSpawn, level);
        }else{
            return Server.getInstance().getDefaultLevel().getSafeSpawn();
        }
    }

    public Position getStartSpawn() {
        Level level = Server.getInstance().getLevelByName(startLevel);
        if(level != null) {
            return Position.fromObject(startSpawn, level);
        }else{
            return Server.getInstance().getDefaultLevel().getSafeSpawn();
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
