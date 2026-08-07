package gameapi.tools.rotation;

import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.tools.FaceableBlockRotationBehavior;

public class ShulkerBoxBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        return damage;
    }

    @Override
    public void modifyNbt(int blockId, CompoundTag tag, int rotationDegree) {
        if (tag != null && tag.contains("facing")) {
            int facing = tag.getByte("facing");
            int steps = normalizeDegree(rotationDegree) / 90;
            for (int s = 0; s < steps; s++) {
                facing = switch (facing) {
                    case 2 -> 5;
                    case 3 -> 4;
                    case 4 -> 2;
                    case 5 -> 3;
                    default -> facing;
                };
            }
            tag.putByte("facing", facing);
        }
    }
}
