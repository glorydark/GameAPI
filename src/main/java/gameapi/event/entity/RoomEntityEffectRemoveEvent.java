package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.potion.Effect;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomEntityEffectRemoveEvent extends RoomEntityEvent implements Cancellable {

    private final Effect removeEffect;

    public RoomEntityEffectRemoveEvent(Room room, Entity entity, Effect removeEffect) {
        super(room, entity);
        this.removeEffect = removeEffect;
    }

    public Effect getRemoveEffect() {
        return removeEffect;
    }
}
