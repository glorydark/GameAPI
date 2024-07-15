package gameapi.manager.data;

import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.ranking.RankingFormat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author lt_name (CrystalWar)
 * Glorydark made some changes to adapt GameAPI.
 */

public class PlayerGameDataManager {

    protected static HashMap<String, Map<String, Object>> playerData = new HashMap<>();

    protected static RankingFormat rankingFormat = new RankingFormat();

    public static RankingFormat getRankingFormat() {
        return rankingFormat;
    }

    public static void setRankingFormat(RankingFormat rf) {
        rankingFormat = rf;
    }

    public static Map<String, Object> getPlayerAllGameData(String gameName, String fileName) {
        if (playerData.containsKey(gameName + "/" + fileName)) {
            return playerData.get(gameName + "/" + fileName);
        } else {
            return new LinkedHashMap<>();
        }
    }

    public static void addPlayerGameData(String gameName, String fileName, String player, Integer add) {
        Map<String, Object> allData = getPlayerAllGameData(gameName, fileName); // o1 -> o2
        int value = (Integer) allData.getOrDefault(player, 0) + add;
        allData.put(player, value);
        playerData.put(gameName + "/" + fileName, allData);
        Config config = new Config(GameAPI.getPath() + "/gameRecords/" + gameName + "/" + fileName + ".yml", Config.YAML);
        config.set(player, value);
        config.save();
    }

    public static void reducePlayerGameData(String gameName, String fileName, String player, Integer reduce) {
        Map<String, Object> allData = getPlayerAllGameData(gameName, fileName);
        int value = (Integer) allData.getOrDefault(player, 0) - reduce;
        allData.put(player, value);
        playerData.put(gameName + "/" + fileName, allData);
        Config config = new Config(GameAPI.getPath() + "/gameRecords/" + gameName + "/" + fileName + ".yml", Config.YAML);
        config.set(player, value);
        config.save();
    }

    public static int getPlayerGameData(String gameName, String fileName, String player) {
        return getPlayerGameData(gameName, fileName, player, 0);
    }

    public static <T> T getPlayerGameData(String gameName, String fileName, String player, T defaultValue) {
        Map<String, Object> allData = getPlayerAllGameData(gameName, fileName);
        if (allData.containsKey(player)) {
            if (defaultValue instanceof String) {
                return (T) allData.getOrDefault(player, defaultValue).toString();
            } else {
                return (T) allData.getOrDefault(player, defaultValue);
            }
        } else {
            return defaultValue;
        }
    }

    public static void setPlayerGameData(String gameName, String fileName, String player, Object value) {
        Map<String, Object> allData = getPlayerAllGameData(gameName, fileName);
        allData.put(player, value);
        playerData.put(gameName + "/" + fileName, allData);
        Config config = new Config(GameAPI.getPath() + "/gameRecords/" + gameName + "/" + fileName + ".yml", Config.YAML);
        config.set(player, value);
        config.save();
    }

    public static void setPlayerGameData(HashMap<String, Map<String, Object>> playerData) {
        PlayerGameDataManager.playerData = playerData;
    }

    public static void close() {
        playerData.clear();
    }
}
