package gameapi.activity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.utils.TextFormat;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import gameapi.manager.data.PlayerGameDataManager;

/**
 * @author glorydark
 */
public class ActivityPVE241001 {

    public static final String activityId = "Activity_MobArena_1001";

    public static void showPVEActivityForm(Player player) {
        int finishedRound = PlayerGameDataManager.getPlayerGameData("MobArena", "A城足球场遗址_round", player.getName(), 0);
        boolean epicClaimed = PlayerGameDataManager.getPlayerGameData(activityId, "epic_award", player.getName(), 0L) != 0L;
        boolean goldClaimed = PlayerGameDataManager.getPlayerGameData(activityId, "gold_award", player.getName(), 0L) != 0L;
        boolean silverClaimed = PlayerGameDataManager.getPlayerGameData(activityId, "silver_award", player.getName(), 0L) != 0L;
        boolean bronzeClaimed = PlayerGameDataManager.getPlayerGameData(activityId, "bronze_award", player.getName(), 0L) != 0L;
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("丧尸围城限时挑战", "您已通关第 " + finishedRound + " 关\n" + TextFormat.YELLOW + "* 完成A市足球场遗址的对应场次可得限定称号哦！");
        if (epicClaimed) {
            simple.addButton(new ElementButton("§g§l前三名通关20关\n" + ActivityMain.STATUS_CLAIMED));
        } else {
            if (player.getName().equals("pxx3694072") || player.getName().equals("walk3257") || player.getName().equals("Wissacy")) {
                simple.addButton(
                        new ResponsiveElementButton("§g§l前三名通关20关\n" + ActivityMain.STATUS_UNCLAIMED)
                                .onRespond(player1 -> {
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + player1.getName() + " 特级怪物猎人 1209600000");
                                    PlayerGameDataManager.setPlayerGameData(activityId, "epic_award", player1.getName(), System.currentTimeMillis());
                                })
                );
            } else {
                simple.addButton(new ElementButton("§g§l前三名通关20关\n" + ActivityMain.STATUS_NOT_QUALIFIED));
            }
        }
        if (goldClaimed) {
            simple.addButton(new ElementButton("§g§l通过20关\n" + ActivityMain.STATUS_CLAIMED));
        } else {
            if (finishedRound >= 20) {
                simple.addButton(
                        new ResponsiveElementButton("§g§l通过20关\n" + ActivityMain.STATUS_UNCLAIMED)
                                .onRespond(player1 -> {
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + player1.getName() + " 金牌怪物猎人 1209600000");
                                    PlayerGameDataManager.setPlayerGameData(activityId, "gold_award", player1.getName(), System.currentTimeMillis());
                                })
                );
            } else {
                simple.addButton(new ElementButton("§g§l通过20关\n" + ActivityMain.STATUS_NOT_QUALIFIED));
            }
        }
        if (silverClaimed) {
            simple.addButton(new ElementButton("§2§l通过15关\n" + ActivityMain.STATUS_CLAIMED));
        } else {
            if (finishedRound >= 15) {
                simple.addButton(
                        new ResponsiveElementButton("§2§l通过15关\n" + ActivityMain.STATUS_UNCLAIMED)
                                .onRespond(player1 -> {
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + player1.getName() + " 银牌怪物猎人 1209600000");
                                    PlayerGameDataManager.setPlayerGameData(activityId, "silver_award", player1.getName(), System.currentTimeMillis());
                                })
                );
            } else {
                simple.addButton(new ElementButton("§2§l通过15关\n" + ActivityMain.STATUS_NOT_QUALIFIED));
            }
        }
        if (bronzeClaimed) {
            simple.addButton(new ElementButton("§1§l通过10关\n" + ActivityMain.STATUS_CLAIMED));
        } else {
            if (finishedRound >= 10) {
                simple.addButton(
                        new ResponsiveElementButton("§1§l通过10关\n" + ActivityMain.STATUS_UNCLAIMED)
                                .onRespond(player1 -> {
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + player1.getName() + " 铜牌怪物猎人 1209600000");
                                    PlayerGameDataManager.setPlayerGameData(activityId, "bronze_award", player1.getName(), System.currentTimeMillis());
                                })
                );
            } else {
                simple.addButton(new ElementButton("§1§l通过10关\n" + ActivityMain.STATUS_NOT_QUALIFIED));
            }
        }
        simple.showToPlayer(player);
    }
}
