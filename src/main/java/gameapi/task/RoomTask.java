package gameapi.task;

import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import gameapi.GameAPI;
import gameapi.manager.RoomManager;
import gameapi.room.Room;
import gameapi.room.state.StageState;
import gameapi.room.status.base.CustomRoomStatus;
import gameapi.room.utils.reason.QuitRoomReason;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        room.getPlayersWithoutCreate().remove(null);
        for (Player player : room.getPlayers()) {
            if (!player.isOnline()) {
                room.removePlayer(player, QuitRoomReason.PLAYER_OFFLINE);
            }
        }

        CustomRoomStatus status = room.getCurrentRoomStatus();

        if (status.isStageStateEnabled(room)) {
            // This is specifically designed for some special events that lasts for few seconds
            for (StageState stageState : new ArrayList<>(room.getStageStates())) {
                try {
                    if (!stageState.isExecuteStartAction()) {
                        stageState.setExecuteStartAction(true);
                        stageState.onStart(room);
                    }
                    stageState.setTime(stageState.getTime() + 1);
                    if (stageState.isEnd()) {
                        if (!stageState.isExecuteEndAction()) {
                            stageState.setExecuteEndAction(true);
                            stageState.onEnd(room);
                            room.getStageStates().remove(stageState);
                        }
                    } else {
                        stageState.onTick(room);
                    }
                } catch (Throwable t) {
                    GameAPI.getGameDebugManager().printError(t);
                }
            }
        }
        if (!status.onTick(room)) {
            if (status.isTimeCounterEnabled(room)) {
                room.setTime(room.getTime() + 1);
            }
        }
        return true;
    }
}
