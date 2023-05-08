package gameapi.task;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.level.Sound;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.event.block.RoomBlockTreadEvent;
import gameapi.event.room.*;
import gameapi.fireworkapi.CreateFireworkApi;
import gameapi.inventory.InventoryTools;
import gameapi.listener.*;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.scoreboard.ScoreboardTools;
import gameapi.utils.AdvancedLocation;
import gameapi.utils.SmartTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Glorydark
 */
public class RoomTask extends AsyncTask {

    public boolean checkState(Room room){
        if (room == null) {
            return false;
        }
        if(room.getRoomStatus() == RoomStatus.ROOM_MapLoadFailed || room.getRoomStatus() == RoomStatus.ROOM_MapInitializing){
            return false;
        }
        for(Player player: room.getPlayers()){
            if(player == null || !player.isOnline()){
                room.removePlayer(player, false);
            }else{
                Block block = player.getLevelBlock();
                if(!(block instanceof BlockLiquid)){
                    RoomBlockTreadEvent roomBlockTreadEvent = new RoomBlockTreadEvent(room, block, player);
                    GameListenerRegistry.callEvent(room, roomBlockTreadEvent);
                }
            }
        }
        switch (room.getRoomStatus()) {
            case ROOM_STATUS_WAIT:
                if(room.getTemporary() && room.getPlayers().size() < 1){
                    room.detectToReset();
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomWaitListener(room));
                this.execute(room, ListenerStatusType.Wait);
                break;
            case ROOM_STATUS_GameEnd:
                if(room.getPlayers().size() < 1){
                    room.detectToReset();
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomGameEndListener(room));
                this.execute(room, ListenerStatusType.GameEnd);
                break;
            case ROOM_STATUS_Ceremony:
                if(room.getPlayers().size() < 1){
                    room.detectToReset();
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomCeremonyListener(room));
                this.execute(room, ListenerStatusType.Ceremony);
                break;
            case ROOM_STATUS_PreStart:
                if(room.getPlayers().size() < room.getMinPlayer()){
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT, false);
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomPreStartListener(room));
                this.execute(room, ListenerStatusType.PreStart);
                break;
            case ROOM_STATUS_GameStart:
                if(room.getPlayers().size() < 1){
                    room.detectToReset();
                    return true;
                }else{
                    if(room.getTeams().size() > 1) {
                        AtomicInteger hasPlayer = new AtomicInteger(0);
                        room.getTeams().forEach(team -> {
                            if (team.getPlayerList().size() > 0) {
                                hasPlayer.addAndGet(1);
                            }
                        });
                        if (hasPlayer.get() < 2) {
                            GameListenerRegistry.callEvent(room, new RoomGameEndEvent(room));
                            room.setTime(0);
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd, false);
                            for(Player player:room.getPlayers()){
                                player.getInventory().clearAll();
                            }
                            return true;
                        }
                    }else {
                        if(room.getPlayers().size() < room.getMinPlayer()){
                            room.setTime(0);
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd, false);
                            for(Player player:room.getPlayers()){
                                player.getInventory().clearAll();
                            }
                            GameListenerRegistry.callEvent(room, new RoomGameEndEvent(room));
                            return true;
                        }
                    }
                }
                GameListenerRegistry.callEvent(room, new RoomGameProcessingListener(room));
                this.execute(room, ListenerStatusType.InGame);
                break;
            case ROOM_STATUS_GameReadyStart:
                if(room.getPlayers().size() < 1){
                    room.detectToReset();
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomReadyStartListener(room));
                this.execute(room, ListenerStatusType.ReadyStart);
                break;
            case ROOM_STATUS_NextRoundPreStart:
                GameListenerRegistry.callEvent(room, new RoomNextRoundPreStartListener(room));
                this.execute(room, ListenerStatusType.ReadyStart);
                break;
        }
        return true;
    }

    @Override
    public void onRun() {
        if (GameAPI.RoomHashMap.size() > 0) {
            for(Map.Entry<String, List<Room>> entry : GameAPI.RoomHashMap.entrySet()){
                List<Room> rooms = new ArrayList<>(entry.getValue());
                for(Room room: rooms) {
                    if(!checkState(room)){
                        GameAPI.RoomHashMap.get(entry.getKey()).remove(room);
                    }
                }
            }
            //Server.getInstance().getLogger().alert("目前房间数量:"+counts);
        }
    }
    
    public void execute(Room room, ListenerStatusType type){
        switch (type){
            case Wait:
                if (room.getPlayers().size() >= room.getMinPlayer()) {
                    if(!room.getRoomRule().needPreStartPass || room.isPreStartPass()){
                        RoomPreStartEvent ev = new RoomPreStartEvent(room);
                        GameListenerRegistry.callEvent(room, ev);
                        if(!ev.isCancelled()){
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_PreStart, false);
                            room.setTime(0);
                            room.setRound(0);
                        }
                    }else{
                        SmartTools.sendActionbar(room.getPlayers(), GameAPI.getLanguage().getText("room.actionbar.wait.needStartPass"));
                    }
                }else{
                    SmartTools.sendActionbar(room.getPlayers(), GameAPI.getLanguage().getText("room.actionbar.wait.waitForPlayers", room.getPlayers().size(), room.getMinPlayer()));
                }
                break;
            case PreStart:
                if (room.getTime() >= room.getWaitTime()) {
                    RoomReadyStartEvent ev = new RoomReadyStartEvent(room);
                    GameListenerRegistry.callEvent(room, ev);
                    if(!ev.isCancelled()){
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_GameReadyStart, false);
                        room.setTime(0);
                        for(Player p:room.getPlayers()){
                            p.getInventory().clearAll();
                        }
                    }
                } else {
                    if (room.getPlayers().size() < room.getMinPlayer()) {
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT, false);
                        room.setTime(0);
                        return;
                    }
                    SmartTools.sendTitle(room.getPlayers(), TextFormat.LIGHT_PURPLE+String.valueOf(room.getWaitTime() - room.getTime()),GameAPI.getLanguage().getText("room.title.preStart.subtitle"));
                    room.setTime(room.getTime()+1);
                }
                break;
            case ReadyStart:
                if (room.getTime() >= room.getGameWaitTime()) {
                    RoomGameStartEvent ev = new RoomGameStartEvent(room);
                    GameListenerRegistry.callEvent(room, new RoomGameStartEvent(room));
                    if(!ev.isCancelled()) {
                        room.setTime(0);
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_GameStart, false);
                        List<AdvancedLocation> startSpawns = room.getStartSpawn();
                        if (room.getTeams().size() > 0) {
                            room.allocatePlayerToTeams();
                            room.getPlayers().forEach(room::teleportToSpawn);
                        } else {
                            if (startSpawns.size() > 1) {
                                for (Player p : room.getPlayers()) {
                                    if (room.getPlayerProperties(p.getName(), "spawnIndex") == null) {
                                        Random random = new Random(System.currentTimeMillis());
                                        AdvancedLocation location = startSpawns.get(random.nextInt(startSpawns.size()));
                                        location.teleport(p);
                                    } else {
                                        AdvancedLocation location = startSpawns.get((Integer) room.getPlayerProperties(p.getName(), "spawnIndex"));
                                        location.teleport(p);
                                    }
                                }
                            } else if (room.getStartSpawn().size() == 1) {
                                AdvancedLocation location = startSpawns.get(0);
                                for (Player p : room.getPlayers()) {
                                    location.teleport(p);
                                }
                            }
                        }
                        room.setRound(room.getRound() + 1);
                        for (Player p : room.getPlayers()) {
                            p.getFoodData().reset();
                            p.setGamemode(room.getRoomRule().gameMode);
                            p.sendTitle(GameAPI.getLanguage().getText("room.title.start"), GameAPI.getLanguage().getText("room.subtitle.start"));
                        }
                    }
                } else {
                    for (Player p : room.getPlayers()) {
                        int lastSec = room.getGameWaitTime() - room.getTime();
                        if(lastSec > 10) {
                            p.getLevel().addSound(p.getPosition(), Sound.NOTE_HARP);
                            p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.readyStart", room.getGameWaitTime() - room.getTime()));
                        }else{
                            if(lastSec == 1){
                                p.getLevel().addSound(p.getPosition(), Sound.NOTE_FLUTE);
                            }else{
                                p.getLevel().addSound(p.getPosition(), Sound.NOTE_BASS);
                            }
                            switch (lastSec){
                                case 10:
                                    p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.preStart.ten", lastSec));
                                    break;
                                case 9:
                                    p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.preStart.nine", lastSec));
                                    break;
                                case 8:
                                    p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.preStart.eight", lastSec));
                                    break;
                                case 7:
                                    p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.preStart.seven", lastSec));
                                    break;
                                case 6:
                                    p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.preStart.six", lastSec));
                                    break;
                                case 5:
                                    p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.preStart.five", lastSec));
                                    break;
                                case 4:
                                    p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.preStart.four", lastSec));
                                    break;
                                case 3:
                                    p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.preStart.three", lastSec));
                                    break;
                                case 2:
                                    p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.preStart.two", lastSec));
                                    break;
                                case 1:
                                    p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.preStart.one", lastSec));
                                    break;
                                case 0:
                                    p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.preStart.zero", lastSec));
                                    break;
                            }
                        }
                    }
                    room.setTime(room.getTime()+1);
                }
                break;
            case InGame:
                if (room.getTime() >= room.getGameTime()) {
                    RoomGameEndEvent ev = new RoomGameEndEvent(room);
                    GameListenerRegistry.callEvent(room, ev);
                    if(!ev.isCancelled()) {
                        room.setTime(0);
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd, false);
                        for(Player player:room.getPlayers()){
                            player.getInventory().clearAll();
                        }
                    }
                }else{
                    if(!room.getRoomRule().noTimeLimit) {
                        room.setTime(room.getTime() + 1);
                    }
                    if(!room.getRoomRule().allowFoodLevelChange) {
                        room.getPlayers().forEach(player -> player.getFoodData().reset());
                    }
                }
                break;
            case GameEnd:
                if (room.getTime() >= room.getGameEndTime()) {
                    if(room.getRound() == room.getMaxRound()) {
                        RoomCeremonyEvent ev = new RoomCeremonyEvent(room);
                        GameListenerRegistry.callEvent(room, ev);
                        if (!ev.isCancelled()) {
                            room.setTime(0);
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_Ceremony, false);
                        }
                    }else{
                        RoomNextRoundPreStartEvent ev = new RoomNextRoundPreStartEvent(room);
                        GameListenerRegistry.callEvent(room, ev);
                        if (!ev.isCancelled()) {
                            room.setTime(0);
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_NextRoundPreStart, false);
                        }
                    }
                }else {
                    room.setTime(room.getTime()+1);
                    if(room.getRound() == room.getMaxRound()){
                        SmartTools.sendActionbar(room.getPlayers(), GameAPI.getLanguage().getText("room.actionbar.preStart", room.getGameEndTime() - room.getTime()));
                    }else{
                        SmartTools.sendActionbar(room.getPlayers(), GameAPI.getLanguage().getText("room.actionbar.nextRound", room.getGameEndTime() - room.getTime()));
                    }
                }
                break;
            case Ceremony:
                if (room.getTime() >= room.getCeremonyTime()) {
                    RoomEndEvent ev = new RoomEndEvent(room);
                    GameListenerRegistry.callEvent(room, ev);
                    if(!ev.isCancelled()){
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_End, false);
                        room.setTime(0);
                        for(Player p:room.getPlayers()) {
                            p.setGamemode(0);
                            InventoryTools.loadBag(p);
                            ScoreboardTools.removeScoreboard(p);
                            ScoreboardTools.scoreboardConcurrentHashMap.remove(p);
                            //玩家先走
                            room.getEndSpawn().teleport(p);
                        }
                        room.resetAll();
                    }
                } else {
                    room.setTime(room.getTime()+1);
                    for (Player p : room.getPlayers()) {
                        ThreadLocalRandom random = ThreadLocalRandom.current();
                        int i1 = random.nextInt(14);
                        int i2 = random.nextInt(4);
                        CreateFireworkApi.spawnFirework(p.getPosition(), CreateFireworkApi.getColorByInt(i1), CreateFireworkApi.getExplosionTypeByInt(i2));
                        p.sendActionBar(GameAPI.getLanguage().getText("room.actionbar.ceremony", room.getCeremonyTime() - room.getTime()));
                    }
                }
                break;
        }
    }
    
    public enum ListenerStatusType{
        Wait,
        PreStart,
        ReadyStart,
        InGame,
        GameEnd,
        Ceremony
    }
}
