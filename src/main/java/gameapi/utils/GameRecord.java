package gameapi.utils;

import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import lombok.Data;

import java.util.*;


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

    public static Map<String, Object> getGameRecordAll(String gameName){
        if(GameAPI.gameRecord.containsKey(gameName)){
            return GameAPI.gameRecord.get(gameName);
        }else{
            return new LinkedHashMap<>();
        }
    }

    public static void addGameRecord(String gameName, String player, String key, Integer add){
        Map<String, Object> allData = getGameRecordAll(gameName); // o1 -> o2
        Map<String, Object> playerData;
        if(allData.containsKey(player)) {
            playerData = (Map<String, Object>) allData.get(player);
            if(playerData.containsKey(key)){
                playerData.put(key,(Integer)playerData.get(key) + add);
            }else{
                playerData.put(key,add);
            }
        }else {
            playerData = new LinkedHashMap<>();
            playerData.put(key, add);
        }
        allData.put(player,playerData);
        GameAPI.gameRecord.put(gameName, allData);
        Config config = new Config(GameAPI.path + "/gameRecords/"+gameName+".yml",Config.YAML);
        config.set(player, playerData);
        config.save();
    }

    public static void reduceGameRecord(String gameName, String player, String key, Integer reduce){
        Map<String, Object> allData = getGameRecordAll(gameName);
        Map<String, Object> playerData;
        if(allData.containsKey(player)) {
            playerData = (Map<String, Object>) allData.get(player);
            if(playerData.containsKey(key)){
                playerData.put(key,(Integer)playerData.get(key) - reduce);
            }else{
                playerData.put(key,-reduce);
            }
        }else{
            playerData = new LinkedHashMap<>();
            playerData.put(key,-reduce);
        }
        allData.put(player,playerData);
        GameAPI.gameRecord.put(gameName, allData);
        Config config = new Config(GameAPI.path + "/gameRecords/"+gameName+".yml",Config.YAML);
        config.set(player, playerData);
        config.save();
    }

    public static int getGameRecord(String gameName, String player, String key){
        Map<String, Object> allData = getGameRecordAll(gameName);
        if(allData.containsKey(player)) {
            Map<String, Object> data = (Map<String, Object>) allData.get(player);
            if(data.containsKey(key)){
                return (int) data.get(key);
            }else{
                return 0;
            }
        }else{
            return 0;
        }
    }

    @Data
    public static class RankingFormat{
        String title = "%gameName%";

        String scoreShowFormat = "[%rank%] %player%: %score%";

        String championPrefix = "§6";

        String runnerUpPrefix = "§e";

        String secondRunnerUpPrefix = "§a";

        String noData = "§a§lNothing is here...";

        public RankingFormat(){

        }

        public RankingFormat(String title, String scoreShowFormat, String champion_prefix, String runnerUpPrefix, String secondRunnerUpPrefix, String noData){
            this.title = title;
            this.scoreShowFormat = scoreShowFormat;
            this.championPrefix = champion_prefix;
            this.runnerUpPrefix = runnerUpPrefix;
            this.secondRunnerUpPrefix = secondRunnerUpPrefix;
            this.noData = noData;
        }
    }

    public enum SortSequence {
        DESCEND,
        ASCEND
    }
}
