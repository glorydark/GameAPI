package gameapi.utils;

import cn.nukkit.entity.Entity;
import lombok.Data;
import lombok.ToString;

/**
 * @author glorydark
 */
@Data
@ToString
public class EntityDamageSource {

    private Entity damager;
    private long milliseconds;

    public EntityDamageSource(Entity damager, long milliseconds) {
        this.damager = damager;
        this.milliseconds = milliseconds;
    }
}