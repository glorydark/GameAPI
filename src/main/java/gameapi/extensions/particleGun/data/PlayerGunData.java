package gameapi.extensions.particleGun.data;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class PlayerGunData {

    private int ammo;
    private int maxAmmo;
}
