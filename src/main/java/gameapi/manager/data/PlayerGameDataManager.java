package gameapi.manager.data;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.manager.GameDebugManager;
import gameapi.ranking.RankingFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author lt_name (CrystalWar)
 * Glorydark made some changes to adapt GameAPI.
 */

public class PlayerGameDataManager {

    protected static Map<String, Map<String, Object>> playerData = new LinkedHashMap<>();

    @Setter
    @Getter
    protected static RankingFormat rankingFormat = new RankingFormat();

    public static Map<String, Object> getPlayerAllGameData(String gameName, String fileName) {
        return playerData.getOrDefault(gameName + "/" + fileName, new LinkedHashMap<>());
    }

    public static void addPlayerGameData(String gameName, String fileName, Player player, Integer add) {
        addPlayerGameData(gameName, fileName, player.getName(), add);
    }

    public static void addPlayerGameData(String gameName, String fileName, String player, Integer add) {
        if (add == 0) {
            return;
        }
        Map<String, Object> allData = getPlayerAllGameData(gameName, fileName); // o1 -> o2
        int value = (Integer) allData.getOrDefault(player, 0) + add;
        allData.put(player, value);
        playerData.put(gameName + "/" + fileName, allData);
        Config config = new Config(GameAPI.getPath() + File.separator + "gameRecords" + File.separator + gameName + File.separator + fileName + ".yml", Config.YAML);
        config.set(player, value);
        config.save();
    }

    public static void reducePlayerGameData(String gameName, String fileName, Player player, Integer reduce) {
        reducePlayerGameData(gameName, fileName, player.getName(), reduce);
    }

    public static void reducePlayerGameData(String gameName, String fileName, String player, Integer reduce) {
        if (reduce == 0) {
            return;
        }
        Map<String, Object> allData = getPlayerAllGameData(gameName, fileName);
        int value = (Integer) allData.getOrDefault(player, 0) - reduce;
        allData.put(player, value);
        playerData.put(gameName + "/" + fileName, allData);
        Config config = new Config(GameAPI.getPath() + File.separator + "gameRecords" + File.separator + gameName + File.separator + fileName + ".yml", Config.YAML);
        config.set(player, value);
        config.save();
    }

    public static int getPlayerGameData(String gameName, String fileName, Player player) {
        return getPlayerGameData(gameName, fileName, player.getName());
    }

    public static int getPlayerGameData(String gameName, String fileName, String player) {
        return getPlayerGameData(gameName, fileName, player, 0);
    }

    public static <T> T getPlayerGameData(String gameName, String fileName, Player player, T defaultValue) {
        return getPlayerGameData(gameName, fileName, player.getName(), defaultValue);
    }

    public static <T> T getPlayerGameData(String gameName, String fileName, String player, T defaultValue) {
        Map<String, Object> allData = getPlayerAllGameData(gameName, fileName);
        if (allData.containsKey(player)) {
            Object data = allData.get(player);
            // 检查defaultValue是否是int类型，并且读取的数据是Long类型
            if (defaultValue instanceof Integer && data instanceof Long) {
                long longData = (Long) data;
                // 检查Long值是否在int的范围内
                if (longData >= Integer.MIN_VALUE && longData <= Integer.MAX_VALUE) {
                    GameAPI.getGameDebugManager().warning("Find a suspicious conversion from Integer to Long: " + gameName +"/" + fileName);
                    return (T) Integer.valueOf(String.valueOf(longData));
                } else {
                    // 如果超出范围，可以选择抛出异常或者返回默认值
                    // 例如：throw new IllegalArgumentException("Value is out of range for int: " + longData);
                    GameAPI.getGameDebugManager().warning("Find a suspicious conversion from Integer to Long and the value is out of limit: " + gameName +"/" + fileName);
                    return defaultValue;
                }
            } else if (defaultValue instanceof Long && data instanceof Integer) {
                // 如果期望的是Long类型，并且实际读取的是Integer类型，直接转换为Long
                GameAPI.getGameDebugManager().warning("Find a suspicious conversion from Long to Integer: " + gameName +"/" + fileName);
                return (T) Long.valueOf(String.valueOf(data));
            } else if (defaultValue instanceof String) {
                // 如果期望的是String类型，直接转换为String
                return (T) data.toString();
            } else {
                // 对于其他类型，直接返回
                return (T) data;
            }
        } else {
            return defaultValue;
        }
    }

    public static void setPlayerGameData(String gameName, String fileName, Player player, Object value) {
        setPlayerGameData(gameName, fileName, player.getName(), value);
    }

    public static void setPlayerGameData(String gameName, String fileName, String player, Object value) {
        Map<String, Object> allData = getPlayerAllGameData(gameName, fileName);
        allData.put(player, value);
        playerData.put(gameName + "/" + fileName, allData);
        Config config = new Config(GameAPI.getPath() + File.separator + "gameRecords" + File.separator + gameName + File.separator + fileName + ".yml", Config.YAML);
        config.set(player, value);
        config.save();
    }

    public static void setPlayerGameData(Map<String, Map<String, Object>> playerData) {
        PlayerGameDataManager.playerData = playerData;
    }

    public static void close() {
        playerData.clear();
    }
}
