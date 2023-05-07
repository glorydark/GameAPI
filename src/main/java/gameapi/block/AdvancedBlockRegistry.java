package gameapi.block;

import gameapi.annotation.Experimental;
import gameapi.event.RoomEvent;
import gameapi.event.block.RoomBlockEvent;

import java.lang.reflect.InvocationTargetException;
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

    @Experimental
    public static void triggerBlock(RoomBlockEvent roomBlockEvent) {
        String idString = roomBlockEvent.getBlock().getId()+":"+roomBlockEvent.getBlock().getDamage();
        if(blockHashmap.containsKey(idString)){
            Class<AdvancedBlock> advancedBlockClass = blockHashmap.get(idString);
            try {
                Method executeMethod = advancedBlockClass.getMethod("trigger", RoomEvent.class);
                for(Class param : executeMethod.getParameterTypes()){
                    if(roomBlockEvent.getClass().isAssignableFrom(param)) {
                        executeMethod.invoke(advancedBlockClass, roomBlockEvent);
                    }
                }
            } catch (NoSuchMethodException ignored){

            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
