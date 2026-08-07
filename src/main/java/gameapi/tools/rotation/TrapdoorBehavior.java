package gameapi.tools.rotation;

import gameapi.tools.FaceableBlockRotationBehavior;

public class TrapdoorBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        int dir = damage & 0x3;
        int steps = normalizeDegree(rotationDegree) / 90;
        for (int s = 0; s < steps; s++) {
            dir = switch (dir) {
                case 0 -> 2;
                case 1 -> 3;
                case 2 -> 1;
                case 3 -> 0;
                default -> dir;
            };
        }
        return (damage & ~0x3) | dir;
    }
}
