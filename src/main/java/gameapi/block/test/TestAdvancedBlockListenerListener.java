package gameapi.block.test;

import gameapi.block.AdvancedBlockListener;
import gameapi.event.block.RoomBlockPlaceEvent;
import gameapi.event.block.RoomBlockTreadEvent;
import gameapi.listener.base.annotations.GameEventHandler;

public class TestAdvancedBlockListenerListener implements AdvancedBlockListener {

    @GameEventHandler
    public void RoomPlayerMoveEvent(RoomBlockTreadEvent event) {
        event.getPlayer().sendMessage("rnm,退钱！");
    }

    @GameEventHandler
    public void RoomBlockPlaceEvent(RoomBlockPlaceEvent event){
        event.getPlayer().sendMessage("tmd，别烦我");
    }

}
