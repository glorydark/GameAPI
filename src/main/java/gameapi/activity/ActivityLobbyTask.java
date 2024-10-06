package gameapi.activity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.form.element.ElementButton;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import gameapi.manager.data.PlayerGameDataManager;

/**
 * @author glorydark
 */
public class ActivityLobbyTask {

    public static final String activityId = "Activity_Lobby_Task_202410";

    public static void showActivityForm(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("常规任务");
        boolean parkourClaimed = PlayerGameDataManager.getPlayerGameData(activityId, "parkour_claimed", player.getName(), 0L) != 0L;
        boolean parkourFinishStatus = PlayerGameDataManager.getPlayerGameData(activityId, "parkkour_finished", player.getName(), false);
        if (parkourClaimed) {
            simple.addButton(new ElementButton("§g§l完成主城跑酷\n" + ActivityMain.STATUS_CLAIMED));
        } else {
            if (parkourFinishStatus) {
                simple.addButton(
                        new ResponsiveElementButton("§g§l完成主城跑酷\n" + ActivityMain.STATUS_UNCLAIMED)
                                .onRespond(player1 -> {
                                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), "prefix give " + player1.getName() + " 跑酷苦手 1209600000");
                                    PlayerGameDataManager.setPlayerGameData(activityId, "parkour_claimed", player1.getName(), System.currentTimeMillis());
                                })
                );
            } else {
                simple.addButton(new ElementButton("§g§l完成主城跑酷\n" + ActivityMain.STATUS_NOT_QUALIFIED));
            }
        }
        simple.showToPlayer(player);
    }
}
