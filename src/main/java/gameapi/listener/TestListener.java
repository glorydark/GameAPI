package gameapi.listener;

import gameapi.GameAPI;
import gameapi.event.player.RoomPlayerJoinEvent;
import gameapi.listener.base.annotations.GameListener;

public class TestListener implements GameListener {

    public void test(RoomPlayerJoinEvent event){
        GameAPI.plugin.getLogger().warning("test");
    }

}
