package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.potion.Effect;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomEntityEffectUpdateEvent extends RoomEntityEvent implements Cancellable {

    private final Effect oldEffect;
    private final Effect newEffect;

    public RoomEntityEffectUpdateEvent(Room room, Entity entity, Effect oldEffect, Effect newEffect) {
        super(room, entity);
        this.oldEffect = oldEffect;
        this.newEffect = newEffect;
    }

    public Effect getOldEffect() {
        return this.oldEffect;
    }

    public Effect getNewEffect() {
        return this.newEffect;
    }
}
