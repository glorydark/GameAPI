package gameapi.tools.rotation;

import gameapi.tools.FaceableBlockRotationBehavior;

public class GlowLichenBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        int steps = normalizeDegree(rotationDegree) / 90;
        for (int s = 0; s < steps; s++) {
            int horiz = damage & 0x3C;
            int rotated = ((horiz << 1) | (horiz >> 3)) & 0x3C;
            damage = (damage & 0x03) | rotated;
        }
        return damage;
    }
}
