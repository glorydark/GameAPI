package gameapi.tools.rotation;

import gameapi.tools.FaceableBlockRotationBehavior;

public class RailBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        int steps = normalizeDegree(rotationDegree) / 90;
        for (int s = 0; s < steps; s++) {
            damage = switch (damage) {
                case 6 -> 7;
                case 7 -> 8;
                case 8 -> 9;
                case 9 -> 6;
                default -> damage;
            };
        }
        return damage;
    }
}
