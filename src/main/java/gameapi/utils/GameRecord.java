package gameapi.utils;

import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.ranking.RankingFormat;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author lt_name (CrystalWar)
 * Glorydark made some changes to adapt GameAPI.
 */

public class GameRecord {

    protected static RankingFormat rankingFormat = new RankingFormat();

    public static RankingFormat getRankingFormat() {
        return rankingFormat;
    }

    public static void setRankingFormat(RankingFormat rf) {
        rankingFormat = rf;
    }

    public static Map<String, Object> getGameRecordAll(String gameName) {
        if (GameAPI.gameRecord.containsKey(gameName)) {
            return GameAPI.gameRecord.get(gameName);
        } else {
            return new LinkedHashMap<>();
        }
    }

    public static void addGameRecord(String gameName, String player, String key, Integer add) {
        Map<String, Object> allData = getGameRecordAll(gameName); // o1 -> o2
        Map<String, Object> playerData;
        if (allData.containsKey(player)) {
            playerData = (Map<String, Object>) allData.get(player);
            if (playerData.containsKey(key)) {
                playerData.put(key, (Integer) playerData.get(key) + add);
            } else {
                playerData.put(key, add);
            }
        } else {
            playerData = new LinkedHashMap<>();
            playerData.put(key, add);
        }
        allData.put(player, playerData);
        GameAPI.gameRecord.put(gameName, allData);
        Config config = new Config(GameAPI.path + "/gameRecords/" + gameName + ".yml", Config.YAML);
        config.set(player, playerData);
        config.save();
    }

    public static void reduceGameRecord(String gameName, String player, String key, Integer reduce) {
        Map<String, Object> allData = getGameRecordAll(gameName);
        Map<String, Object> playerData;
        if (allData.containsKey(player)) {
            playerData = (Map<String, Object>) allData.get(player);
            if (playerData.containsKey(key)) {
                playerData.put(key, (Integer) playerData.get(key) - reduce);
            } else {
                playerData.put(key, -reduce);
            }
        } else {
            playerData = new LinkedHashMap<>();
            playerData.put(key, -reduce);
        }
        allData.put(player, playerData);
        GameAPI.gameRecord.put(gameName, allData);
        Config config = new Config(GameAPI.path + "/gameRecords/" + gameName + ".yml", Config.YAML);
        config.set(player, playerData);
        config.save();
    }

    public static int getGameRecord(String gameName, String player, String key) {
        return (int) getGameRecord(gameName, player, key, 0);
    }

    public static Object getGameRecord(String gameName, String player, String key, Object defaultValue) {
        Map<String, Object> allData = getGameRecordAll(gameName);
        if (allData.containsKey(player)) {
            Map<String, Object> data = (Map<String, Object>) allData.get(player);
            return data.getOrDefault(key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static void setGameRecord(String gameName, String player, String key, Object value) {
        Map<String, Object> allData = getGameRecordAll(gameName);
        Map<String, Object> playerData = (Map<String, Object>) allData.getOrDefault(player, new LinkedHashMap<>());
        playerData.put(key, value);
        allData.put(player, playerData);
        GameAPI.gameRecord.put(gameName, allData);
        Config config = new Config(GameAPI.path + "/gameRecords/" + gameName + ".yml", Config.YAML);
        config.set(player, playerData);
        config.save();
    }

}
