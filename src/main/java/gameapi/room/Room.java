package gameapi.room;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import com.sun.istack.internal.NotNull;
import gameapi.MainClass;
import gameapi.event.PlayerJoinRoomEvent;
import gameapi.event.PlayerLeaveRoomEvent;
import gameapi.inventory.Inventory;
import gameapi.task.AsyncBlockCleanTask;

import java.util.*;

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
    private HashMap<String, List<Player>> teamCache = new HashMap<>();
    private LinkedList<Block> placeBlocks = new LinkedList<>();
    private LinkedList<Block> breakBlocks = new LinkedList<>();
    private LinkedList<Entity> explodeEntity = new LinkedList<>();
    private HashMap<Player, Float> playersHealth = new HashMap<>();

    public Room(RoomRule roomRule, int round){
        this.MaxRound = round;
        this.roomRule = roomRule;
    }

    public static void addTeam(Room room ,String string, @NotNull List<Player> players){
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
        Server.getInstance().getScheduler().scheduleAsyncTask(MainClass.plugin,new AsyncBlockCleanTask(this));
        for(Player player: players){
            Inventory.loadBag(player);
        }
        this.players = new ArrayList<>();
        this.roomStatus = RoomStatus.ROOM_STATUS_WAIT;
        this.roundCache = 0;
        this.teamCache = new HashMap<>();
        this.time = 0;
    }

    public LinkedList<Block> getBreakBlocks() {
        return breakBlocks;
    }

    public LinkedList<Block> getPlaceBlocks() {
        return placeBlocks;
    }

    public void setBreakBlocks(LinkedList<Block> breakBlocks) {
        this.breakBlocks = breakBlocks;
    }

    public void setPlaceBlocks(LinkedList<Block> placeBlocks) {
        this.placeBlocks = placeBlocks;
    }

    public void addBreakBlock(Block block) {
        this.breakBlocks.add(block);
    }

    public void addPlaceBlock(Block block) {
        this.placeBlocks.add(block);
    }

    public void addBreakBlocks(List<Block> block) {
        this.breakBlocks.addAll(block);
    }

    public void addPlaceBlocks(List<Block> block) {
        this.placeBlocks.addAll(block);
    }

    public void removeBreakBlock(Block block) {
        this.breakBlocks.remove(block);
    }

    public void removePlaceBlock(Block block) {
        this.placeBlocks.remove(block);
    }

    public void removeBreakBlocks(List<Block> block) {
        this.breakBlocks.removeAll(block);
    }

    public void removePlaceBlocks(List<Block> block) {
        this.placeBlocks.removeAll(block);
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
    }

    public Position getWaitSpawn() {
        return waitSpawn;
    }

    public void setEndSpawn(Position endSpawn) {
        this.endSpawn = endSpawn;
    }

    public Position getEndSpawn() {
        return endSpawn;
    }

    public Position getStartSpawn() {
        return startSpawn;
    }

    public void setStartSpawn(Position startSpawn) {
        this.startSpawn = startSpawn;
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

    public LinkedList<Entity> getExplodeEntity() {
        return this.explodeEntity;
    }

    public void setExplodeEntity(LinkedList<Entity> explodeEntity) {
        this.explodeEntity = explodeEntity;
    }

    public void addExplodeEntity(Entity entity) {
        this.explodeEntity.add(entity);
    }

    public void removeExplodeEntity(Entity entity) {
        this.explodeEntity.remove(entity);
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
        for(Player player:this.playersHealth.keySet()){
            this.playersHealth.put(player,health);
        }
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
