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

    public static Map<String, Object> getGameRecordAll(String gameName, String fileName) {
        if (GameAPI.gameRecord.containsKey(gameName + "/" + fileName)) {
            return GameAPI.gameRecord.get(gameName + "/" + fileName);
        } else {
            return new LinkedHashMap<>();
        }
    }

    public static void addGameRecord(String gameName, String fileName, String player, Integer add) {
        Map<String, Object> allData = getGameRecordAll(gameName, fileName); // o1 -> o2
        int value = (Integer) allData.getOrDefault(player, 0) + add;
        allData.put(player, value);
        GameAPI.gameRecord.put(gameName + "/" + fileName, allData);
        Config config = new Config(GameAPI.path + "/gameRecords/" + gameName + "/" + fileName + ".yml", Config.YAML);
        config.set(player, value);
        config.save();
    }

    public static void reduceGameRecord(String gameName, String fileName, String player, Integer reduce) {
        Map<String, Object> allData = getGameRecordAll(gameName, fileName);
        int value = (Integer) allData.getOrDefault(player, 0) - reduce;
        allData.put(player, value);
        GameAPI.gameRecord.put(gameName + "/" + fileName, allData);
        Config config = new Config(GameAPI.path + "/gameRecords/" + gameName + "/" + fileName + ".yml", Config.YAML);
        config.set(player, value);
        config.save();
    }

    public static int getGameRecord(String gameName, String fileName, String player) {
        return (int) getGameRecord(gameName, fileName, player, 0);
    }

    public static Object getGameRecord(String gameName, String fileName, String player, Object defaultValue) {
        Map<String, Object> allData = getGameRecordAll(gameName, fileName);
        if (allData.containsKey(player)) {
            return allData.getOrDefault(player, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static void setGameRecord(String gameName, String fileName, String player, Object value) {
        Map<String, Object> allData = getGameRecordAll(gameName, fileName);
        allData.put(player, value);
        GameAPI.gameRecord.put(gameName + "/" + fileName, allData);
        Config config = new Config(GameAPI.path + "/gameRecords/" + gameName + "/" + fileName + ".yml", Config.YAML);
        config.set(player, value);
        config.save();
    }

}
