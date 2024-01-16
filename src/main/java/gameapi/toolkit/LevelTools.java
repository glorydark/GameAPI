package gameapi.toolkit;

import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import gameapi.GameAPI;
import gameapi.utils.AdvancedLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public class LevelTools {

    public static List<Vector3> parseVectorsFromStrings(String... str) {
        List<Vector3> vector3List = new ArrayList<>();
        for (String s : str) {
            vector3List.add(parseVectorFromString(s));
        }
        return vector3List;
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
                loc.setVersion(0);
                return loc;
            }
            GameAPI.plugin.getLogger().warning("Wrong Location Format! Please check it again, text: " + locationString);
            return null;
        }
        if (!Server.getInstance().isLevelLoaded(positions[3])) {
            if (Server.getInstance().loadLevel(positions[3])) {
                Location location = new Location(Double.parseDouble(positions[0]), Double.parseDouble(positions[1]), Double.parseDouble(positions[2]), Server.getInstance().getLevelByName(positions[3]));
                AdvancedLocation advancedLocation = new AdvancedLocation();
                advancedLocation.setLocation(location);
                advancedLocation.setVersion(0);
                if (positions.length >= 6) {
                    advancedLocation.setYaw(Double.parseDouble(positions[4]));
                    advancedLocation.setPitch(Double.parseDouble(positions[5]));
                    advancedLocation.setVersion(1);
                    if (positions.length == 7) {
                        advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                        advancedLocation.setVersion(2);
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
            advancedLocation.setVersion(0);
            if (positions.length >= 6) {
                advancedLocation.setYaw(Double.parseDouble(positions[4]));
                advancedLocation.setPitch(Double.parseDouble(positions[5]));
                advancedLocation.setVersion(1);
                if (positions.length == 7) {
                    advancedLocation.setHeadYaw(Double.parseDouble(positions[6]));
                    advancedLocation.setVersion(2);
                }
            }
            return advancedLocation;
        }
    }

    public static SimpleAxisAlignedBB[] splitSimpleAxisAlignedBB(SimpleAxisAlignedBB original, double maxBlockSizeX, double maxBlockSizeY, double maxBlockSizeZ) {
        double minX = original.getMinX();
        double minY = original.getMinY();
        double minZ = original.getMinZ();
        double maxX = original.getMaxX();
        double maxY = original.getMaxY();
        double maxZ = original.getMaxZ();

        double currentMinX = minX;
        double currentMinY = minY;
        double currentMinZ = minZ;

        int xBlocks = 0;
        while (currentMinX < maxX) {
            currentMinX += maxBlockSizeX;
            xBlocks++;
        }

        int yBlocks = 0;
        while (currentMinY < maxY) {
            currentMinY += maxBlockSizeY;
            yBlocks++;
        }

        int zBlocks = 0;
        while (currentMinZ < maxZ) {
            currentMinZ += maxBlockSizeZ;
            zBlocks++;
        }

        SimpleAxisAlignedBB[] result = new SimpleAxisAlignedBB[xBlocks * yBlocks * zBlocks];

        currentMinX = minX;
        for (int x = 0; x < xBlocks; x++) {
            double currentMaxX = Math.min(maxX, currentMinX + maxBlockSizeX);
            currentMinY = minY;

            for (int y = 0; y < yBlocks; y++) {
                double currentMaxY = Math.min(maxY, currentMinY + maxBlockSizeY);
                currentMinZ = minZ;

                for (int z = 0; z < zBlocks; z++) {
                    double currentMaxZ = Math.min(maxZ, currentMinZ + maxBlockSizeZ);

                    result[x * (yBlocks * zBlocks) + y * zBlocks + z] = new SimpleAxisAlignedBB(currentMinX, currentMinY, currentMinZ, currentMaxX, currentMaxY, currentMaxZ);

                    currentMinZ += maxBlockSizeZ;
                }

                currentMinY += maxBlockSizeY;
            }

            currentMinX += maxBlockSizeX;
        }

        return result;
    }
}
