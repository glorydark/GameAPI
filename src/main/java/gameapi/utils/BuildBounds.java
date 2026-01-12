package gameapi.utils;

import cn.nukkit.math.Vector3;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class BuildBounds {

    private final int x;
    private final int y;
    private final int z;

    public BuildBounds(Vector3 start, Vector3 end) {
        IntegerAxisAlignBB bb = new IntegerAxisAlignBB(start, end);
        this.x = bb.getMaxX() - bb.getMinX();
        this.y = bb.getMaxY() - bb.getMinY();
        this.z = bb.getMaxZ() - bb.getMinZ();
    }

    public BuildBounds(Vector3 sizeBox) {
        this.x = sizeBox.getFloorX();
        this.y = sizeBox.getFloorY();
        this.z = sizeBox.getFloorZ();
    }

    public BuildBounds(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 getBounds() {
        return this.getBounds(0, 0, 0);
    }

    public Vector3 getShapeBounds() {
        return this.getBounds(1, 0, 1);
    }

    public Vector3 getBounds(double offsetX, double offsetY, double offsetZ) {
        return new Vector3(x + offsetX, y + offsetY, z + offsetZ);
    }

    /**
     * 世界空间的几何中心（用于 DebugShape / 旋转锚点）
     */
    public Vector3 getCenter(Vector3 startPos) {
        return startPos.add(this.getBounds().multiply(0.5));
    }

    public Vector3 getShapeCenter(Vector3 startPos) {
        return startPos.add(this.getShapeBounds().multiply(0.5));
    }

    /**
     * 世界空间底部中心（地面锚点）
     */
    public Vector3 getBottomCenter(Vector3 startPos) {
        return this.getCenter(startPos).clone().setY(startPos.getY());
    }

    public Vector3 getShapeBottomCenter(Vector3 startPos) {
        return this.getShapeCenter(startPos).clone().setY(startPos.getY());
    }

    public static int normalizeRotation(int deg) {
        deg = deg % 360;
        if (deg < 0) deg += 360;
        return deg;
    }

    /**
     * 把一个 min-based 局部方块坐标(rx,ry,rz)
     * 转换为「绕建筑中心旋转后的世界坐标」
     *
     * @param startPos   建筑最小点(minX,minY,minZ)的世界坐标
     * @param rx         方块相对 minX 的 X
     * @param ry         方块相对 minY 的 Y
     * @param rz         方块相对 minZ 的 Z
     * @param rotation  0 / 90 / 180 / 270
     */
    public Vector3 getBlockPosAfterHorizontalRotatedByCenter(Vector3 startPos, int rx, int ry, int rz, int rotation) {

        rotation = normalizeRotation(rotation);

        // 建筑尺寸（格子尺寸）
        double sizeX = x + 1;
        double sizeY = y + 1;
        double sizeZ = z + 1;

        // 几何中心（在 min-based 坐标系内）
        double cx = (sizeX - 1) / 2.0;
        double cy = (sizeY - 1) / 2.0;
        double cz = (sizeZ - 1) / 2.0;

        // 把方块移到“以中心为原点”的坐标系
        double lx = rx - cx;
        double lz = rz - cz;

        // 围绕中心旋转 (XZ)
        double nx = lx;
        double nz = lz;

        switch (rotation) {
            case 90 -> {
                nx = -lz;
                nz =  lx;
            }
            case 180 -> {
                nx = -lx;
                nz = -lz;
            }
            case 270 -> {
                nx =  lz;
                nz = -lx;
            }
        }

        // 移回 min-based 坐标系
        double fx = nx + cx;
        double fz = nz + cz;

        // 转成世界坐标
        return new Vector3(
                startPos.getFloorX() + Math.round(fx),
                startPos.getFloorY() + ry,
                startPos.getFloorZ() + Math.round(fz)
        );
    }
}
