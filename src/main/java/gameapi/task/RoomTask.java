package gameapi.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.scheduler.Task;
import gameapi.GameAPI;
import gameapi.event.room.*;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.manager.RoomManager;
import gameapi.manager.tools.ScoreboardManager;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.room.state.StageState;
import gameapi.utils.text.GameTranslationContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Glorydark
 */
public class RoomTask extends Task {

    @Override
    public void onRun(int i) {
        if (!RoomManager.getLoadedRooms().isEmpty()) {
            for (Map.Entry<String, List<Room>> entry : RoomManager.getLoadedRooms().entrySet()) {
                List<Room> rooms = new ArrayList<>(entry.getValue());
                for (Room room : rooms) {
                    if (!this.onUpdate(room)) {
                        RoomManager.getLoadedRooms().get(entry.getKey()).remove(room);
                    }
                }
            }
            //Server.getInstance().getLogger().alert("目前房间数量:"+counts);
        }
    }

    public boolean onUpdate(Room room) {
        if (room == null) {
            return false;
        }
        room.getPlayers().remove(null);
        for (Player player : new ArrayList<>(room.getPlayers())) {
            if (!player.isOnline()) {
                room.removePlayer(null);
            }
        }

        if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_START) {
            // This is specifically designed for some special events that lasts for few seconds
            for (StageState stageState : new ArrayList<>(room.getStageStates())) {
                if (stageState.getTime() == 0) {
                    stageState.onStart(room);
                }
                stageState.setTime(stageState.getTime() + 1);
                if (stageState.isEnd()) {
                    stageState.onEnd(room);
                    room.getStageStates().remove(stageState);
                } else {
                    stageState.onTick(room);
                }
            }
        }

