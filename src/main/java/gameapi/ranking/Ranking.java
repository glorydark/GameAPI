package gameapi.ranking;

import cn.nukkit.level.Location;
import gameapi.GameAPI;
import gameapi.entity.GameEntityCreator;
import gameapi.entity.RankingListEntity;
import gameapi.ranking.simple.RankingValueType;
import gameapi.toolkit.SmartTools;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class Ranking {

    private final RankingValueType type;
    // This only stores the inner compared values
    protected LinkedHashMap<String, ?> rankingData;
    private RankingSortSequence rankingSortSequence;
    private String title;
    private String noDataContent;
    private RankingFormat rankingFormat;
    @Setter(AccessLevel.NONE)
    private RankingListEntity entity;
    private Location location;

    public Ranking(Location location, String type, String title, String noDataContent, RankingFormat rankingFormat, RankingSortSequence rankingSortSequence) {
        this.location = location;
        this.title = title;
        this.noDataContent = noDataContent;
        this.rankingFormat = rankingFormat;
        this.rankingData = new LinkedHashMap<>();
        this.rankingSortSequence = rankingSortSequence;
        switch (type.toLowerCase()) {
            case "double":
                this.type = RankingValueType.DOUBLE;
                break;
            case "integer_to_time":
                this.type = RankingValueType.INTEGER_T0_TIME;
                break;
            case "float":
                this.type = RankingValueType.FLOAT;
                break;
            case "long":
                this.type = RankingValueType.LONG;
                break;
            case "integer":
            default:
                this.type = RankingValueType.INTEGER;
                break;
        }
    }

    public String getDisplayContent() {
        RankingFormat format = this.getRankingFormat();
        StringBuilder builder = new StringBuilder().append(this.getTitle().replace("\\n", "\n"));
        if (rankingData.size() > 0) {
            int i = 1;
            for (Map.Entry<String, ?> entry : rankingData.entrySet()) {
                String text = format.getScoreShowFormat().replace("%rank%", String.valueOf(i)).replace("%player%", entry.getKey()).replace("\\n", "\n");
                if (this.getType() == RankingValueType.INTEGER_T0_TIME) {
                    text = text.replace("%score%", SmartTools.timeMillisToString(Long.parseLong(entry.getValue().toString())));
                } else {
                    text = text.replace("%score%", entry.getValue().toString());
                }
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
        } else {
            builder.append("\n").append(this.getNoDataContent().replace("\\n", "\n"));
        }
        return builder.toString();
    }

    public void refreshRankingData() {
        LinkedHashMap<String, Object> oldRankingData = new LinkedHashMap<>(getLatestRankingData());
        LinkedHashMap<String, Object> output = new LinkedHashMap<>();
        // 先转换成Map.Entry进行排序
        switch (type) {
            case DOUBLE:
                List<Map.Entry<String, Double>> doubleTemp;
                if (this.rankingSortSequence == RankingSortSequence.DESCEND) {
                    doubleTemp = getMapByType(oldRankingData, Double.class).entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).collect(Collectors.toList());
                } else {
                    doubleTemp = getMapByType(oldRankingData, Double.class).entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
                }
                for (Map.Entry<String, Double> final_entry : doubleTemp) {
                    output.put(final_entry.getKey(), final_entry.getValue());
                }
                break;
            case FLOAT:
                List<Map.Entry<String, Float>> floatTemp;
                if (this.rankingSortSequence == RankingSortSequence.DESCEND) {
                    floatTemp = getMapByType(oldRankingData, Float.class).entrySet().stream().sorted(Map.Entry.<String, Float>comparingByValue().reversed()).collect(Collectors.toList());
                } else {
                    floatTemp = getMapByType(oldRankingData, Float.class).entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
                }
                for (Map.Entry<String, Float> final_entry : floatTemp) {
                    output.put(final_entry.getKey(), final_entry.getValue());
                }
                break;
            case LONG:
                List<Map.Entry<String, Long>> longTemp;
                if (this.rankingSortSequence == RankingSortSequence.DESCEND) {
                    longTemp = getMapByType(oldRankingData, Long.class).entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).collect(Collectors.toList());
                } else {
                    longTemp = getMapByType(oldRankingData, Long.class).entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
                }
                for (Map.Entry<String, Long> final_entry : longTemp) {
                    output.put(final_entry.getKey(), final_entry.getValue());
                }
                break;
            case INTEGER:
            case INTEGER_T0_TIME:
            default:
                List<Map.Entry<String, Integer>> integerTemp;
                if (this.rankingSortSequence == RankingSortSequence.DESCEND) {
                    integerTemp = getMapByType(oldRankingData, Integer.class).entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toList());
                } else {
                    integerTemp = getMapByType(oldRankingData, Integer.class).entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
                }
                for (Map.Entry<String, Integer> final_entry : integerTemp) {
                    output.put(final_entry.getKey(), final_entry.getValue());
                }
                break;
        }
        // 导出为Map
        this.rankingData = output;
    }

    public void spawnEntity() {
        GameEntityCreator.spawnTextEntity(this.location, this);
    }

    public <T> Map<String, T> getMapByType(Map<String, Object> map, Class<T> clazz) {
        Map<String, T> newMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                if (clazz.isInstance(value)) {
                    newMap.put(key, (T) value);
                } else {
                    GameAPI.plugin.getLogger().error("Can not convert value because value is not instance of the defined type: " + value.toString());
                }
            }
        }
        return newMap;
    }

    public Map<String, Object> getLatestRankingData() {
        return (Map<String, Object>) rankingData;
    }
}
