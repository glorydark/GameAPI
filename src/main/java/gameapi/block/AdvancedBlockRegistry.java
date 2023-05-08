package gameapi.block;

import cn.nukkit.block.Block;
import gameapi.annotation.Experimental;
import gameapi.event.RoomEvent;
import gameapi.event.block.RoomBlockEvent;
import gameapi.event.player.RoomPlayerInteractEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class AdvancedBlockRegistry {

    protected static HashMap<String, Class<?>> blockHashmap = new HashMap<>();

    public static void registerAdvancedBlock(int id, int meta, Class<?> blockClass){
        blockHashmap.put(id+":"+meta, blockClass);
    }

    public static void unregisterAdvancedBlock(int id, int meta){
        blockHashmap.remove(id+":"+meta);
    }

    @Experimental
    public static void triggerBlock(RoomPlayerInteractEvent roomPlayerInteractEvent) {
        if(roomPlayerInteractEvent.getBlock() == null){
            return;
        }
        trigger(roomPlayerInteractEvent.getBlock(), roomPlayerInteractEvent);
    }

    @Experimental
    public static void triggerBlock(RoomBlockEvent roomBlockEvent) {
        trigger(roomBlockEvent.getBlock(), roomBlockEvent);
    }

    public static void trigger(Block block, RoomEvent roomEvent){
        String idString = block.getId()+":"+block.getDamage();
        if(blockHashmap.containsKey(idString)){
            Class advancedBlockClass = blockHashmap.get(idString);
            try {
                for(Method method: advancedBlockClass.getMethods()) {
                    for (Class param : method.getParameterTypes()) {
                        if (roomEvent.getClass().isAssignableFrom(param)) {
                            method.invoke(advancedBlockClass.newInstance(), roomEvent);
                        }
                    }
                }
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
