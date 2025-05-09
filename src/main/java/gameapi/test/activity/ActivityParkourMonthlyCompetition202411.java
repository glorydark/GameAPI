package gameapi.test.activity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.utils.TextFormat;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import gameapi.manager.data.PlayerGameDataManager;
import gameapi.ranking.Ranking;
import gameapi.ranking.RankingFormat;
import gameapi.ranking.RankingSortSequence;
import gameapi.ranking.simple.SimpleRanking;
import gameapi.tools.CalendarTools;
import gameapi.tools.SmartTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author glorydark
 */
@Deprecated
public class ActivityParkourMonthlyCompetition202411 {

    public static final String activityId = "Activity_Parkour_241107"; // todo

    public static final Ranking ranking = new SimpleRanking(Ranking.getRankingValueType("long_to_time"), activityId, "time", "比赛排行", "暂无数据", new RankingFormat(), RankingSortSequence.ASCEND, 30);

    public static void showCompetitionCheckBox(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("赛事承诺书");
        String content = new StringBuilder()
                .append("参考延迟: ").append(player.getPing()).append("ms")
                .append("\n")
                .append("1. 本次比赛请保持网络稳定，比赛延迟、单刻/单秒位移、玩家Motion等数据将会实时记录，用于机器复现和核实，将作为成绩有效的重要依据，请玩家做好赛前设备调试。")
                .append("\n")
                .append("2. 本次比赛全程录像，且保存对局记录，录像、数据未到比赛结束将不对外公开，参赛选手请自行保护自身的成绩隐私，比赛全部结束后，经官方人员审核，将公布赛事成绩。")
                .append("\n")
                .append("3. 比赛中，玩家应尊重比赛规则、不使用非法手段取得成绩，违者将取消参赛资格，严重者将加以禁赛，所有取消成绩的情况将在赛后公示。")
                .append("\n")
                .append("4. " + TextFormat.RED + "点击确认后，即默认玩家已做出以上承诺，且玩家已完成设备调试，立即扣除比赛剩余参与次数，随后进入比赛。")
                .toString();
        simple.setContent(content);
        simple.addButton(
                new ResponsiveElementButton("我已知晓")
                        .onRespond(player1 -> Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "drh competition \"" + player1.getName() + "\""))
        );
        simple.showToPlayer(player);
    }

    public static void showMainForm(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("2024年11月跑酷月赛", "活动时间: 2024年11月7日00:00 - 2024年11月16日23:59\n比赛地图: AdventureDelight-Easy"); // todo
        int joinTimes = PlayerGameDataManager.getPlayerGameData(activityId, "join_times", player.getName(), 0);
        long currentMillis = System.currentTimeMillis();
        long time = Long.parseLong(PlayerGameDataManager.getPlayerGameData(activityId, "time", player.getName(), "0"));
        if (currentMillis < CalendarTools.getDate("2024-11-07 00-00-00").getTime()) { // todo
            simple.addButton(new ElementButton(TextFormat.RED + "[未开始]"));
        } else if (currentMillis > CalendarTools.getDate("2024-11-17 00-00-00").getTime()) { // todo
            simple.addButton(new ElementButton(TextFormat.MATERIAL_GOLD + "[比赛已结束]\n" + TextFormat.YELLOW + "[" + (time > 0 ? SmartTools.timeMillisToString(time) : "NAN") + "]"));
        } else {
            if (joinTimes >= 3) {
                simple.addButton(
                        new ElementButton(TextFormat.RED + "已完赛\n" + TextFormat.YELLOW + "[" + (time > 0 ? SmartTools.timeMillisToString(time) : "NAN") + "]")
                );
            } else {
                simple.addButton(
                        new ResponsiveElementButton("立即开始挑战\n" + TextFormat.GREEN + "[您还有" + TextFormat.YELLOW + (3 - joinTimes) + TextFormat.GREEN + "次挑战机会]")
                                .onRespond(ActivityParkourMonthlyCompetition202411::showCompetitionCheckBox)
                );
            }
        }
        if (PlayerGameDataManager.getPlayerGameData(activityId, "activity_award_distributed", "is_operated", false)){
            simple.addButton(
                    new ResponsiveElementButton("获奖公示")
                            .onRespond(ActivityParkourMonthlyCompetition202411::showCompetitionFinalRank)
            );
        } else {
            simple.addButton(
                    new ResponsiveElementButton("实时排行")
                            .onRespond(ActivityParkourMonthlyCompetition202411::showCompetitionRank)
            );
        }
        if (player.isOp()) {
            simple.addButton(
                    new ResponsiveElementButton("管理员入口")
                            .onRespond(ActivityParkourMonthlyCompetition202411::showAdminRankInfo)
            );
        }
        /*
        simple.addButton(
                new ResponsiveElementButton("比赛通知")
                        .onRespond(ActivityParkourMonthlyCompetition::showCompetitionFileInfo)
        );
         */
        simple.showToPlayer(player);
    }

    public static void showAdminRankInfo(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("赛事管理员入口");
        int rank = 1;
        StringBuilder stringBuilder = new StringBuilder();
        ranking.refreshRankingData();
        Set<? extends Map.Entry<String, ?>> list = ranking.getRankingCache().entrySet();
        List<String> participatingPlayers = new ArrayList<>(PlayerGameDataManager.getPlayerAllGameData(activityId, "join_times").keySet());
        for (Map.Entry<String, ?> stringEntry : list) {
            participatingPlayers.remove(stringEntry.getKey());
        }
        int maxCount = list.size();
        int gold = 0;
        int silver = 0;
        String champion = "";
        String runnerUp = "";
        String secondRunnerUp = "";
        List<String> golds = new ArrayList<>();
        List<String> silvers = new ArrayList<>();
        List<String> bronzes = new ArrayList<>();
        for (Map.Entry<String, ?> entry : list) {
            if (rank == 1) {
                stringBuilder.append(TextFormat.GOLD).append("[冠军] ").append(TextFormat.RESET);
                champion = entry.getKey();
            } else if (rank == 2) {
                stringBuilder.append(TextFormat.YELLOW).append("[亚军] ").append(TextFormat.RESET);
                runnerUp = entry.getKey();
            } else if (rank == 3) {
                stringBuilder.append(TextFormat.GREEN).append("[季军] ").append(TextFormat.RESET);
                secondRunnerUp = entry.getKey();
            } else if ((double) rank / maxCount < 0.2 || gold == 0) {
                stringBuilder.append("[金奖] ");
                gold += 1;
                golds.add(entry.getKey());
            } else if ((double) rank / maxCount < 0.5 || silver == 0) {
                stringBuilder.append("[银奖] ");
                silver += 1;
                silvers.add(entry.getKey());
            } else {
                stringBuilder.append("[铜奖] ");
                bronzes.add(entry.getKey());
            }
            stringBuilder.append("[").append(rank).append("] ").append(entry.getKey()).append("\n");
            rank++;
        }
        for (String participatingPlayer : participatingPlayers) {
            stringBuilder.append("[参与奖] ").append("[").append(rank).append("] ").append(participatingPlayer).append("\n");
            rank++;
        }
        simple.setContent(stringBuilder.toString());
        String finalChampion = champion;
        String finalRunnerUp = runnerUp;
        String finalSecondRunnerUp = secondRunnerUp;
        if (PlayerGameDataManager.getPlayerGameData(activityId, "send", "activity_award_distributed", false)) {
            simple.addButton(new ElementButton(TextFormat.RED + "[奖励已发放]"));
        } else {
            simple.addButton(
                    new ResponsiveElementButton("确认成绩并发放奖励")
                            .onRespond(player1 -> {
                                if (!PlayerGameDataManager.getPlayerGameData(activityId, "activity_award_distributed", "is_operated", false)){
                                    PlayerGameDataManager.setPlayerGameData(activityId, "activity_award_distributed", "is_operated", true);
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + "\"" + finalChampion + "\" 跑酷月赛冠军 2592000000");
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "gameapi giveachievement " + "\"" + finalChampion + "\" 2024年11月跑酷月赛 competition_202411_champion 比赛奖励发放");

                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + "\"" + finalRunnerUp + "\" 跑酷月赛亚军 2592000000");
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "gameapi giveachievement " + "\"" + finalRunnerUp + "\" 2024年11月跑酷月赛 competition_202411_runner-up 比赛奖励发放");

                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + "\"" + finalSecondRunnerUp + "\" 跑酷月赛季军 2592000000");
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "gameapi giveachievement " + "\"" + finalSecondRunnerUp + "\" 2024年11月跑酷月赛 competition_202411_second-runner-up 比赛奖励发放");
                                    for (String string : golds) {
                                        Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + "\"" + string + "\" 跑酷月赛金奖 2592000000");
                                        Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "gameapi giveachievement " + "\"" + string + "\" 2024年11月跑酷月赛 competition_202411_gold_medal 比赛奖励发放");
                                    }
                                    for (String string : silvers) {
                                        Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + "\"" + string + "\" 跑酷月赛银奖 2592000000");
                                        Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "gameapi giveachievement " + "\"" + string + "\" 2024年11月跑酷月赛 competition_202411_silver_medal 比赛奖励发放");
                                    }
                                    for (String string : bronzes) {
                                        Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + "\"" + string + "\" 跑酷月赛铜奖 2592000000");
                                        Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "gameapi giveachievement " + "\"" + string + "\" 2024年11月跑酷月赛 competition_202411_bronze_medal 比赛奖励发放");
                                    }
                                    for (String participatingPlayer : participatingPlayers) {
                                        Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + "\"" + participatingPlayer + "\" 跑酷爱好者 2592000000");
                                    }
                                    player1.sendMessage(TextFormat.GREEN + "奖励已全部发放完毕！");
                                }
                            })
            );
        }
        simple.showToPlayer(player);
    }

    public static void showCompetitionRank(Player player) {
        ranking.refreshRankingData();
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("实时排行", ranking.getDisplayContent());
        simple.setContent(simple.getContent() + "\n" + TextFormat.RED + "注：管理员当前暂未确认奖项，具体请以公示为准");
        simple.showToPlayer(player);
    }

    public static void showCompetitionFinalRank(Player player) {
        ranking.refreshRankingData();
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("获奖公示", ranking.getDisplayContent());
        int rank = 1;
        StringBuilder stringBuilder = new StringBuilder();
        ranking.refreshRankingData();
        Set<? extends Map.Entry<String, ?>> list = ranking.getRankingCache().entrySet();
        List<String> participatingPlayers = new ArrayList<>(PlayerGameDataManager.getPlayerAllGameData(activityId, "times").keySet());
        for (Map.Entry<String, ?> stringEntry : list) {
            participatingPlayers.remove(stringEntry.getKey());
        }
        int maxCount = list.size();
        int gold = 0;
        int silver = 0;
        for (Map.Entry<String, ?> entry : list) {
            if (rank == 1) {
                stringBuilder.append(TextFormat.GOLD).append("[冠军] ").append(TextFormat.RESET);
            } else if (rank == 2) {
                stringBuilder.append(TextFormat.YELLOW).append("[亚军] ").append(TextFormat.RESET);
            } else if (rank == 3) {
                stringBuilder.append(TextFormat.GREEN).append("[季军] ").append(TextFormat.RESET);
            } else if ((double) rank / maxCount < 0.2 || gold == 0) {
                stringBuilder.append("[金奖] ");
                gold += 1;
            } else if ((double) rank / maxCount < 0.5 || silver == 0) {
                stringBuilder.append("[银奖] ");
                silver += 1;
            } else {
                stringBuilder.append("[铜奖] ");
            }
            stringBuilder.append("[").append(rank).append("] ").append(entry.getKey()).append("\n");
            rank++;
        }
        for (String participatingPlayer : participatingPlayers) {
            stringBuilder.append("[参与奖] ").append("[").append(rank).append("] ").append(participatingPlayer).append("\n");
            rank++;
        }
        simple.setContent(stringBuilder.toString());
        simple.setContent(simple.getContent() + "\n" + TextFormat.YELLOW + "注：此名单已由管理员确认，如有异议，请在11月17日23:59前提出！");
        simple.showToPlayer(player);
    }
}
