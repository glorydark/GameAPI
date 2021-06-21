package gameapi.task;

import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import gameapi.MainClass;
import gameapi.event.*;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

public class RoomTask extends Task {
    @Override
    public void onRun(int i) {
        if (MainClass.RoomHashMap.size() >= 1) {
            for (Room room: MainClass.RoomHashMap) {
                if(room == null){return; }
                switch (room.roomStatus){
                    case ROOM_STATUS_WAIT:
                        Server.getInstance().getPluginManager().callEvent(new RoomWaitListener(room));
                        break;
                    case ROOM_STATUS_GameEnd:
                        Server.getInstance().getPluginManager().callEvent(new RoomGameEndListener(room));
                        break;
                    case ROOM_STATUS_Ceremony:
                        Server.getInstance().getPluginManager().callEvent(new RoomCeremonyListener(room));
                        break;
                    case ROOM_STATUS_PreStart:
                        Server.getInstance().getPluginManager().callEvent(new RoomPreStartListener(room));
                        break;
                    case ROOM_STATUS_GameStart:
                        Server.getInstance().getPluginManager().callEvent(new RoomGameProcessingListener(room));
                        break;
                    case ROOM_STATUS_GameReadyStart:
                        Server.getInstance().getPluginManager().callEvent(new RoomReadyStartListener(room));
                        break;
                    case ROOM_STATUS_NextRoundPreStart:
                        Server.getInstance().getPluginManager().callEvent(new RoomNextRoundPreStartListener(room));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
