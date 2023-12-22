package gameapi.ranking;

import cn.nukkit.level.Location;
import gameapi.entity.EntityTools;
import gameapi.entity.RankingListEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Ranking {

    // This only stores the inner compared values
    protected Map<String, Integer> rankingData;

    private RankingSortSequence rankingSortSequence;

    private String title;

    private String noDataContent;

    private RankingFormat rankingFormat;

    @Setter(AccessLevel.NONE)
    private RankingListEntity entity;

    private Location location;

    public Ranking(Location location, String title, String noDataContent, RankingFormat rankingFormat, RankingSortSequence rankingSortSequence) {
        this.location = location;
        this.title = title;
        this.noDataContent = noDataContent;
        this.rankingFormat = rankingFormat;
        this.rankingData = new HashMap<>();
        this.rankingSortSequence = rankingSortSequence;
    }

    public String getDisplayContent() {
        StringBuilder builder = new StringBuilder().append(title.replace("\\n", "\n"));
        if (rankingData.size() > 0) {
            int i = 1;
            for (Map.Entry<String, Integer> entry : rankingData.entrySet()) {
                String text = rankingFormat.getScoreShowFormat().replace("%rank%", String.valueOf(i + 1)).replace("%player%", entry.getKey()).replace("%score%", String.valueOf(entry.getValue())).replace("\\n", "\n");
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
                i++;
            }
        } else {
            builder.append(noDataContent.replace("\\n", "\n"));
        }
        return builder.toString();
    }

    public void refreshRankingData() {
        Map<String, Integer> output = new HashMap<>(getRankingData());
        // 先转换成Map.Entry进行排序
        List<Map.Entry<String, Integer>> entryList_temp;
        if (this.rankingSortSequence == RankingSortSequence.DESCEND) {
            entryList_temp = output.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toList());
        } else {
            entryList_temp = output.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        }
        // 导出为Map
        for (Map.Entry<String, Integer> final_entry : entryList_temp) {
            output.put(final_entry.getKey(), final_entry.getValue());
        }
        this.rankingData = output;
    }

    public Map<String, Integer> getRankingData() {
        return new HashMap<>();
    }

    public void spawnEntity() {
        EntityTools.spawnTextEntity(this.location, this);
    }

}
