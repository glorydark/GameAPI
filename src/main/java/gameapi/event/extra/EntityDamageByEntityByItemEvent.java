package gameapi.event.extra;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;

/**
 * @author glorydark
 */
public class EntityDamageByEntityByItemEvent extends EntityDamageByEntityEvent {

    private final Item item;

    public EntityDamageByEntityByItemEvent(Entity damager, Entity entity, DamageCause cause, float damage, Item item) {
        super(damager, entity, cause, damage);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
