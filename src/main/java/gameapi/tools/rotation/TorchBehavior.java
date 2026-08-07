package gameapi.tools.rotation;

import gameapi.tools.FaceableBlockRotationBehavior;

public class TorchBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        int steps = normalizeDegree(rotationDegree) / 90;
        for (int s = 0; s < steps; s++) {
            damage = switch (damage) {
                case 1 -> 3;
                case 2 -> 4;
                case 3 -> 2;
                case 4 -> 1;
                default -> damage;
            };
        }
        return damage;
    }
}
