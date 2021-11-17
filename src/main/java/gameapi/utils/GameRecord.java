package gameapi.utils;

import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import com.sun.org.glassfish.gmbal.Description;
import gameapi.MainClass;

import java.util.*;

@Description("Author: lt-name")

public class GameRecord {

    public static Map<String, Object> getGameRecordAll(String gameName){
        if(MainClass.gameRecord.containsKey(gameName)){
            return MainClass.gameRecord.get(gameName);
        }else{
            return new LinkedHashMap<>();
        }
    }

    public static Map<String, Integer> getGameRecordRankingList(String gameName, String comparedKey){
        Map<String, Object> allData = getGameRecordAll(gameName);
        HashMap<String, Integer> rankingList = new HashMap<>();
        for(String s:allData.keySet()){
            Map<String, Object> data = (Map<String, Object>) allData.get(s);
            Integer value = Integer.parseInt((String) data.get(comparedKey));
            rankingList.put(s,value);
        }
        List<Map.Entry<String, Integer>> list = new ArrayList<>(rankingList.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        rankingList.clear();
        for(Map.Entry<String, Integer> entry:list){
            rankingList.put(entry.getKey(),entry.getValue());
        }
        return rankingList;
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
        MainClass.gameRecord.put(gameName, allData);
        Config config = new Config(MainClass.path + "/gameRecords/"+gameName+".yml",Config.YAML);
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
        MainClass.gameRecord.put(gameName, allData);
        Config config = new Config(MainClass.path + "/gameRecords/"+gameName+".yml",Config.YAML);
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
}
