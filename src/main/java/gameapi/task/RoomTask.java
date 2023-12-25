package gameapi.task;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLiquid;
import cn.nukkit.scheduler.AsyncTask;
import gameapi.GameAPI;
import gameapi.event.block.RoomBlockTreadEvent;
import gameapi.event.player.RoomPlayerEnterPortalEvent;
import gameapi.event.room.*;
import gameapi.inventory.InventoryTools;
import gameapi.listener.*;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.scoreboard.ScoreboardTools;

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
        if (room.getRoomStatus() == RoomStatus.ROOM_MapLoadFailed || room.getRoomStatus() == RoomStatus.ROOM_MapInitializing) {
            return false;
        }
        room.getPlayers().removeIf(player -> player == null || !player.isOnline());
        for (Player player : room.getPlayers()) {
            if (player.isOnline()) {
                Block block = player.getLevelBlock();
                if (!(block instanceof BlockLiquid) && player.getY() == player.getPosition().round().getY()) {
                    RoomBlockTreadEvent roomBlockTreadEvent = new RoomBlockTreadEvent(room, block, player);
                    GameListenerRegistry.callEvent(room, roomBlockTreadEvent);
                }

                for (Block collisionBlock : player.getCollisionBlocks()) {
                    if (collisionBlock.getId() == 90) {
                        RoomPlayerEnterPortalEvent roomPlayerEnterPortalEvent = new RoomPlayerEnterPortalEvent(room, player);
                        GameListenerRegistry.callEvent(room, roomPlayerEnterPortalEvent);
                        player.inPortalTicks = 0;
                        break;
                    }
                }
            }
        }
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
                    room.setRoomStatus(RoomStatus.ROOM_STATUS_WAIT, false);
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
                        if (hasPlayer.get() < 2) {
                            GameListenerRegistry.callEvent(room, new RoomGameEndEvent(room));
                            room.setTime(0);
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd, false);
                            for (Player player : room.getPlayers()) {
                                player.getInventory().clearAll();
                            }
                            return true;
                        }
                    } else {
                        if (room.getPlayers().size() < room.getMinPlayer()) {
                            room.setTime(0);
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd, false);
                            for (Player player : room.getPlayers()) {
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
        if (GameAPI.loadedRooms.size() > 0) {
            for (Map.Entry<String, List<Room>> entry : GameAPI.loadedRooms.entrySet()) {
                List<Room> rooms = new ArrayList<>(entry.getValue());
                for (Room room : rooms) {
                    if (!onUpdate(room)) {
                        GameAPI.loadedRooms.get(entry.getKey()).remove(room);
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
                        RoomPreStartEvent ev = new RoomPreStartEvent(room);
                        GameListenerRegistry.callEvent(room, ev);
                        if (!ev.isCancelled()) {
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_PreStart, false);
                            room.setTime(0);
                            room.setRound(0);
                            room.getStatusExecutor().beginPreStart();
                        }
                    } else {
                        room.getStatusExecutor().onWait();
                    }
                } else {
                    room.getStatusExecutor().onWait();
                }
                break;
            case PreStart:
                if (room.getTime() >= room.getWaitTime()) {
                    RoomReadyStartEvent ev = new RoomReadyStartEvent(room);
                    GameListenerRegistry.callEvent(room, ev);
                    if (!ev.isCancelled()) {
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_GameReadyStart, false);
                        room.setTime(0);
                        room.getStatusExecutor().beginReadyStart();
                    }
                } else {
                    room.getStatusExecutor().onPreStart();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case ReadyStart:
                if (room.getTime() >= room.getGameWaitTime()) {
                    RoomGameStartEvent ev = new RoomGameStartEvent(room);
                    GameListenerRegistry.callEvent(room, new RoomGameStartEvent(room));
                    if (!ev.isCancelled()) {
                        room.setTime(0);
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_GameStart, false);
                        room.setRound(room.getRound() + 1);
                        room.getStatusExecutor().beginGameStart();
                        room.setStartMillis(System.currentTimeMillis());
                    }
                } else {
                    room.getStatusExecutor().onReadyStart();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case InGame:
                if (room.getTime() >= room.getGameTime()) {
                    RoomGameEndEvent ev = new RoomGameEndEvent(room);
                    GameListenerRegistry.callEvent(room, ev);
                    if (!ev.isCancelled()) {
                        room.setTime(0);
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_GameEnd, false);
                        room.getStatusExecutor().beginGameEnd();
                    }
                } else {
                    room.getStatusExecutor().onGameStart();
                    if (!room.getRoomRule().isNoTimeLimit()) {
                        room.setTime(room.getTime() + 1);
                    }
                    if (!room.getRoomRule().isAllowFoodLevelChange()) {
                        room.getPlayers().forEach(player -> player.getFoodData().reset());
                    }
                }
                break;
            case GameEnd:
                if (room.getTime() >= room.getGameEndTime()) {
                    if (room.getRound() == room.getMaxRound()) {
                        RoomCeremonyEvent ev = new RoomCeremonyEvent(room);
                        GameListenerRegistry.callEvent(room, ev);
                        if (!ev.isCancelled()) {
                            room.setTime(0);
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_Ceremony, false);
                            room.getStatusExecutor().beginCeremony();
                        }
                    } else {
                        RoomNextRoundPreStartEvent ev = new RoomNextRoundPreStartEvent(room);
                        GameListenerRegistry.callEvent(room, ev);
                        if (!ev.isCancelled()) {
                            room.setTime(0);
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_NextRoundPreStart, false);
                            room.getStatusExecutor().beginNextRoundPreStart();
                        }
                    }
                } else {
                    room.getStatusExecutor().onGameEnd();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case Ceremony:
                if (room.getTime() >= room.getCeremonyTime()) {
                    RoomEndEvent ev = new RoomEndEvent(room);
                    GameListenerRegistry.callEvent(room, ev);
                    if (!ev.isCancelled()) {
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_End, false);
                        room.setTime(0);
                        for (Player p : room.getPlayers()) {
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
                    room.getStatusExecutor().onCeremony();
                    room.setTime(room.getTime() + 1);
                }
                break;
            case NextRoundPreStart:
                if (room.getTime() >= room.getGameWaitTime()) {
                    RoomGameStartEvent ev = new RoomGameStartEvent(room);
                    GameListenerRegistry.callEvent(room, new RoomGameStartEvent(room));
                    if (!ev.isCancelled()) {
                        room.setTime(0);
                        room.setRoomStatus(RoomStatus.ROOM_STATUS_GameStart, false);
                        room.setRound(room.getRound() + 1);
                        room.getStatusExecutor().beginGameStart();
                    }
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
