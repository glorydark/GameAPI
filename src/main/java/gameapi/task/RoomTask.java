package gameapi.task;

import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import gameapi.MainClass;
import gameapi.event.*;
import gameapi.room.Room;

public class RoomTask extends Task {
    @Override
    public void onRun(int i) {
        //线程池 Demo
        MainClass.THREAD_POOL_EXECUTOR.execute(() -> {
            if (MainClass.RoomHashMap.size() >= 1) {
                for (Room room : MainClass.RoomHashMap) {
                    if (room == null) {
                        return;
                    }
                    switch (room.getRoomStatus()) {
                        case ROOM_STATUS_WAIT:
                            Server.getInstance().getPluginManager().callEvent(new RoomWaitListener(room));
                            break;
                        case ROOM_STATUS_GameEnd:
                            Server.getInstance().getPluginManager().callEvent(new RoomGameEndListener(room));
                            if(room.getPlayers().size() < 1){
                                room.reset();
                            }
                            break;
                        case ROOM_STATUS_Ceremony:
                            Server.getInstance().getPluginManager().callEvent(new RoomCeremonyListener(room));
                            if(room.getPlayers().size() < 1){
                                room.reset();
                            }
                            break;
                        case ROOM_STATUS_PreStart:
                            Server.getInstance().getPluginManager().callEvent(new RoomPreStartListener(room));
                            if(room.getPlayers().size() < 1){
                                room.reset();
                            }
                            break;
                        case ROOM_STATUS_GameStart:
                            Server.getInstance().getPluginManager().callEvent(new RoomGameProcessingListener(room));
                            if(room.getPlayers().size() < 1){
                                room.reset();
                            }
                            break;
                        case ROOM_STATUS_GameReadyStart:
                            Server.getInstance().getPluginManager().callEvent(new RoomReadyStartListener(room));
                            if(room.getPlayers().size() < 1){
                                room.reset();
                            }
                            break;
                        case ROOM_STATUS_NextRoundPreStart:
                            Server.getInstance().getPluginManager().callEvent(new RoomNextRoundPreStartListener(room));
                            if(room.getPlayers().size() < 1){
                                room.reset();
                            }
                            break;
                    }
                }
            }
        });
    }
}
