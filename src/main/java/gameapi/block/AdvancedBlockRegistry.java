package gameapi.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.block.BlockEvent;

import java.lang.reflect.Method;
import java.util.HashMap;

public class AdvancedBlockRegistry {

    protected static HashMap<String, Class<AdvancedBlock>> blockHashmap = new HashMap<>();

    public static void registerAdvancedBlock(int id, int meta, Class<AdvancedBlock> blockClass){
        blockHashmap.put(id+":"+meta, blockClass);
    }

    public static void unregisterAdvancedBlock(int id, int meta){
        blockHashmap.remove(id+":"+meta);
    }

    public static void triggerBlock(Block block, BlockEvent blockEvent){
        String idString = block.getId()+":"+block.getDamage();
        if(blockHashmap.containsKey(idString)){
            Class<AdvancedBlock> advancedBlockClass = blockHashmap.get(idString);
            //to do
        }
    }

}
