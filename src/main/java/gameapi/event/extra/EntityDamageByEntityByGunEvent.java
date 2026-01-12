package gameapi.event.extra;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;

/**
 * @author glorydark
 */
public class EntityDamageByEntityByGunEvent extends EntityDamageByEntityByItemEvent {

    public final AttackPos attackPos;

    public EntityDamageByEntityByGunEvent(Entity damager, Entity entity, DamageCause cause, float damage, Item item, AttackPos attackPos) {
        super(damager, entity, cause, damage, item);
        this.attackPos = attackPos;
    }

    public enum AttackPos {
        HEAD,
        CHEST,
        LEG,
        ARM
    }

    public AttackPos getAttackPos() {
        return attackPos;
    }
}
