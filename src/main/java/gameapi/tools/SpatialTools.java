package gameapi.tools;

import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import gameapi.GameAPI;
import gameapi.utils.AdvancedLocation;
import gameapi.utils.IntegerAxisAlignBB;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public class SpatialTools {

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
}
