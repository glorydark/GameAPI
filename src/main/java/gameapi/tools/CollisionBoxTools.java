package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;

/**
 * @author glorydark
 */
public class CollisionBoxTools {

    /**
     * 判断点是否在旋转后的AABB内（高效准确的方法）
     * @param box 原始AABB
     * @param yawDegrees 旋转角度（0-360度）
     * @param point 检测点
     * @return true表示点在旋转后的AABB内
     */
    public static boolean intersectsPoint(AxisAlignedBB box, double yawDegrees, Vector3 point) {
        // 计算AABB中心
        double centerX = (box.getMinX() + box.getMaxX()) / 2.0;
        double centerY = (box.getMinY() + box.getMaxY()) / 2.0;
        double centerZ = (box.getMinZ() + box.getMaxZ()) / 2.0;

        // 计算AABB的半尺寸
        double halfX = (box.getMaxX() - box.getMinX()) / 2.0;
        double halfY = (box.getMaxY() - box.getMinY()) / 2.0;
        double halfZ = (box.getMaxZ() - box.getMinZ()) / 2.0;

        // 将点转换到局部坐标系（相对于AABB中心）
        double dx = point.x - centerX;
        double dz = point.z - centerZ;
        double dy = point.y - centerY;

        // 逆旋转点（将点旋转回AABB的局部坐标系）
        double rad = Math.toRadians(-yawDegrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double localX = dx * cos - dz * sin;
        double localZ = dx * sin + dz * cos;

        // 判断点是否在原始AABB内
        return Math.abs(localX) <= halfX
                && Math.abs(dy) <= halfY
                && Math.abs(localZ) <= halfZ;
    }

    // 精确边界检查版本
    public static boolean intersectsPointExact(AxisAlignedBB box, double yawDegrees, Vector3 point) {
        double centerX = (box.getMinX() + box.getMaxX()) / 2.0;
        double centerY = (box.getMinY() + box.getMaxY()) / 2.0;
        double centerZ = (box.getMinZ() + box.getMaxZ()) / 2.0;

        double halfX = (box.getMaxX() - box.getMinX()) / 2.0;
        double halfY = (box.getMaxY() - box.getMinY()) / 2.0;
        double halfZ = (box.getMaxZ() - box.getMinZ()) / 2.0;

        double dx = point.x - centerX;
        double dz = point.z - centerZ;
        double dy = point.y - centerY;

        double rad = Math.toRadians(-yawDegrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double localX = dx * cos - dz * sin;
        double localZ = dx * sin + dz * cos;

        // 精确边界检查（考虑浮点精度）
        return localX >= -halfX - 1e-6 && localX <= halfX + 1e-6
                && dy >= -halfY - 1e-6 && dy <= halfY + 1e-6
                && localZ >= -halfZ - 1e-6 && localZ <= halfZ + 1e-6;
    }

    public static SimpleAxisAlignedBB getVertical(Entity e) {
        float width = 0.58F;
        float height = getHeight(e);

        // 局部坐标 min/max，脚底为原点
        return new SimpleAxisAlignedBB(
                new Vector3(-width / 2, 0, -width / 2).add(e),
                new Vector3(width / 2, height, width / 2).add(e)
        );
    }

    public static SimpleAxisAlignedBB getHorizontal(Entity e) {
        // float width = 0.58F;
        float height = getHeight(e);

        // 手臂横向盒子，可根据实际手臂位置调整
        // 假设手臂水平，略高于脚底的中心位置
        double armYMin = height * 0.3;
        double armYMax = height * 0.75;

        return new SimpleAxisAlignedBB(
                new Vector3(-0.6, armYMin, -0.2).add(e),
                new Vector3(0.6, armYMax, 0.2).add(e)
        );
    }

    private static float getHeight(Entity e) {
        if (!e.isSwimming() && !e.isGliding()) {
            if (e.isShortSneaking()) {
                if (e instanceof Player player && player.protocol < 589) {
                    return 1.65F;
                }
                return 1.49F;
            } else {
                return e.isCrawling() ? 0.625F : 2F;
            }
        } else {
            return 0.6F;
        }
    }
}
