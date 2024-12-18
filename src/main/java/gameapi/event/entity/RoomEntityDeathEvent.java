package gameapi.event.entity;

import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import gameapi.room.Room;
import gameapi.utils.EntityDamageSource;

import java.util.Optional;

/**
 * @author glorydark
 */
public class RoomEntityDeathEvent extends RoomEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    private Item[] drops;

    private EntityDamageSource lastDamageSourceFromEntity = null;

    private EntityDamageSource lastDamageSourceFromPlayer = null;

    public RoomEntityDeathEvent(Room room, EntityLiving entity) {
        this(room, entity, new Item[0]);
    }

    public RoomEntityDeathEvent(Room room, EntityLiving entity, Item[] drops) {
        super(room, entity);
        this.drops = drops;

        Optional<EntityDamageSource> source = room.getLastEntityDamageByEntitySource(entity);
        source.ifPresent(entityDamageSource -> this.lastDamageSourceFromEntity = entityDamageSource);

        Optional<EntityDamageSource> pSource = room.getLastEntityDamageByPlayerSource(entity);
        pSource.ifPresent(playerDamageSource -> this.lastDamageSourceFromPlayer = playerDamageSource);

        room.getLastEntityReceiveDamageSource().remove(entity);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Item[] getDrops() {
        return this.drops;
    }

    public void setDrops(Item[] drops) {
        if (drops == null) {
            drops = new Item[0];
        }

        this.drops = drops;
    }

    public Optional<EntityDamageSource> getLastDamageSourceFromEntity() {
        return Optional.ofNullable(lastDamageSourceFromEntity);
    }

    public Optional<EntityDamageSource> getLastDamageSourceFromPlayer() {
        return Optional.ofNullable(lastDamageSourceFromPlayer);
    }
}
