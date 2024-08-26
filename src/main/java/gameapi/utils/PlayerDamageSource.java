package gameapi.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import lombok.Data;
import lombok.ToString;

/**
 * @author glorydark
 */
@Data
@ToString
public class PlayerDamageSource {

    private Player damager;
    private long milliseconds;

    public PlayerDamageSource(Player damager, long milliseconds) {
        this.damager = damager;
        this.milliseconds = milliseconds;
    }
}