package gameapi.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import gameapi.listener.*;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.manager.RoomManager;
import gameapi.manager.tools.PlayerTempStateManager;
import gameapi.manager.tools.ScoreboardManager;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Glorydark
 */
public class RoomTask extends AsyncTask {

    public boolean onUpdate(Room room) {
        if (room == null) {
            return false;
        }
        if (room.getRoomStatus() == RoomStatus.ROOM_MapLoadFailed || room.getRoomStatus() == RoomStatus.ROOM_REMOVE) {
            return false;
        }
        room.getPlayers().removeIf(player -> player == null || !player.isOnline());
        switch (room.getRoomStatus()) {
            case ROOM_STATUS_WAIT:
                if (room.isTemporary() && room.getPlayers().size() < 1) {
                    room.resetAll();
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomWaitListener(room));
                this.execute(room, ListenerStatusType.Wait);
                break;
            case ROOM_STATUS_GameEnd:
                if (room.getPlayers().size() < 1) {
                    room.resetAll();
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomGameEndListener(room));
                this.execute(room, ListenerStatusType.GameEnd);
                break;
            case ROOM_STATUS_Ceremony:
                if (room.getPlayers().size() < 1) {
                    room.setTime(room.getCeremonyTime());
                }
                GameListenerRegistry.callEvent(room, new RoomCeremonyListener(room));
                this.execute(room, ListenerStatusType.Ceremony);
                break;
            case ROOM_STATUS_PreStart:
                if (room.getPlayers().size() < room.getMinPlayer()) {
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT);
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomPreStartListener(room));
                this.execute(room, ListenerStatusType.PreStart);
                break;
            case ROOM_STATUS_GameStart:
                if (room.getPlayers().size() < 1) {
                    room.resetAll();
                    return true;
                } else {
                    if (room.getTeams().size() > 1) {
                        AtomicInteger hasPlayer = new AtomicInteger(0);
                        room.getTeams().forEach(team -> {
                            if (team.getPlayers().size() > 0) {
                                hasPlayer.addAndGet(1);
                            }
                        });
                        if (hasPlayer.get() < room.getMinPlayer()) {
                            room.setTime(0);
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd);
                            for (Player player : room.getPlayers()) {
                                player.getInventory().clearAll();
                            }
                            return true;
                        }
                    } else {
                        if (room.getPlayers().size() < room.getMinPlayer()) {
                            room.setTime(0);
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd);
                            for (Player player : room.getPlayers()) {
                                player.getInventory().clearAll();
                            }
                            return true;
                        }
                    }
                }
                GameListenerRegistry.callEvent(room, new RoomGameProcessingListener(room));
                this.execute(room, ListenerStatusType.InGame);
                break;
            case ROOM_STATUS_GameReadyStart:
                if (room.getPlayers().size() < 1) {
                    room.resetAll();
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomReadyStartListener(room));
                this.execute(room, ListenerStatusType.ReadyStart);
                break;
            case ROOM_STATUS_NextRoundPreStart:
                GameListenerRegistry.callEvent(room, new RoomNextRoundPreStartListener(room));
                this.execute(room, ListenerStatusType.NextRoundPreStart);
                break;
        }
        return true;
    }

    @Override
    public void onRun() {
        if (RoomManager.loadedRooms.size() > 0) {
            for (Map.Entry<String, List<Room>> entry : RoomManager.loadedRooms.entrySet()) {
                List<Room> rooms = new ArrayList<>(entry.getValue());
                for (Room room : rooms) {
                    if (!onUpdate(room)) {
                        RoomManager.loadedRooms.get(entry.getKey()).remove(room);
                    }
                }
            }
            //Server.getInstance().getLogger().alert("目前房间数量:"+counts);
        }
    }

    public void execute(Room room, ListenerStatusType type) {
        switch (type) {
            case Wait:
                if (room.getPlayers().size() >= room.getMinPlayer()) {
                    if (!room.getRoomRule().isNeedPreStartPass() || room.isPreStartPass()) {
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_PreStart);
                        room.setRound(0);
                        room.getStatusExecutor().beginPreStart();
                    } else {
                        room.getStatusExecutor().onWait();
                    }
                } else {
                    room.getStatusExecutor().onWait();
                }
                break;
            case PreStart:
                if (room.getTime() >= room.getWaitTime()) {
                    room.getStatusExecutor().beginReadyStart();
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_GameReadyStart);
                } else {
                    room.getStatusExecutor().onPreStart();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case ReadyStart:
                if (room.getTime() >= room.getGameWaitTime()) {
                    room.setRound(room.getRound() + 1);
                    room.getStatusExecutor().beginGameStart();
                    room.setStartMillis(System.currentTimeMillis());
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_GameStart);
                } else {
                    room.getStatusExecutor().onReadyStart();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case InGame:
                if (room.getTime() >= room.getGameTime()) {
                    room.getStatusExecutor().beginGameEnd();
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd);
                } else {
                    room.getStatusExecutor().onGameStart();
                    if (!room.getRoomRule().isNoTimeLimit()) {
                        room.setTime(room.getTime() + 1);
                    }
                }
                break;
            case GameEnd:
                if (room.getTime() >= room.getGameEndTime()) {
                    if (room.getRound() == room.getMaxRound()) {
                        room.getStatusExecutor().beginCeremony();
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_Ceremony);
                    } else {
                        room.getStatusExecutor().beginNextRoundPreStart();
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_NextRoundPreStart);
                    }
                } else {
                    room.getStatusExecutor().onGameEnd();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case Ceremony:
                if (room.getTime() >= room.getCeremonyTime()) {
                    for (Player p : room.getPlayers()) {
                        p.setGamemode(0);
                        PlayerTempStateManager.loadAllData(p);
                        ScoreboardManager.removeScoreboard(p);
                        ScoreboardManager.scoreboardConcurrentHashMap.remove(p);
                        //玩家先走
                        p.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
                    }
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_End);
                    room.resetAll();
                } else {
                    room.getStatusExecutor().onCeremony();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case NextRoundPreStart:
                if (room.getTime() >= room.getGameWaitTime()) {
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_GameStart);
                    room.setRound(room.getRound() + 1);
                    room.getStatusExecutor().beginGameStart();
                } else {
                    room.getStatusExecutor().onNextRoundPreStart();
                    room.setTime(room.getTime() + 1);
                }
                break;
        }
    }

    public enum ListenerStatusType {
        Wait,
        PreStart,
        ReadyStart,
        InGame,
        GameEnd,
        Ceremony,
        NextRoundPreStart,
    }
}
