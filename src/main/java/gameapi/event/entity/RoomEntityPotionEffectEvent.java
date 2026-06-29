package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.entity.EntityPotionEffectEvent;
import cn.nukkit.potion.Effect;
import gameapi.room.Room;
import org.jetbrains.annotations.Nullable;

/**
 * @author glorydark
 */
public class RoomEntityPotionEffectEvent extends RoomEntityEvent implements Cancellable {

    @Nullable
    private final Effect oldEffect;
    @Nullable
    private final Effect newEffect;
    private final EntityPotionEffectEvent.Action action;
    private final EntityPotionEffectEvent.Cause cause;

    public RoomEntityPotionEffectEvent(Room room, Entity entity, @Nullable Effect oldEffect, @Nullable Effect newEffect, EntityPotionEffectEvent.Action action, EntityPotionEffectEvent.Cause cause) {
        super(room, entity);
        this.oldEffect = oldEffect;
        this.newEffect = newEffect;
        this.action = action;
        this.cause = cause;
    }

    @Nullable
    public Effect getOldEffect() {
        return this.oldEffect;
    }

    @Nullable
    public Effect getNewEffect() {
        return this.newEffect;
    }

    public EntityPotionEffectEvent.Action getAction() {
        return this.action;
    }

    public EntityPotionEffectEvent.Cause getCause() {
        return this.cause;
    }
}
