package gameapi.manager.room;

import cn.nukkit.block.Block;
import gameapi.annotation.Experimental;
import gameapi.event.block.RoomBlockEvent;
import gameapi.event.player.RoomPlayerInteractEvent;
import gameapi.extensions.block.AbstractAdvancedBlock;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdvancedBlockManager {

    protected Map<String, AbstractAdvancedBlock> blockHashmap;

    public AdvancedBlockManager() {
        this.blockHashmap = new LinkedHashMap<>();
    }

    public void registerAdvancedBlock(int id, int meta, AbstractAdvancedBlock advancedBlock) {
        this.blockHashmap.put(id + ":" + meta, advancedBlock);
    }

    public void unregisterAdvancedBlock(int id, int meta) {
        this.blockHashmap.remove(id + ":" + meta);
    }

    public void triggerBlock(RoomPlayerInteractEvent event) {
        if (event.getBlock() == null) {
            return;
        }
        Block block = event.getBlock();
        String idString = block.getId() + ":" + block.getDamage();
        //String pnxIdString = block.toItem().getNamespaceId();
        if (this.blockHashmap.containsKey(idString)) {
            AbstractAdvancedBlock advancedBlockClass = this.blockHashmap.get(idString);
            advancedBlockClass.onRespond(event);
        }
    }

    public void triggerBlock(RoomBlockEvent event) {
        Block block = event.getBlock();
        String idString = block.getId() + ":" + block.getDamage();
        //String pnxIdString = block.toItem().getNamespaceId();
        if (this.blockHashmap.containsKey(idString)) {
            AbstractAdvancedBlock advancedBlockClass = this.blockHashmap.get(idString);
            advancedBlockClass.onRespond(event);
        }
    }
}
