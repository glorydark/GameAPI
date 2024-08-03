package gameapi.utils;

import cn.nukkit.math.Vector3;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class Rotation {

    private double yaw;

    private double pitch;

    private double headYaw;

    public Rotation(double yaw, double pitch, double headYaw) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.headYaw = headYaw;
    }

    public static Rotation fromVector(Vector3 vector3) {
        return new Rotation(vector3.x, vector3.y, vector3.z);
    }
}
