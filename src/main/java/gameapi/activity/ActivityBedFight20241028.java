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
public class ActivityBedFight20241028 {

    public static final String activityId = "Activity_BedFight_20241028";

    public static void showActivityForm(Player player) {
        int win = PlayerGameDataManager.getPlayerGameData("BedFight", "Greenery-Simple_win", player.getName(), 0);
        boolean goldClaimed = PlayerGameDataManager.getPlayerGameData(activityId, "gold_award", player.getName(), 0L) != 0L;
        boolean silverClaimed = PlayerGameDataManager.getPlayerGameData(activityId, "silver_award", player.getName(), 0L) != 0L;
        boolean bronzeClaimed = PlayerGameDataManager.getPlayerGameData(activityId, "bronze_award", player.getName(), 0L) != 0L;

        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("BedFight限时挑战", "您已胜利" + win + "局\n");
        if (goldClaimed) {
            simple.addButton(new ElementButton("§1§l胜利5局\n" + ActivityMain.STATUS_CLAIMED));
        } else {
            if (win >= 5) {
                simple.addButton(
                        new ResponsiveElementButton("§1§l胜利5局\n" + ActivityMain.STATUS_UNCLAIMED)
                                .onRespond(player1 -> {
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + player1.getName() + " 金牌BF玩家 1209600000");
                                    PlayerGameDataManager.setPlayerGameData(activityId, "gold_award", player1.getName(), System.currentTimeMillis());
                                })
                );
            } else {
                simple.addButton(new ElementButton("§1§l胜利5局\n" + ActivityMain.STATUS_NOT_QUALIFIED));
            }
        }
        if (silverClaimed) {
            simple.addButton(new ElementButton("§1§l胜利3局\n" + ActivityMain.STATUS_CLAIMED));
        } else {
            if (win >= 3) {
                simple.addButton(
                        new ResponsiveElementButton("§1§l胜利3局\n" + ActivityMain.STATUS_UNCLAIMED)
                                .onRespond(player1 -> {
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + player1.getName() + " 银牌BF玩家 1209600000");
                                    PlayerGameDataManager.setPlayerGameData(activityId, "silver_award", player1.getName(), System.currentTimeMillis());
                                })
                );
            } else {
                simple.addButton(new ElementButton("§1§l胜利3局\n" + ActivityMain.STATUS_NOT_QUALIFIED));
            }
        }
        if (bronzeClaimed) {
            simple.addButton(new ElementButton("§1§l胜利1局\n" + ActivityMain.STATUS_CLAIMED));
        } else {
            if (win >= 1) {
                simple.addButton(
                        new ResponsiveElementButton("§1§l胜利1局\n" + ActivityMain.STATUS_UNCLAIMED)
                                .onRespond(player1 -> {
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + player1.getName() + " 铜牌BF玩家 1209600000");
                                    PlayerGameDataManager.setPlayerGameData(activityId, "bronze_award", player1.getName(), System.currentTimeMillis());
                                })
                );
            } else {
                simple.addButton(new ElementButton("§1§l胜利1局\n" + ActivityMain.STATUS_NOT_QUALIFIED));
            }
        }
        simple.showToPlayer(player);
    }
}
