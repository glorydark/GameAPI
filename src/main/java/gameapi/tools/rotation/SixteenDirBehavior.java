package gameapi.tools.rotation;

import gameapi.tools.FaceableBlockRotationBehavior;

public class SixteenDirBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        int steps = normalizeDegree(rotationDegree) / 90;
        return (damage + steps * 4) & 0xF;
    }
}
