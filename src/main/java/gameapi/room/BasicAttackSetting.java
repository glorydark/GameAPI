package gameapi.room;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class BasicAttackSetting {

    private float motionXZ;

    private float motionY;

    private float airMotionY;

    private float baseKnockBack;

    private int attackCoolDown;
}
