package gameapi.tools.rotation;

import gameapi.tools.FaceableBlockRotationBehavior;

public class Simple4DirBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        int dir = damage & 0x3;
        int steps = normalizeDegree(rotationDegree) / 90;
        dir = (dir + steps) & 0x3;
        return (damage & ~0x3) | dir;
    }
}
