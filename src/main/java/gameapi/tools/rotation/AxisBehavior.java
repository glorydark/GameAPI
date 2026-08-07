package gameapi.tools.rotation;

import gameapi.tools.FaceableBlockRotationBehavior;

public class AxisBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        int steps = normalizeDegree(rotationDegree) / 90;
        for (int s = 0; s < steps; s++) {
            if (damage >= 4 && damage <= 11) {
                damage ^= 0xc;
            }
        }
        return damage;
    }
}
