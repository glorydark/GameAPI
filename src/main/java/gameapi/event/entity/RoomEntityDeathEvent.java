package gameapi.event.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import gameapi.listener.BaseEventListener;
import gameapi.room.Room;
import gameapi.utils.EntityDamageSource;
import gameapi.utils.PlayerDamageSource;

/**
 * @author glorydark
 */
public class RoomEntityDeathEvent extends RoomEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    private Item[] drops;

    private Entity lastDamageSourceFromEntity = null;

    private Player lastDamageSourceFromPlayer = null;

    public RoomEntityDeathEvent(Room room, EntityLiving entity) {
        this(room, entity, new Item[0]);
    }

    public RoomEntityDeathEvent(Room room, EntityLiving entity, Item[] drops) {
        super(room, entity);
        this.drops = drops;
        EntityDamageSource entityDamageSource = BaseEventListener.lastLivingEntityDamagedByEntitySources.get(entity.getId());
        if (entityDamageSource != null) {
            this.lastDamageSourceFromEntity = entityDamageSource.getDamager();
        }
        PlayerDamageSource playerDamageSource = BaseEventListener.lastLivingEntityDamagedByPlayerSources.get(entity.getId());
        if (playerDamageSource != null) {
            this.lastDamageSourceFromPlayer = playerDamageSource.getDamager();
        }
        BaseEventListener.lastLivingEntityDamagedByEntitySources.remove(entity.getId());
        BaseEventListener.lastLivingEntityDamagedByPlayerSources.remove(entity.getId());
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

    public Entity getLastDamageSourceFromEntity() {
        return lastDamageSourceFromEntity;
    }

    public Player getLastDamageSourceFromPlayer() {
        return lastDamageSourceFromPlayer;
    }
}
