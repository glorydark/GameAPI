package gameapi.tools.rotation;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWall;
import cn.nukkit.math.BlockFace;
import gameapi.tools.FaceableBlockRotationBehavior;

public class WallBehavior extends FaceableBlockRotationBehavior {
    @Override
    public int rotateDamage(int blockId, int damage, int rotationDegree) {
        int steps = normalizeDegree(rotationDegree) / 90;
        if (steps == 0) {
            return damage;
        }
        Block block = Block.get(blockId, damage);
        if (!(block instanceof BlockWall wall)) {
            return damage;
        }
        BlockWall.WallConnectionType east = wall.getConnectionType(BlockFace.EAST);
        BlockWall.WallConnectionType north = wall.getConnectionType(BlockFace.NORTH);
        BlockWall.WallConnectionType south = wall.getConnectionType(BlockFace.SOUTH);
        BlockWall.WallConnectionType west = wall.getConnectionType(BlockFace.WEST);
        for (int step = 0; step < steps; step++) {
            BlockWall.WallConnectionType newEast = north;
            BlockWall.WallConnectionType newSouth = east;
            BlockWall.WallConnectionType newWest = south;
            BlockWall.WallConnectionType newNorth = west;
            east = newEast;
            south = newSouth;
            west = newWest;
            north = newNorth;
        }
        wall.setConnection(BlockFace.EAST, east);
        wall.setConnection(BlockFace.NORTH, north);
        wall.setConnection(BlockFace.SOUTH, south);
        wall.setConnection(BlockFace.WEST, west);
        return wall.getDamage();
    }
}
