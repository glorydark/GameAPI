package gameapi.commands.sub.ranking;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.manager.tools.GameEntityManager;
import gameapi.ranking.Ranking;
import gameapi.ranking.simple.SimpleRanking;
import gameapi.tools.SmartTools;
import org.checkerframework.checker.units.qual.A;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * @author glorydark
 */
public class FunctionRankingMonthlyConclusionCommand extends EasySubCommand {

    protected static final double MULTIPLIER_DEFAULT = 0.5;
    protected static final double MULTIPLIER_DYNAMIC = 1;
    protected static final double MULTIPLIER_DYNAMIC_PRO = 2;
    protected static final double MULTIPLIER_C1 = 1.5;
    protected static final double MULTIPLIER_C1P = 2;
    protected static final double MULTIPLIER_C2 = 3;
    protected static final double MULTIPLIER_C3 = 4;
    protected static final double MULTIPLIER_C3P = 5;
    protected static final double MULTIPLIER_C4 = 6;


    public FunctionRankingMonthlyConclusionCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        int i = 1;
        if (commandSender.isPlayer()) {
            Map<String, Double> map = getDetailedMap().getOrDefault(commandSender.getName(), new LinkedHashMap<>());
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, Double> all : getDetailedMap().get("all").entrySet()) {
                switch (i) {
                    case 1:
                        stringBuilder.append(TextFormat.GOLD);
                        break;
                    case 2:
                        stringBuilder.append(TextFormat.GRAY);
                        break;
                    case 3:
                        stringBuilder.append(TextFormat.MATERIAL_COPPER);
                        break;
                    default:
                        stringBuilder.append(TextFormat.RESET);
                        break;
                }
                stringBuilder.append("[").append(i).append("] ")
                        .append(all.getKey())
                        .append(" - ")
                        .append(all.getValue())
                        .append("\n");
                i++;
            }
            stringBuilder.append("--------------").append("\nPersonal Mark Breakdown").append("\n");
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                stringBuilder.append(entry.getKey()).append(": ").append(entry.getValue())
                        .append("\n");
            }
            AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("本月全图跑酷榜单");
            simple.setContent(stringBuilder.toString());
            simple.showToPlayer(commandSender.asPlayer());
        } else {
            for (Map.Entry<String, Double> all : getDetailedMap().get("all").entrySet()) {
                GameAPI.getInstance().getLogger().info("[" + i + "] " + all.getKey() + " - " + all.getValue());
                i++;
            }
        }
        return false;
    }

    public static Map<String, Map<String, Double>> getDetailedMap() {
        Map<String, Double> map = new LinkedHashMap<>();
        Map<String, Map<String, Double>> details = new LinkedHashMap<>();
        for (Ranking ranking : GameEntityManager.rankingFactory.values()) {
            if (ranking instanceof SimpleRanking) {
                SimpleRanking simpleRanking = (SimpleRanking) ranking;
                if (simpleRanking.getGameName().equals("RecklessHero")) {
                    if (simpleRanking.getDataName().endsWith("_time")) {
                        ranking.refreshRankingData();
                        Set<? extends Map.Entry<String, ?>> list = ranking.getRankingData().entrySet();
                        String mapName = simpleRanking.getDataName().split("_")[0];
                        int score = 15;
                        switch (mapName) {
                            case "Clamber4":
                                for (Map.Entry<String, ?> entry : list) {
                                    if (score == 0) {
                                        break;
                                    }
                                    map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0d) + score * MULTIPLIER_C4);
                                    details.computeIfAbsent(entry.getKey(), s1 -> new LinkedHashMap<>()).put(simpleRanking.getDataName(), score * MULTIPLIER_C4);
                                    score--;
                                }
                                break;
                            case "Clamber3-Pro":
                                for (Map.Entry<String, ?> entry : list) {
                                    if (score == 0) {
                                        break;
                                    }
                                    map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0d) + score * MULTIPLIER_C3P);
                                    details.computeIfAbsent(entry.getKey(), s1 -> new LinkedHashMap<>()).put(simpleRanking.getDataName(), score * MULTIPLIER_C3P);
                                    score--;
                                }
                                break;
                            case "Clamber3":
                                for (Map.Entry<String, ?> entry : list) {
                                    if (score == 0) {
                                        break;
                                    }
                                    map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0d) + score * MULTIPLIER_C3);
                                    details.computeIfAbsent(entry.getKey(), s1 -> new LinkedHashMap<>()).put(simpleRanking.getDataName(), score * MULTIPLIER_C3);
                                    score--;
                                }
                                break;
                            case "Clamber2":
                                for (Map.Entry<String, ?> entry : list) {
                                    if (score == 0) {
                                        break;
                                    }
                                    map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0d) + score * MULTIPLIER_C2);
                                    details.computeIfAbsent(entry.getKey(), s1 -> new LinkedHashMap<>()).put(simpleRanking.getDataName(), score * MULTIPLIER_C2);
                                    score--;
                                }
                                break;
                            case "Clamber1-Pro":
                                for (Map.Entry<String, ?> entry : list) {
                                    if (score == 0) {
                                        break;
                                    }
                                    map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0d) + score * MULTIPLIER_C1P);
                                    details.computeIfAbsent(entry.getKey(), s1 -> new LinkedHashMap<>()).put(simpleRanking.getDataName(), score * MULTIPLIER_C1P);
                                    score--;
                                }
                                break;
                            case "Clamber1":
                                for (Map.Entry<String, ?> entry : list) {
                                    if (score == 0) {
                                        break;
                                    }
                                    map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0d) + score * MULTIPLIER_C1);
                                    details.computeIfAbsent(entry.getKey(), s1 -> new LinkedHashMap<>()).put(simpleRanking.getDataName(), score * MULTIPLIER_C1);
                                    score--;
                                }
                                break;
                            case "Dynamic-Pro":
                                for (Map.Entry<String, ?> entry : list) {
                                    if (score == 0) {
                                        break;
                                    }
                                    map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0d) + score * MULTIPLIER_DYNAMIC_PRO);
                                    details.computeIfAbsent(entry.getKey(), s1 -> new LinkedHashMap<>()).put(simpleRanking.getDataName(), score * MULTIPLIER_DYNAMIC_PRO);
                                    score--;
                                }
                                break;
                            case "Dynamic":
                                for (Map.Entry<String, ?> entry : list) {
                                    if (score == 0) {
                                        break;
                                    }
                                    map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0d) + score * MULTIPLIER_DYNAMIC);
                                    details.computeIfAbsent(entry.getKey(), s1 -> new LinkedHashMap<>()).put(simpleRanking.getDataName(), score * MULTIPLIER_DYNAMIC);
                                    score--;
                                }
                                break;
                            default:
                                // commandSender.sendMessage("Default score process for: " + simpleRanking.getDataName());
                                for (Map.Entry<String, ?> entry : list) {
                                    if (score == 0) {
                                        break;
                                    }
                                    map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0d) + score * MULTIPLIER_DEFAULT);
                                    details.computeIfAbsent(entry.getKey(), s1 -> new LinkedHashMap<>()).put(simpleRanking.getDataName(), score * MULTIPLIER_DEFAULT);
                                    score--;
                                }
                                break;
                        }
                    }
                }
            }
        }

        List<Map.Entry<String, Double>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparingDouble(Map.Entry::getValue));
        Collections.reverse(entries);
        for (Map.Entry<String, Double> entry : entries) {
            details.computeIfAbsent("all", s1 -> new LinkedHashMap<>()).put(entry.getKey(), entry.getValue());
        }
        return details;
    }
}
