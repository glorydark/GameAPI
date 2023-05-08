package gameapi.listener.base;

import cn.nukkit.event.EventPriority;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;
import gameapi.annotation.Future;
import gameapi.block.AdvancedBlockRegistry;
import gameapi.event.RoomEvent;
import gameapi.event.block.RoomBlockEvent;
import gameapi.event.player.RoomPlayerInteractEvent;
import gameapi.listener.base.annotations.GameEventHandler;
import gameapi.listener.base.interfaces.GameListener;
import gameapi.room.Room;

import java.lang.reflect.Method;
import java.util.*;

@Future
public class GameListenerRegistry {
    private static HashMap<String, List<RoomListener>> listeners = new HashMap<>();

    public void unregisterAllEvents(String gameName){
        listeners.put(gameName, new ArrayList<>());
    }

    public static void clearAllRegisters(){
        listeners = new HashMap<>();
    }

    public static void registerEvents(String gameName, GameListener listener, Plugin plugin) {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin attempted to register " + listener.getClass().getName() + " while not enabled");
        } else {
            HashSet<Method> methods;
            try {
                Method[] publicMethods = listener.getClass().getMethods();
                Method[] privateMethods = listener.getClass().getDeclaredMethods();
                methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0F);
                Collections.addAll(methods, publicMethods);
                Collections.addAll(methods, privateMethods);
            } catch (NoClassDefFoundError var11) {
                plugin.getLogger().error("Plugin " + plugin.getDescription().getFullName() + " has failed to register game events for " + listener.getClass() + " because " + var11.getMessage() + " does not exist.");
                return;
            }

            for(Method method: methods) {
                GameEventHandler eh = method.getAnnotation(GameEventHandler.class);
                RoomListener evl;
                if(eh != null){
                    evl = new RoomListener(gameName, listener, new MethodGameEventExecutor(method), eh.priority(), plugin, eh.ignoreCancelled());
                }else{
                    evl = new RoomListener(gameName, listener, new MethodGameEventExecutor(method), EventPriority.NORMAL, plugin, false);
                }
                if(listeners.containsKey(gameName)){
                    listeners.get(gameName).add(evl);
                }else {
                    listeners.put(gameName, new ArrayList<>());
                    listeners.get(gameName).add(evl);
                }
            }
        }
    }

    public static void callEvent(Room room, RoomEvent event) {
        String gameName = room.getGameName();
        List<RoomListener> find = new ArrayList<>(listeners.getOrDefault(gameName, new ArrayList<>()));
        for(RoomListener listener: find){
            listener.callEvent(gameName, event);
        }
        if(event instanceof RoomBlockEvent){
            AdvancedBlockRegistry.trigger(((RoomBlockEvent) event).getBlock(), event);
        }else if(event instanceof RoomPlayerInteractEvent){
            AdvancedBlockRegistry.trigger(((RoomPlayerInteractEvent) event).getBlock(), event);
        }
    }
}
