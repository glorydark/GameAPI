package gameapi.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityVehicle;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;
import gameapi.room.Room;

/**
 * @author glorydark
 * @date {2024/1/5} {23:42}
 */
public class RoomEntitySpawnEvent extends RoomEntityEvent {
    private static final HandlerList handlers = new HandlerList();
    private final int entityType;

    public RoomEntitySpawnEvent(Room room, Entity entity) {
        super(room, entity);
        this.entityType = entity.getNetworkId();
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Position getPosition() {
        return this.entity.getPosition();
    }

    public int getType() {
        return this.entityType;
    }

    public boolean isCreature() {
        return this.entity instanceof EntityCreature;
    }

    public boolean isHuman() {
        return this.entity instanceof EntityHuman;
    }

    public boolean isProjectile() {
        return this.entity instanceof EntityProjectile;
    }

    public boolean isVehicle() {
        return this.entity instanceof EntityVehicle;
    }

    public boolean isItem() {
        return this.entity instanceof EntityItem;
    }
}
