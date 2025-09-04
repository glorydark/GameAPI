package gameapi.listener.mot;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityEffectRemoveEvent;
import cn.nukkit.event.entity.EntityEffectUpdateEvent;
import gameapi.event.entity.RoomEntityEffectRemoveEvent;
import gameapi.event.entity.RoomEntityEffectUpdateEvent;
import gameapi.listener.base.GameListenerRegistry;
import gameapi.manager.RoomManager;
import gameapi.room.Room;

/**
 * @author Glorydark
 */
public class BaseEventListenerMOTPatch implements Listener {

    @EventHandler
    public void EntityEffectUpdateEvent(EntityEffectUpdateEvent event) {
        for (Room room : RoomManager.getRooms(event.getEntity().getLevel())) {
            RoomEntityEffectUpdateEvent roomEntityEffectUpdateEvent = new RoomEntityEffectUpdateEvent(room, event.getEntity(), event.getOldEffect(), event.getNewEffect());
            GameListenerRegistry.callEvent(room, roomEntityEffectUpdateEvent);
            if (roomEntityEffectUpdateEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void EntityEffectRemoveEvent(EntityEffectRemoveEvent event) {
        for (Room room : RoomManager.getRooms(event.getEntity().getLevel())) {
            RoomEntityEffectRemoveEvent roomEntityEffectRemoveEvent = new RoomEntityEffectRemoveEvent(room, event.getEntity(), event.getRemoveEffect());
            GameListenerRegistry.callEvent(room, roomEntityEffectRemoveEvent);
            if (roomEntityEffectRemoveEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }
}