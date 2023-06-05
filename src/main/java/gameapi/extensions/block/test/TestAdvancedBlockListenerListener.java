package gameapi.extensions.block.test;

import cn.nukkit.math.Vector3;
import gameapi.extensions.block.AdvancedBlockListener;
import gameapi.event.block.RoomBlockPlaceEvent;
import gameapi.event.block.RoomBlockTreadEvent;
import gameapi.listener.base.annotations.GameEventHandler;

public class TestAdvancedBlockListenerListener implements AdvancedBlockListener {

    @GameEventHandler
    public void RoomPlayerMoveEvent(RoomBlockTreadEvent event) {
        event.getPlayer().sendMessage("rnm,退钱！");
        Vector3 motion = event.getPlayer().getMotion();
        event.getPlayer().setMotion(motion.multiply(3.0));
    }

    @GameEventHandler
    public void RoomBlockPlaceEvent(RoomBlockPlaceEvent event){
        event.getPlayer().sendMessage("tmd，别烦我");
    }

}
