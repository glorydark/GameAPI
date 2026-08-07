package gameapi.tools.rotation;

import gameapi.tools.FaceableBlockRotationBehavior;

public class VineBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        int steps = normalizeDegree(rotationDegree) / 90;
        for (int s = 0; s < steps; s++) {
            damage = (damage << 1 | damage >> 3) & 0xf;
        }
        return damage;
    }
}
