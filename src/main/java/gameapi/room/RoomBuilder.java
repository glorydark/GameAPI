package gameapi.room;

import cn.nukkit.level.Level;
import gameapi.room.team.BaseTeam;
import gameapi.utils.AdvancedLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author glorydark
 * @date {2023/7/13} {19:27}
 */
public class RoomBuilder {

    protected String gameName = "Undefined";

    protected RoomRule roomRule = new RoomRule(0);

    protected String roomLevelBackup = "";

    protected int maxRound = 1;

    protected int minPlayer = 2;

    protected int maxPlayer = 16;

    protected ConcurrentHashMap<String, BaseTeam> teamCache = new ConcurrentHashMap<>();

    protected boolean resetMap = false;

    protected boolean temporary = false;

    protected boolean preStartPass = false;

    protected int waitTime = 10;

    protected int gameWaitTime = 10;

    protected int gameTime = 10;

    protected int gameEndTime = 10;

    protected int ceremonyTime = 10;

    protected Level playLevel = null;

    protected AdvancedLocation waitSpawn = new AdvancedLocation();

    protected List<AdvancedLocation> startSpawns = new ArrayList<>();

    protected AdvancedLocation endSpawn = new AdvancedLocation();

    protected List<AdvancedLocation> spectatorSpawns = new ArrayList<>();

    protected List<String> winConsoleCommands = new ArrayList<>();

    protected List<String> loseConsoleCommands = new ArrayList<>();


    public RoomBuilder(){

    }

    public RoomBuilder gameName(String gameName){
        this.gameName = gameName;
        return this;
    }

    public RoomBuilder roomRule(RoomRule roomRule){
        this.roomRule = roomRule;
        return this;
    }

    public RoomBuilder roomLevelBackup(String roomLevelBackup){
        this.roomLevelBackup = roomLevelBackup;
        return this;
    }

    public RoomBuilder maxRound(int maxRound){
        this.maxRound = maxRound;
        return this;
    }

    public RoomBuilder temporary(boolean temporary){
        this.temporary = temporary;
        return this;
    }

    public RoomBuilder resetMap(boolean resetMap){
        this.resetMap = resetMap;
        return this;
    }

    public RoomBuilder preStartPass(boolean preStartPass){
        this.preStartPass = preStartPass;
        return this;
    }

    public RoomBuilder minPlayer(int minPlayer){
        this.minPlayer = minPlayer;
        return this;
    }

    public RoomBuilder maxPlayer(int maxPlayer){
        this.maxPlayer = maxPlayer;
        return this;
    }

    public RoomBuilder waitTime(int waitTime){
        this.waitTime = waitTime;
        return this;
    }

    public RoomBuilder gameWaitTime(int gameWaitTime){
        this.gameWaitTime = gameWaitTime;
        return this;
    }

    public RoomBuilder gameTime(int gameTime){
        this.gameTime = gameTime;
        return this;
    }

    public RoomBuilder gameEndTime(int gameEndTime){
        this.gameEndTime = gameEndTime;
        return this;
    }

    public RoomBuilder ceremonyTime(int ceremonyTime){
        this.ceremonyTime = ceremonyTime;
        return this;
    }

    public RoomBuilder winConsoleCommand(String... winConsoleCommands){
        this.winConsoleCommands.addAll(Arrays.asList(winConsoleCommands));
        return this;
    }

    public RoomBuilder loseConsoleCommand(String... loseConsoleCommand){
        this.loseConsoleCommands.addAll(Arrays.asList(loseConsoleCommand));
        return this;
    }

    public RoomBuilder team(BaseTeam... teams){
        for(BaseTeam team : teams){
            this.teamCache.put(team.getRegistryName(), team);
        }
        return this;
    }

    public RoomBuilder setWaitSpawn(AdvancedLocation waitSpawn){
        this.waitSpawn = waitSpawn;
        return this;
    }

    public RoomBuilder addStartSpawn(AdvancedLocation... startSpawn){
        this.startSpawns.addAll(Arrays.asList(startSpawn));
        return this;
    }

    public RoomBuilder setEndSpawn(AdvancedLocation endSpawn){
        this.endSpawn = endSpawn;
        return this;
    }

    public RoomBuilder addSpectatorSpawn(AdvancedLocation... spectatorSpawn){
        this.spectatorSpawns.addAll(Arrays.asList(spectatorSpawn));
        return this;
    }

    public RoomBuilder playLevel(Level playLevel){
        this.playLevel = playLevel;
        return this;
    }

    public Room build(){
        // Compulsory
        Room room = new Room(gameName, roomRule, roomLevelBackup, maxRound);
        room.setMinPlayer(minPlayer);
        room.setMaxPlayer(maxPlayer);
        room.setTemporary(temporary);
        room.setResetMap(resetMap);
        room.setPreStartPass(preStartPass);
        room.setWaitTime(waitTime);
        room.setGameWaitTime(gameWaitTime);
        room.setGameTime(gameTime);
        room.setGameEndTime(gameEndTime);
        room.setCeremonyTime(ceremonyTime);

        room.setWaitSpawn(waitSpawn);
        room.setStartSpawn(startSpawns);
        room.setEndSpawn(endSpawn);
        room.setSpectatorSpawn(spectatorSpawns);
        room.setPlayLevel(playLevel);
        // Selective
        room.setWinConsoleCommands(winConsoleCommands);
        room.setLoseConsoleCommands(loseConsoleCommands);
        room.teamCache = teamCache;
        return room;
    }
    
}
