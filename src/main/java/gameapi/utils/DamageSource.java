package gameapi.utils;

import lombok.Data;
import lombok.ToString;

/**
 * @author glorydark
 */
@Data
@ToString
public class DamageSource {
    private String damager;
    private long milliseconds;

    public DamageSource(String damager, long milliseconds) {
        this.damager = damager;
        this.milliseconds = milliseconds;
    }
}