        switch (room.getRoomStatus()) {
            case ROOM_PLAYBACK:
                if (room.getPlayers().isEmpty()) {
                    GameAPI.getGameDebugManager().info("Detect that temp room " + room.getRoomName() + " has no players whilst playback, start destroying...");
                    room.resetAll();
                    return true;
                }
                break;
            case ROOM_STATUS_WAIT:
                if (room.isTemporary()) {
                    if (room.isAutoDestroyOverTime()) {
                        if (System.currentTimeMillis() >= room.getCreateMillis() + room.getMaxTempRoomWaitMillis()) {
                            GameAPI.getGameDebugManager().info("Detect that temp room " + room.getRoomName() + " has reached the maximum of waiting time, start destroying...");
                            room.resetAll();
                            return true;
                        }
                    }
                }
                GameListenerRegistry.callEvent(room, new RoomWaitTickEvent(room));
                this.onStateUpdate(room, ListenerStatusType.WAIT);
                break;
            case ROOM_STATUS_PRESTART:
                if (room.getPlayers().size() < room.getMinPlayer()) {
                    if (room.isTemporary()) {
                        room.resetAll();
                    } else {
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT, "internal");
                    }
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomPreStartTickEvent(room));
                this.onStateUpdate(room, ListenerStatusType.PRESTART);
                break;
            case ROOM_STATUS_READY_START:
                if (room.getPlayers().size() < room.getMinPlayer()) {
                    room.resetAll();
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomReadyStartTickEvent(room));
                this.onStateUpdate(room, ListenerStatusType.READY_START);
                break;
            case ROOM_STATUS_START:
                if (room.getPlayers().isEmpty()) {
                    room.resetAll();
                    return true;
                } else {
                    if (!room.getRoomRule().isTestStatus()) {
                        if (room.getTeams().size() > 1) {
                            AtomicInteger hasPlayer = new AtomicInteger(0);
                            room.getTeams().forEach(team -> {
                                if (team.getSize() > 0) {
                                    hasPlayer.addAndGet(1);
                                }
                            });
                            if (hasPlayer.get() <= 1) {
                                if (hasPlayer.get() == 0) {
                                    room.resetAll();
                                } else {
                                    room.setRoomStatus(RoomStatus.ROOM_STATUS_GAME_END, "internal");
                                }
                                return true;
                            }
                        } else {
                            if (room.getPlayers().size() < room.getMinPlayer()) {
                                if (room.getPlayers().isEmpty()) {
                                    room.resetAll();
                                } else {
                                    room.setRoomStatus(RoomStatus.ROOM_STATUS_GAME_END, "internal");
                                }
                                return true;
                            }
                        }
                    }
                }
                GameListenerRegistry.callEvent(room, new RoomGameStartTickEvent(room));
                this.onStateUpdate(room, ListenerStatusType.START);
                break;
            case ROOM_STATUS_GAME_END:
                if (room.getPlayers().isEmpty()) {
                    room.resetAll();
                    return true;
                }
                GameListenerRegistry.callEvent(room, new RoomGameEndTickEvent(room));
                this.onStateUpdate(room, ListenerStatusType.GAME_END);
                break;
            case ROOM_STATUS_CEREMONY:
                if (room.getPlayers().isEmpty()) {
                    room.resetAll();
                }
                GameListenerRegistry.callEvent(room, new RoomCeremonyTickEvent(room));
                this.onStateUpdate(room, ListenerStatusType.CEREMONY);
                break;
            case ROOM_STATUS_NEXT_ROUND_PRESTART:
                if (room.getPlayers().isEmpty()) {
                    room.resetAll();
                    return true;
                } else {
                    if (room.getTeams().size() > 1) {
                        AtomicInteger hasPlayer = new AtomicInteger(0);
                        room.getTeams().forEach(team -> {
                            if (!team.getPlayers().isEmpty()) {
                                hasPlayer.addAndGet(1);
                            }
                        });
                        if (hasPlayer.get() <= 1) {
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GAME_END, "internal");
                            return true;
                        }
                    } else {
                        if (room.getPlayers().size() < room.getMinPlayer()) {
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GAME_END, "internal");
                            return true;
                        }
                    }
                }
                GameListenerRegistry.callEvent(room, new RoomNextRoundPreStartTickEvent(room));
                this.onStateUpdate(room, ListenerStatusType.NEXT_ROUND_PRE_START);
                break;
        }
        return true;
    }

    public void onStateUpdate(Room room, ListenerStatusType type) {
        switch (type) {
            case WAIT:
                if (room.getPlayers().size() >= room.getMinPlayer()) {
                    if (room.isAllowedToStart()) {
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_PRESTART, "internal");
                        room.setRound(0);
                        room.getStatusExecutor().beginPreStart();
                    } else {
                        room.getStatusExecutor().onWait();
                    }
                } else {
                    room.getStatusExecutor().onWait();
                }
                break;
            case PRESTART:
                if (room.getTime() >= room.getWaitTime()) {
                    room.getStatusExecutor().beginReadyStart();
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_READY_START, "internal");
                } else {
                    int leftWaitTime = room.getWaitTime() - room.getTime();
                    if (leftWaitTime >= 15
                            && room.getPlayers().size() >= room.getAccelerateWaitCountDownPlayerCount()) {
                        room.sendMessageToAll(new GameTranslationContainer("room.game.wait.time_accelerated"));
                        room.setTime(room.getWaitTime() - 15);
                    }
                    room.getStatusExecutor().onPreStart();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case READY_START:
                if (room.getTime() >= room.getGameWaitTime()) {
                    room.setRound(room.getRound() + 1);
                    room.getStatusExecutor().beginGameStart();
                    room.setStartMillis(System.currentTimeMillis());
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_START, "internal");
                } else {
                    room.getStatusExecutor().onReadyStart();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case START:
                if (!room.getRoomRule().isNoTimeLimit() && room.getTime() >= room.getGameTime()) {
                    if (room.getRound() >= room.getMaxRound()) {
                        room.getStatusExecutor().beginGameEnd();
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_GAME_END, "internal");
                    } else {
                        room.getStatusExecutor().beginNextRoundPreStart();
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_NEXT_ROUND_PRESTART, "internal");
                    }
                } else {
                    room.getStatusExecutor().onGameStart();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case GAME_END:
                if (room.getTime() >= room.getGameEndTime()) {
                    room.getStatusExecutor().beginCeremony();
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_CEREMONY, "internal");
                } else {
                    room.getStatusExecutor().onGameEnd();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case CEREMONY:
                if (room.getTime() >= room.getCeremonyTime()) {
                    for (Player p : room.getPlayers()) {
                        p.setGamemode(2);
                        ScoreboardManager.removeScoreboard(p);
                        ScoreboardManager.scoreboardConcurrentHashMap.remove(p);
                        //玩家先走
                        p.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_END, "internal");
                    room.resetAll();
                } else {
                    room.getStatusExecutor().onCeremony();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case NEXT_ROUND_PRE_START:
                if (room.getTime() >= room.getNextRoundPreStartTime()) {
                    room.setRound(room.getRound() + 1);
                    room.getStatusExecutor().beginGameStart();
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_START, "internal");
                } else {
                    room.getStatusExecutor().onNextRoundPreStart();
                    room.setTime(room.getTime() + 1);
                }
                break;
        }
    }

    public enum ListenerStatusType {
        WAIT,
        PRESTART,
        READY_START,
        START,
        GAME_END,
        CEREMONY,
        NEXT_ROUND_PRE_START,
    }
}
