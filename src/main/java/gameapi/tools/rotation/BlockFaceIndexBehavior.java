package gameapi.tools.rotation;

import gameapi.tools.FaceableBlockRotationBehavior;

public class BlockFaceIndexBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        int steps = normalizeDegree(rotationDegree) / 90;
        int dir = damage & 0x7;
        for (int s = 0; s < steps; s++) {
            dir = switch (dir) {
                case 2 -> 5;
                case 3 -> 4;
                case 4 -> 2;
                case 5 -> 3;
                default -> dir;
            };
        }
        return (damage & ~0x7) | dir;
    }
}
