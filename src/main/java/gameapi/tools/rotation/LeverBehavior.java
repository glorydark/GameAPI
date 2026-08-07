package gameapi.tools.rotation;

import gameapi.tools.FaceableBlockRotationBehavior;

public class LeverBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        int steps = normalizeDegree(rotationDegree) / 90;
        int thrown = damage & 0x8;
        int dir = damage & 0x7;
        for (int s = 0; s < steps; s++) {
            dir = switch (dir) {
                case 1 -> 3;
                case 2 -> 4;
                case 3 -> 2;
                case 4 -> 1;
                case 5 -> 6;
                case 6 -> 5;
                case 0 -> 7;
                case 7 -> 0;
                default -> dir;
            };
        }
        return dir | thrown;
    }
}
