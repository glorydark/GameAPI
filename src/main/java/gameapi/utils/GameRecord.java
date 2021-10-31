package gameapi.utils;

import com.sun.org.glassfish.gmbal.Description;
import gameapi.MainClass;

import java.util.*;

@Description("Author: lt-name") //引用若水的NBT物品保存代码

public class GameRecord {

    public static Map<String, Object> getGameRecordAll(String gameName){
        if(MainClass.gameRecord.containsKey(gameName)){
            return MainClass.gameRecord.get(gameName);
        }else{
            return new TreeMap<>();
        }
    }

    public static void setGameRecordAll(String gameName, Map<String, Object> map){
        MainClass.gameRecord.put(gameName,map);
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
        Map<String, Object> allData = getGameRecordAll(gameName);
        if(allData.containsKey(player)) {
            Map<String, Object> data = (Map<String, Object>) allData.get(player);
            if(data.containsKey(key)){
                data.put(key,(Integer)data.get(key) + add);
            }else{
                data.put(key,add);
            }
            allData.put(player,data);
        }else{
            Map<String, Object> playerData = new TreeMap<>();
            playerData.put(key,add);
            allData.put(player,playerData);
        }
        setGameRecordAll(gameName,allData);
    }

    public static void reduceGameRecord(String gameName, String player, String key, Integer reduce){
        Map<String, Object> allData = getGameRecordAll(gameName);
        if(allData.containsKey(player)) {
            Map<String, Object> data = (Map<String, Object>) allData.get(player);
            if(data.containsKey(key)){
                data.put(key,(Integer)data.get(key) - reduce);
            }else{
                data.put(key,-reduce);
            }
            allData.put(player,data);
        }else{
            Map<String, Object> playerData = new TreeMap<>();
            playerData.put(key,-reduce);
            allData.put(player,playerData);
        }
        setGameRecordAll(gameName,allData);
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
