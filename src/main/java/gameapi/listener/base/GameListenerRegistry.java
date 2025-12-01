package gameapi.listener.base;

import cn.nukkit.event.EventPriority;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;
import gameapi.annotation.Experimental;
import gameapi.event.RoomEvent;
import gameapi.event.block.RoomBlockEvent;
import gameapi.event.player.RoomPlayerInteractEvent;
import gameapi.listener.base.annotations.GameEventHandler;
import gameapi.listener.base.interfaces.GameListener;
import gameapi.room.Room;
import it.unimi.dsi.fastutil.Function;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Experimental
public class GameListenerRegistry {

    public static String KEY_GLOBAL_LISTENER = "global";

    private static ConcurrentHashMap<String, List<RoomListener>> listeners = new ConcurrentHashMap<>();

    private static final Map<String, Map<Class<?>, List<Consumer<?>>>> newListeners = new LinkedHashMap<>();

    public static void clearAllRegisters() {
        listeners = new ConcurrentHashMap<>();
    }

    public static void registerGlobalEvents(GameListener listener, Plugin plugin) {
        registerEvents(KEY_GLOBAL_LISTENER, listener, plugin);
    }

    public static <T> void registerGlobalEvents(Class<T> eventType, Consumer<T> handler) {
        registerEvents(KEY_GLOBAL_LISTENER, eventType, handler);
    }

    public static <T> void registerEvents(String gameName, Class<T> eventType, Consumer<T> handler) {
        newListeners
                .computeIfAbsent(gameName, k -> new LinkedHashMap<>())
                .computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(handler);
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

            for (Method method : methods) {
                if (method.getParameterTypes().length == 0) {
                    continue;
                }
                GameEventHandler eh = method.getAnnotation(GameEventHandler.class);
                RoomListener evl;
                if (eh != null) {
                    evl = new RoomListener(gameName, listener, new MethodGameEventExecutor(method), eh.priority(), plugin, eh.ignoreCancelled());
                } else {
                    evl = new RoomListener(gameName, listener, new MethodGameEventExecutor(method), EventPriority.NORMAL, plugin, false);
                }
                listeners.computeIfAbsent(gameName, (Function<String, List<RoomListener>>) o -> new ArrayList<>()).add(evl);
            }
        }
    }

    public static void callEvent(Room room, RoomEvent event) {
        callNewEvent(room, event); // call new event
        callOldEvent(room, event);
    }

    public static void callOldEvent(Room room, RoomEvent event) {
        String gameName = room.getGameName();

        List<RoomListener> find = new ArrayList<>(listeners.getOrDefault(gameName, new ArrayList<>()));
        find.addAll(listeners.getOrDefault(KEY_GLOBAL_LISTENER, new ArrayList<>()));
        find = find.stream().sorted(Comparator.comparingInt(o -> o.getPriority().getSlot())).collect(Collectors.toList());
        if (!find.isEmpty()) {
            for (RoomListener listener : find) {
                listener.callEvent(gameName, event);
            }
        }
        if (event instanceof RoomBlockEvent) {
            room.getAdvancedBlockManager().triggerBlock((RoomBlockEvent) event);
        } else if (event instanceof RoomPlayerInteractEvent) {
            room.getAdvancedBlockManager().triggerBlock(((RoomPlayerInteractEvent) event));
        }
    }

    @SuppressWarnings("unchecked")
    protected static <T> void callNewEvent(Room room, T event) {
        Map<Class<?>, List<Consumer<?>>> gameListeners = newListeners.get(room.getGameName());
        if (gameListeners == null) return;

        List<Consumer<?>> handlers = gameListeners.getOrDefault(event.getClass(), new ArrayList<>());
        handlers.addAll(newListeners.getOrDefault(KEY_GLOBAL_LISTENER, new LinkedHashMap<>()).getOrDefault(event.getClass(), new ArrayList<>()));
        for (Consumer<?> handler : handlers) {
            if (handler != null) {
                ((Consumer<T>) handler).accept(event);
            }
        }
    }

    public static ConcurrentHashMap<String, List<RoomListener>> getListeners() {
        return listeners;
    }

    public static Map<String, Map<Class<?>, List<Consumer<?>>>> getNewListeners() {
        return newListeners;
    }

    public void unregisterAllEvents(String gameName) {
        listeners.put(gameName, new ArrayList<>());
    }
}
