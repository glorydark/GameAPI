package gameapi.activity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.utils.TextFormat;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import gameapi.manager.data.PlayerGameDataManager;
import gameapi.tools.SmartTools;

/**
 * @author glorydark
 */
public class ActivityParkourMonthlyCompetition {

    public static final String activityId = "Activity_Parkour_241001";

    public static final String content = "为促进服务器玩家跑酷水平提升，以赛代练，以赛促学，特举办2024年10月大鸟测试服跑酷赛。" +
            "\n" +
            "1. 参与对象: 测试服全体玩家" +
            "\n" +
            "2. 比赛时间：2024年10月3日 - 2024年10月20日" +
            "\n" +
            "3. 比赛形式：线上比赛，活动时间内均可开启挑战，每人最多3次挑战机会，以最好成绩为准。" +
            "\n" +
            "4. 奖项（暂定）" +
            "\n" +
            "- 冠、亚、季军：专属称号 全服最强pker * 7 - 21天，对应奖项纪念成就 * 1" +
            "\n" +
            "- 参与奖：称号 跑酷探索者 * 7 - 21天（根据跑酷用时发放）" +
            "\n" +
            "5. 注意事项：玩家需要签署诚信承诺书，本次比赛将采取相对严格的监督方式，将实时录制玩家的跑酷情况，" +
            "记录玩家的Motion, Movement, Ping等数据，服务器有权取消违规成绩与违规玩家的参与资格，" +
            "请保证网络ping不大于50，以避免出现赛后系统、赛事人员造成误判。";

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
                new ResponsiveElementButton("确认")
                        .onRespond(player1 -> Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "drh competition \"" + player1.getName() + "\""))
        );
        simple.showToPlayer(player);
    }

    public static void showMainForm(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("2024年10月跑酷月赛", "活动时间: 2024年10月3日00:00 - 2024年10月20日23:59");
        int joinTimes = PlayerGameDataManager.getPlayerGameData("Activity_Parkour_241001", "join_times", player.getName(), 0);
        if (System.currentTimeMillis() <= 1729439999000L) {
            if (joinTimes >= 3) {
                long time = Long.parseLong(PlayerGameDataManager.getPlayerGameData("Activity_Parkour_241001", "time", player.getName(), "0"));
                simple.addButton(
                        new ElementButton("立即开始挑战\n" + TextFormat.YELLOW + "[" + (time > 0? SmartTools.timeMillisToString(time): "NAN") + "]")
                );
            } else {
                simple.addButton(
                        new ResponsiveElementButton("立即开始挑战\n" + TextFormat.GREEN + "[您还有" + TextFormat.YELLOW + (3 - joinTimes) + TextFormat.GREEN + "次挑战机会]")
                                .onRespond(ActivityParkourMonthlyCompetition::showCompetitionCheckBox)
                );
                }
        } else {
            simple.addButton(new ElementButton("立即开始挑战\n" + TextFormat.GRAY + "[已结束]"));
        }
        simple.addButton(
                new ResponsiveElementButton("比赛结果\n" + TextFormat.RED + "[全部比赛结束可查看]")
        );
        /*
        simple.addButton(
                new ResponsiveElementButton("比赛通知")
                        .onRespond(ActivityParkourMonthlyCompetition::showCompetitionFileInfo)
        );
         */
        simple.showToPlayer(player);
    }

    public static void showCompetitionFileInfo(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("比赛文件", content);
        simple.showToPlayer(player);
    }
}
