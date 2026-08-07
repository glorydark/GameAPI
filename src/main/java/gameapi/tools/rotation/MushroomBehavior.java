package gameapi.tools.rotation;

import gameapi.tools.FaceableBlockRotationBehavior;

public class MushroomBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        if (damage >= 10) return damage;
        int steps = normalizeDegree(rotationDegree) / 90;
        for (int s = 0; s < steps; s++) {
            damage = damage * 3 % 10;
        }
        return damage;
    }
}
