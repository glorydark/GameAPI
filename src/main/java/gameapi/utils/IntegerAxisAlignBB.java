package gameapi.utils;

import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author glorydark
 */
public class IntegerAxisAlignBB {

    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;

    public IntegerAxisAlignBB(Vector3 pos1, Vector3 pos2) {
        this.minX = Math.min(pos1.getFloorX(), pos2.getFloorX());
        this.minY = Math.min(pos1.getFloorY(), pos2.getFloorY());
        this.minZ = Math.min(pos1.getFloorZ(), pos2.getFloorZ());
        this.maxX = Math.max(pos1.getFloorX(), pos2.getFloorX());
        this.maxY = Math.max(pos1.getFloorY(), pos2.getFloorY());
        this.maxZ = Math.max(pos1.getFloorZ(), pos2.getFloorZ());
    }

    public IntegerAxisAlignBB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public static void main(String[] args) {
        AtomicLong count = new AtomicLong();
        IntegerAxisAlignBB bb = new IntegerAxisAlignBB(0, 0, 0, 640, 640, 640);
        bb.forEach((var1, var2, var3) -> count.addAndGet(1));

        AtomicLong count1 = new AtomicLong();
        for (IntegerAxisAlignBB bb1 : bb.splitAABB(32, 32, 32)) {
            bb1.forEach(((var1, var2, var3) -> count1.addAndGet(1)));
        }

        System.out.println(count1 + "/" + count);
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    public void forEach(BBConsumer action) {
        int minX = this.getMinX();
        int minY = this.getMinY();
        int minZ = this.getMinZ();
        int maxX = this.getMaxX();
        int maxY = this.getMaxY();
        int maxZ = this.getMaxZ();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    action.accept(x, y, z);
                }
            }
        }
    }

    public long getSize() {
        return ((long) (this.getMaxX() - this.getMinX() + 1)) * (this.getMaxY() - this.getMinY() + 1) * (this.getMaxZ() - this.getMinZ() + 1);
    }

    @Override
    public String toString() {
        return "{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", minZ=" + minZ +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ", maxZ=" + maxZ +
                '}';
    }

    public IntegerAxisAlignBB[] splitAABB(int xLength, int yLength, int zLength) {
        List<IntegerAxisAlignBB> bbs = new ArrayList<>();
        int minX = this.getMinX();
        int minY = this.getMinY();
        int minZ = this.getMinZ();
        int maxX = this.getMaxX();
        int maxY = this.getMaxY();
        int maxZ = this.getMaxZ();

        for (int x = minX; x <= maxX + xLength; x += xLength) {
            for (int y = minY; y <= maxY + yLength; y += yLength) {
                for (int z = minZ; z <= maxZ + zLength; z += zLength) {
                    int subMaxX = Math.min(x + xLength - 1, maxX);
                    int subMaxY = Math.min(y + yLength - 1, maxY);
                    int subMaxZ = Math.min(z + zLength - 1, maxZ);
                    IntegerAxisAlignBB subAABB = new IntegerAxisAlignBB(x, y, z, subMaxX, subMaxY, subMaxZ);
                    bbs.add(subAABB);
                }
            }
        }

        // Handle any last blocks if they are smaller than the specified lengths
        if (bbs.isEmpty()) {
            // In case there are no AABBs generated, create a single one covering the entire range
            bbs.add(new IntegerAxisAlignBB(minX, minY, minZ, maxX, maxY, maxZ));
        }

        return bbs.toArray(new IntegerAxisAlignBB[0]);
    }

    public interface BBConsumer<T> {
        void accept(int var1, int var2, int var3);

        default T get() {
            return null;
        }
    }
}