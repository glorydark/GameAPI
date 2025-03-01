package gameapi.ranking;

import gameapi.GameAPI;
import gameapi.entity.RankingListEntity;
import gameapi.ranking.simple.RankingValueType;
import gameapi.tools.SmartTools;
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
    protected Map<String, ?> rankingData;
    private RankingSortSequence rankingSortSequence;
    private String title;
    private String noDataContent;
    private RankingFormat rankingFormat;
    @Setter(AccessLevel.NONE)
    private RankingListEntity entity;
    private int maxDisplayCount;
    private long lastUpdateMillis = 0L;

    private static final String SEQUENCE_ASCEND = "ascend";

    private static final String TYPE_DOUBLE = "double";
    private static final String TYPE_LONG_TO_TIME = "long_to_time";
    private static final String TYPE_FLOAT = "float";
    private static final String TYPE_LONG = "long";
    private static final String TYPE_INTEGER = "integer";

    private static final String FORMAT_RESET = "§f";

    public Ranking(RankingValueType valueType, String title, String noDataContent, RankingFormat rankingFormat, RankingSortSequence rankingSortSequence, int maxDisplayCount) {
        this.title = title;
        this.noDataContent = noDataContent;
        this.rankingFormat = rankingFormat;
        this.rankingData = new LinkedHashMap<>(); // sequential needs
        this.rankingSortSequence = rankingSortSequence;
        this.type = valueType;
        this.maxDisplayCount = maxDisplayCount;
    }

    public static RankingSortSequence getRankingSortSequence(String s) {
        if (SEQUENCE_ASCEND.equals(s)) {
            return RankingSortSequence.ASCEND;
        }
        return RankingSortSequence.DESCEND;
    }

    public static RankingValueType getRankingValueType(String s) {
        switch (s.toLowerCase()) {
            case TYPE_DOUBLE:
                return RankingValueType.DOUBLE;
            case TYPE_LONG_TO_TIME:
                return RankingValueType.LONG_T0_TIME;
            case TYPE_FLOAT:
                return RankingValueType.FLOAT;
            case TYPE_LONG:
                return RankingValueType.LONG;
            case TYPE_INTEGER:
            default:
                return RankingValueType.INTEGER;
        }
    }

    public String getDisplayContent() {
        return getDisplayContent(false);
    }

    public String getDisplayContent(boolean onlyContent) {
        RankingFormat format = this.getRankingFormat();
        StringBuilder builder = new StringBuilder();
        if (!onlyContent) {
            builder.append(this.getTitle().replace("\\n", "\n")).append("\n");
        }
        if (!this.rankingData.isEmpty()) {
            builder.append(getRawRankingContent(this.maxDisplayCount, format));
        } else {
            if (!onlyContent) {
                builder.append("\n").append(this.getNoDataContent().replace("\\n", "\n"));
            }
        }
        return builder.toString();
    }

    public String getRawRankingContent(int maxDisplayCount, RankingFormat format) {
        StringBuilder builder = new StringBuilder();
        if (!this.getLatestRankingData().isEmpty()) {
            int i = 1;
            for (Map.Entry<String, ?> entry : this.getLatestRankingData().entrySet()) {
                if (maxDisplayCount == -1 || i <= maxDisplayCount) {
                    String text = format.getScoreShowFormat().replace("%rank%", String.valueOf(i)).replace("%player%", entry.getKey()).replace("\\n", "\n");
                    if (this.getType() == RankingValueType.LONG_T0_TIME) {
                        text = text.replace("%score%", SmartTools.timeMillisToString(Long.parseLong(entry.getValue().toString())));
                    } else {
                        text = text.replace("%score%", entry.getValue().toString());
                    }
                    switch (i) {
                        case 1:
                            builder.append(FORMAT_RESET).append(format.getChampionPrefix());
                            break;
                        case 2:
                            builder.append(FORMAT_RESET).append("\n").append(format.getRunnerUpPrefix());
                            break;
                        case 3:
                            builder.append(FORMAT_RESET).append("\n").append(format.getSecondRunnerUpPrefix());
                            break;
                        default:
                            builder.append(FORMAT_RESET).append("\n");
                            break;
                    }
                    builder.append(text);
                    i++;
                } else {
                    break;
                }
            }
        }
        return builder.toString();
    }

    public void refreshRankingData() {
        if (System.currentTimeMillis() - this.lastUpdateMillis <= 5000L) {
            return;
        }
        this.lastUpdateMillis = System.currentTimeMillis();
        Map<String, Object> oldRankingData = new LinkedHashMap<>(this.getLatestRankingData());
        Map<String, Object> output = new LinkedHashMap<>();
        // 先转换成Map.Entry进行排序
        switch (this.type) {
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
            case LONG_T0_TIME:
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

    public <T> Map<String, T> getMapByType(Map<String, Object> map, Class<T> clazz) {
        Map<String, T> newMap = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                if (clazz.isInstance(value)) {
                    newMap.put(key, (T) value);
                } else {
                    if (clazz.isAssignableFrom(Integer.class)) {
                        if (value.getClass().isAssignableFrom(Long.class)) {
                            if (((Long) value) <= Integer.MAX_VALUE) {
                                Object o = Integer.parseInt(value.toString());
                                newMap.put(key, (T) o);
                                continue;
                            }
                        }
                    } else if (clazz.isAssignableFrom(Long.class)) {
                        if (value.getClass().isAssignableFrom(Integer.class)) {
                            Object o = Long.parseLong(value.toString());
                            newMap.put(key, (T) o);
                            continue;
                        }
                    }

                    GameAPI.getInstance().getLogger().error("Can not convert value because value is not instance of the defined type: " + value + ", " + value.getClass().getName());
                }
            }
        }
        return newMap;
    }

    public Map<String, Object> getLatestRankingData() {
        return (Map<String, Object>) rankingData;
    }
}
