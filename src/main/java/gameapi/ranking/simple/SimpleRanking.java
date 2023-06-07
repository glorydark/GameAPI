package gameapi.ranking.simple;

import cn.nukkit.level.Location;
import gameapi.ranking.Ranking;
import gameapi.ranking.RankingFormat;
import gameapi.ranking.RankingSortSequence;
import gameapi.utils.GameRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleRanking extends Ranking {

    // This only stores the inner compared values
    private final String gameName;

    private final String comparedKey;

    public SimpleRanking(Location location, String title, String noDataContent, RankingFormat rankingFormat, RankingSortSequence rankingSortSequence, String gameName, String comparedKey){
        super(location, title, noDataContent, rankingFormat, rankingSortSequence);
        this.rankingData = new HashMap<>();
        this.gameName = gameName;
        this.comparedKey = comparedKey;
    }

    public String getDisplayContent(){
        RankingFormat format = this.getRankingFormat();
        StringBuilder builder = new StringBuilder().append(this.getTitle().replace("\\n", "\n"));
        if(rankingData.size() > 0) {
            int i = 1;
            for (Map.Entry<String, Integer> entry : rankingData.entrySet()) {
                String text = format.getScoreShowFormat().replace("%rank%", String.valueOf(i+1)).replace("%player%", entry.getKey()).replace("%score%", String.valueOf(entry.getValue())).replace("\\n", "\n");
                switch (i) {
                    case 0:
                        builder.append("§f\n").append(format.getChampionPrefix());
                        break;
                    case 1:
                        builder.append("§f\n").append(format.getRunnerUpPrefix());
                        break;
                    case 2:
                        builder.append("§f\n").append(format.getSecondRunnerUpPrefix());
                        break;
                    default:
                        builder.append("§f\n");
                        break;
                }
                builder.append(text);
                i++;
            }
        }else{
            builder.append(this.getNoDataContent().replace("\\n", "\n"));
        }
        return builder.toString();
    }

    public void refreshRankingData(){
        Map<String, Integer> output = new HashMap<>(getRankingData());
        // 先转换成Map.Entry进行排序
        List<Map.Entry<String, Integer>> entryList_temp;
        if(this.getRankingSortSequence() == RankingSortSequence.DESCEND){
            entryList_temp = output.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toList());
        }else{
            entryList_temp = output.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        }
        // 导出为Map
        for(Map.Entry<String, Integer> final_entry: entryList_temp){
            output.put(final_entry.getKey(), final_entry.getValue());
        }
        this.rankingData = output;
    }

    public Map<String, Integer> getRankingData(){
        Map<String, Integer> output = new HashMap<>();
        for(Map.Entry<String, Object> objectEntry : GameRecord.getGameRecordAll(this.gameName).entrySet()){
            HashMap<String, Object> objectHashMap = (HashMap<String, Object>) objectEntry.getValue();
            output.put(objectEntry.getKey(), (int) objectHashMap.get(this.comparedKey));
        }
        return output;
    }

}
