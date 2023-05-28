package gameapi.entity;


import cn.nukkit.event.entity.EntityDespawnEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.GameAPI;
import gameapi.utils.GameRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RankingListEntity extends TextEntity
{

    private String gameName;

    private String comparedKey;

    private GameRecord.SortSequence sortSequence;

    public RankingListEntity(FullChunk chunk, Position position, String text, GameRecord.SortSequence sortSequence, CompoundTag nbt) {
        super(chunk, position, text, nbt);
        this.sortSequence = sortSequence;
    }

    public RankingListEntity(FullChunk chunk, Position position, String gameName, String comparedKey, GameRecord.SortSequence sortSequence, CompoundTag nbt) {
        super(chunk, position, getGameRecordRankingListString(gameName, comparedKey, sortSequence), nbt);
        this.gameName = gameName;
        this.comparedKey = comparedKey;
        this.sortSequence = sortSequence;
    }

    public boolean onUpdate(int currentTick)
    {
        if(this.health <= 0){
            this.health+=this.getMaxHealth();
        }
        if(currentTick % GameAPI.entityRefreshIntervals == 0){
            this.setNameTag(getGameRecordRankingListString(gameName, comparedKey, sortSequence));
        }
        return super.onUpdate(currentTick);
    }


    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.server.getPluginManager().callEvent(new EntityDespawnEvent(this));
            this.despawnFromAll();
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }

            if (this.level != null) {
                this.level.removeEntity(this);
            }
        }
    }

    protected static Map<String, Integer> getGameRecordRankingList(String gameName, String comparedKey, boolean isPreSorted, Object... params){
        Map<String, Object> allData = GameRecord.getGameRecordAll(gameName);
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


    protected static String getGameRecordRankingListString(String gameName, String comparedKey, GameRecord.SortSequence sortSequence){
        GameRecord.RankingFormat rankingFormat = GameRecord.getRankingFormat();
        return getGameRecordRankingListString(gameName, comparedKey, rankingFormat.getTitle().replace("%gameName%", gameName), rankingFormat.getScoreShowFormat(), sortSequence);
    }

    protected static String getGameRecordRankingListString(String gameName, String comparedKey, String title, String format, GameRecord.SortSequence sortSequence){
        Map<String, Integer> objectMap = getGameRecordRankingList(gameName, comparedKey, false);
        StringBuilder builder = new StringBuilder().append(title.replace("\\n", "\n"));
        List<Map.Entry<String, Integer>> e;
        if(sortSequence == GameRecord.SortSequence.DESCEND){
            e = objectMap.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toList());
        }else{
            e = objectMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        }
        GameRecord.RankingFormat rankingFormat = GameRecord.getRankingFormat();
        if(e.size() > 0) {
            for (int i = 0; i < e.size(); i++) {
                Map.Entry<String, Integer> cache = e.get(i);
                String text = format.replace("%rank%", String.valueOf(i+1)).replace("%player%", cache.getKey()).replace("%score%", String.valueOf(cache.getValue())).replace("\\n", "\n");
                switch (i) {
                    case 0:
                        builder.append("§f\n").append(rankingFormat.getChampionPrefix());
                        break;
                    case 1:
                        builder.append("§f\n").append(rankingFormat.getRunnerUpPrefix());
                        break;
                    case 2:
                        builder.append("§f\n").append(rankingFormat.getSecondRunnerUpPrefix());
                        break;
                    default:
                        builder.append("§f\n");
                        break;
                }
                builder.append(text);
            }
        }else{
            builder.append(rankingFormat.getNoData());
        }
        return builder.toString();
    }
}