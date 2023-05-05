package gameapi.utils;

import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author lt_name (CrystalWar)
 * Glorydark made some changes to adapt GameAPI.
 */

public class GameRecord {

    static RankingFormat rankingFormat = new RankingFormat();

    public static Map<String, Object> getGameRecordAll(String gameName){
        if(GameAPI.gameRecord.containsKey(gameName)){
            return GameAPI.gameRecord.get(gameName);
        }else{
            return new LinkedHashMap<>();
        }
    }

    public static Map<String, Integer> getGameRecordRankingList(String gameName, String comparedKey, boolean isPreSorted, Object... params){
        Map<String, Object> allData = getGameRecordAll(gameName);
        Map<String, Integer> rankingList = new HashMap<>();
        for(String s:allData.keySet()){
            Map<String, Object> data = (Map<String, Object>) allData.getOrDefault(s, new HashMap<>());
            if(data.containsKey(comparedKey)){
                Integer value = Integer.parseInt(data.get(comparedKey).toString());
                rankingList.put(s,value);
            }
        }
        if(isPreSorted) {
            List<Map.Entry<String, Integer>> entryList = new ArrayList<>(rankingList.entrySet());
            rankingList.clear();
            entryList.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
            entryList.forEach(entry -> {
                rankingList.put(entry.getKey(), entry.getValue());
                System.out.println(entry.getKey() + ":" + entry.getValue());
            });
        }
        return rankingList;
    }


    public static String getGameRecordRankingListString(String gameName, String comparedKey){
        return getGameRecordRankingListString(gameName, comparedKey, rankingFormat.title.replace("%gameName%", gameName), rankingFormat.score_show_format);
    }

    public static String getGameRecordRankingListString(String gameName, String comparedKey, String title, String format){
        Map<String, Integer> objectMap = getGameRecordRankingList(gameName, comparedKey, false);
        StringBuilder builder = new StringBuilder().append(title.replace("\\n", "\n"));
        List<Map.Entry<String, Integer>> e = objectMap.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toList());
        if(e.size() > 0) {
            for (int i = 0; i < e.size(); i++) {
                Map.Entry<String, Integer> cache = e.get(i);
                String text = format.replace("%rank%", String.valueOf(i+1)).replace("%player%", cache.getKey()).replace("%score%", String.valueOf(cache.getValue())).replace("\\n", "\n");
                switch (i) {
                    case 0:
                        builder.append("§f\n"+rankingFormat.champion_prefix);
                        break;
                    case 1:
                        builder.append("§f\n"+rankingFormat.runnerUp_prefix);
                        break;
                    case 2:
                        builder.append("§f\n"+rankingFormat.secondRunnerUp_prefix);
                        break;
                    default:
                        builder.append("§f\n");
                        break;
                }
                builder.append(text);
            }
        }else{
            builder.append(rankingFormat.no_data);
        }
        return builder.toString();
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

        String score_show_format = "[%rank%] %player%: %score%";

        String champion_prefix = "§6";

        String runnerUp_prefix = "§e";

        String secondRunnerUp_prefix = "§a";

        String no_data = "§a§l暂无数据";

        public RankingFormat(){

        }

        public RankingFormat(String title, String score_show_format, String champion_prefix, String runnerUp_prefix, String secondRunnerUp_prefix, String no_data){
            this.title = title;
            this.score_show_format = score_show_format;
            this.champion_prefix = champion_prefix;
            this.runnerUp_prefix = runnerUp_prefix;
            this.secondRunnerUp_prefix = secondRunnerUp_prefix;
            this.no_data = no_data;
        }
    }

    public static void setRankingFormat(RankingFormat rf) {
        rankingFormat = rf;
    }
}
