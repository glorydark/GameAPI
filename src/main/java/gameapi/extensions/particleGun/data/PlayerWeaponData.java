package gameapi.extensions.particleGun.data;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class PlayerWeaponData {

    private int ammo;

    private int maxAmmo;

    private long lastUsedMillis;

}
