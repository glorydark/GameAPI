package gameapi.tools;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import gameapi.GameAPI;
import gameapi.utils.AdvancedLocation;
import gameapi.utils.IntegerAxisAlignBB;
import gameapi.utils.Rotation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public class SpatialTools {

    public static SimpleAxisAlignedBB toAxisAlignedBB(int x1, int y1, int z1, int x2, int y2, int z2) {
        return new SimpleAxisAlignedBB(new Vector3(x1, y1, z1), new Vector3(x2, y2, z2));
    }

    public static List<Rotation> parseRotationFromStrings(List<String> str) {
        return parseRotationFromStrings(str.toArray(new String[0]));
    }

    public static List<Rotation> parseRotationFromStrings(String... str) {
        List<Rotation> vector3List = new ArrayList<>();
        for (String s : str) {
            vector3List.add(parseRotationFromString(s));
        }
        return vector3List;
    }

    public static Rotation parseRotationFromString(String str) {
        String[] locArray = str.split(":");
        if (locArray.length == 3) {
            return new Rotation(Double.parseDouble(locArray[0]), Double.parseDouble(locArray[1]), Double.parseDouble(locArray[2]));
        } else {
            return new Rotation(0, 0, 0);
        }
    }

    public static List<Vector3> parseVectorsFromStrings(String... str) {
        List<Vector3> vector3List = new ArrayList<>();
        for (String s : str) {
            vector3List.add(parseVectorFromString(s));
        }
        return vector3List;
    }

    public static List<Vector3> parseVectorsFromStrings(List<String> str) {
        return parseVectorsFromStrings(str.toArray(new String[0]));
    }

    public static Vector3 parseVectorFromString(String str) {
        String[] locArray = str.split(":");
        if (locArray.length == 3) {
            return new Vector3(Double.parseDouble(locArray[0]), Double.parseDouble(locArray[1]), Double.parseDouble(locArray[2]));
        } else {
            return null;
        }
    }

    public static List<AdvancedLocation> parseLocations(String... locationString) {
        List<AdvancedLocation> advancedLocations = new ArrayList<>();
        for (String s : locationString) {
            advancedLocations.add(parseLocation(s));
        }
        return advancedLocations;
    }

    public static AdvancedLocation parseLocation(String locationString) {
        String[] positions = locationString.split(":");
        if (positions.length < 4) {
            if (positions.length == 3) {
                AdvancedLocation loc = new AdvancedLocation();
                loc.setLocation(new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2])));
                loc.setVersion(AdvancedLocation.LocationType.POS);
                return loc;
            }
            GameAPI.getInstance().getLogger().warning("Wrong Location Format! Please check it again, text: " + locationString);
            return null;
        }
        if (!Server.getInstance().isLevelLoaded(positions[3])) {
            if (Server.getInstance().loadLevel(positions[3])) {
                Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
                AdvancedLocation advancedLocation = new AdvancedLocation();
                advancedLocation.setLocation(location);
                advancedLocation.setVersion(AdvancedLocation.LocationType.POS);
                if (positions.length >= 6) {
                    advancedLocation.setYaw(Double.parseDouble(positions[4]));
                    advancedLocation.setPitch(Double.parseDouble(positions[5]));
                    advancedLocation.setVersion(AdvancedLocation.LocationType.POS_AND_ROT_EXCEPT_HEADYAW);
                    if (positions.length == 7) {
                        advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                        advancedLocation.setVersion(AdvancedLocation.LocationType.POS_AND_ROT);
                    }
                }
                return advancedLocation;
            } else {
                return null;
            }
        } else {
            Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
            AdvancedLocation advancedLocation = new AdvancedLocation();
            advancedLocation.setLocation(location);
            advancedLocation.setVersion(AdvancedLocation.LocationType.POS);
            if (positions.length >= 6) {
                advancedLocation.setYaw(Double.parseDouble(positions[4]));
                advancedLocation.setPitch(Double.parseDouble(positions[5]));
                advancedLocation.setVersion(AdvancedLocation.LocationType.POS_AND_ROT_EXCEPT_HEADYAW);
                if (positions.length == 7) {
                    advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                    advancedLocation.setVersion(AdvancedLocation.LocationType.POS_AND_ROT);
                }
            }
            return advancedLocation;
        }
    }

    public static IntegerAxisAlignBB[] splitSimpleAxisAlignedBB(IntegerAxisAlignBB original, int maxBlockSizeX, int maxBlockSizeY, int maxBlockSizeZ) {
        int minX = original.getMinX();
        int minY = original.getMinY();
        int minZ = original.getMinZ();
        int maxX = original.getMaxX();
        int maxY = original.getMaxY();
        int maxZ = original.getMaxZ();

        List<IntegerAxisAlignBB> result = new ArrayList<>();

        for (int currentMinX = minX; currentMinX <= maxX; currentMinX += maxBlockSizeX) {
            int currentMaxX = Math.min(maxX, currentMinX + maxBlockSizeX);

            for (int currentMinY = minY; currentMinY <= maxY; currentMinY += maxBlockSizeY) {
                int currentMaxY = Math.min(maxY, currentMinY + maxBlockSizeY);

                for (int currentMinZ = minZ; currentMinZ <= maxZ; currentMinZ += maxBlockSizeZ) {
                    int currentMaxZ = Math.min(maxZ, currentMinZ + maxBlockSizeZ);

                    result.add(new IntegerAxisAlignBB(new Vector3(currentMinX, currentMinY, currentMinZ), new Vector3(Math.min(maxX, currentMaxX), Math.min(maxY, currentMaxY), Math.min(maxZ, currentMaxZ))));
                }
            }
        }

        return result.toArray(new IntegerAxisAlignBB[0]);
    }

    public static Vector3 rotateDegreesClockwise(Vector3 point, double degrees) {
        double radians = Math.toRadians(degrees); // 将度转换为弧度
        double cosTheta = Math.cos(radians);
        double sinTheta = Math.sin(radians);
        double newX = cosTheta * point.x + sinTheta * point.y;
        double newY = -sinTheta * point.x + cosTheta * point.y;
        return new Vector3(newX, newY, point.z);
    }

    public static Vector3 rotateDegreesCounterClockwise(Vector3 point, double degrees) {
        double radians = Math.toRadians(degrees); // 将度转换为弧度
        double cosTheta = Math.cos(radians);
        double sinTheta = Math.sin(radians);
        double newX = cosTheta * point.x - sinTheta * point.y;
        double newY = sinTheta * point.x + cosTheta * point.y;
        return new Vector3(newX, newY, point.z);
    }

    public static List<Vector3> getRandomRoundPosition(Position position, int needCount, double xExpand, double yExpand, double zExpand, boolean allowDuplicated) {
        List<Vector3> allPosList = getAllPositionsAround(position, xExpand, yExpand, zExpand);
        if (allPosList.size() <= needCount) {
            return allPosList;
        }
        List<Vector3> results = new ArrayList<>();
        for (int i = 0; i < needCount; i++) {
            Vector3 result;
            if (allowDuplicated) {
                result = getRandomRoundPosition(position, xExpand, yExpand, zExpand);
            } else {
                result = getRandomRoundPosition(position, xExpand, yExpand, zExpand, results.toArray(new Vector3[0]));
            }
            results.add(result);
        }
        return results;
    }

    public static Vector3 getRandomRoundPosition(Position position, double xExpand, double yExpand, double zExpand, Vector3... excluded) {
        AxisAlignedBB bb = new SimpleAxisAlignedBB(position, position);
        bb.expand(xExpand, yExpand, zExpand);
        double xRand = RandomTools.getRandom(bb.getMinX(), bb.getMaxX());
        double yRand = RandomTools.getRandom(bb.getMinY(), bb.getMaxY());
        double zRand = RandomTools.getRandom(bb.getMinZ(), bb.getMaxZ());
        Vector3 result = new Vector3(xRand, yRand, zRand);
        for (Vector3 vector3 : excluded) {
            if (result.equals(vector3)) {
                return getRandomRoundPosition(position, xExpand, yExpand, zExpand);
            }
        }
        return result;
    }

    public static List<Vector3> getAllPositionsAround(Position position, double xExpand, double yExpand, double zExpand) {
        List<Vector3> results = new ArrayList<>();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(position, position);
        bb.expand(xExpand, yExpand, zExpand);
        bb.forEach((i, i1, i2) -> results.add(new Vector3(i, i1, i2)));
        return results;
    }

    public static double compareTwoBuilds(SimpleAxisAlignedBB bb, SimpleAxisAlignedBB bb1, Level level) {
        // 获取两个区域的尺寸
        int width1 = (int) (bb.getMaxX() - bb.getMinX() + 1);
        int height1 = (int) (bb.getMaxY() - bb.getMinY() + 1);
        int length1 = (int) (bb.getMaxZ() - bb.getMinZ() + 1);

        int width2 = (int) (bb1.getMaxX() - bb1.getMinX() + 1);
        int height2 = (int) (bb1.getMaxY() - bb1.getMinY() + 1);
        int length2 = (int) (bb1.getMaxZ() - bb1.getMinZ() + 1);

        // 如果尺寸不同，则返回0，因为它们不可能完全相同
        if (width1 != width2 || height1 != height2 || length1 != length2) {
            return 0.0;
        }

        int totalBlocks1 = width1 * height1 * length1;
        int startX1 = (int) bb.getMinX();
        int startY1 = (int) bb.getMinY();
        int startZ1 = (int) bb.getMinZ();
        int startX2 = (int) bb1.getMinX();
        int startY2 = (int) bb1.getMinY();
        int startZ2 = (int) bb1.getMinZ();

        Block[][][] blocksRegion1 = new Block[width1][height1][length1];
        Block[][][] blocksRegion2 = new Block[width2][height2][length2];

        for (int x = 0; x < width1; x++) {
            for (int y = 0; y < height1; y++) {
                for (int z = 0; z < length1; z++) {
                    blocksRegion1[x][y][z] = level.getBlock(startX1 + x, startY1 + y, startZ1 + z);
                }
            }
        }
        for (int x = 0; x < width2; x++) {
            for (int y = 0; y < height2; y++) {
                for (int z = 0; z < length2; z++) {
                    blocksRegion2[x][y][z] = level.getBlock(startX2 + x, startY2 + y, startZ2 + z);
                }
            }
        }

        int matchingBlocks = 0;
        for (int x = 0; x < width1; x++) {
            for (int y = 0; y < height1; y++) {
                for (int z = 0; z < length1; z++) {
                    Block block1 = blocksRegion1[x][y][z];
                    Block block2 = blocksRegion2[x][y][z];
                    if (block1.getId() == block2.getId() && block1.getDamage() == block2.getDamage()) {
                        matchingBlocks++;
                    }
                }
            }
        }
        return (double) matchingBlocks / totalBlocks1;
    }
}
