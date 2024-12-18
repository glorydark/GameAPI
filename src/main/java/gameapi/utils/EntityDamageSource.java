package gameapi.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import lombok.Data;
import lombok.ToString;

import javax.annotation.Nullable;

/**
 * @author glorydark
 */
@Data
@ToString
public class EntityDamageSource {

    private Entity damager;
    private float finalDamage;
    private float damage;
    private long milliseconds;

    public EntityDamageSource(Entity damager, float damage, float finalDamage, long milliseconds) {
        this.damager = damager;
        this.damage = damage;
        this.finalDamage = finalDamage;
        this.milliseconds = milliseconds;
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