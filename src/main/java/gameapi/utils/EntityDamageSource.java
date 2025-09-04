package gameapi.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

/**
 * @author glorydark
 */
@Data
@ToString
public class EntityDamageSource {

    private Entity damager;
    private Item item;
    private float finalDamage;
    private float damage;
    private long milliseconds;
    private EntityDamageEvent sourceEvent;

    public EntityDamageSource(Entity damager, Item item, float damage, float finalDamage, long milliseconds, EntityDamageEvent sourceEvent) {
        this.damager = damager;
        this.item = item;
        this.damage = damage;
        this.finalDamage = finalDamage;
        this.milliseconds = milliseconds;
        this.sourceEvent = sourceEvent;
    }

    @Nullable
    public Player asPlayer() {
        if (this.damager.isPlayer) {
            return (Player) this.damager;
        } else {
            return null;
        }
    }
}