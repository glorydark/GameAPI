package gameapi.activity;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;

/**
 * @author glorydark
 */
public class ActivityMain {

    protected static String STATUS_CLAIMED = "§6[已领取]";
    protected static String STATUS_UNCLAIMED = "§a[可领取]";
    protected static String STATUS_NOT_QUALIFIED = "§c[未满足条件]";

    public static void showActivityMain(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("活动界面", "称号奖励请前往测试服须知NPC处装备");
        simple.addButton(
                new ResponsiveElementButton("§6§l11月测试服跑酷公开赛\n§b活动时间: 11月7日0:00 - 11月16日23:59")
                        .onRespond(ActivityParkourMonthlyCompetition202411::showMainForm)
        );
        /*
        simple.addButton(
                new ResponsiveElementButton("§6§l10月测试服跑酷公开赛\n§b活动时间: 10月3日 - 10月20日23:59")
                        .onRespond(ActivityParkourMonthlyCompetition::showMainForm)
        );
         */
        /*
        simple.addButton(
                new ResponsiveElementButton("§c§l丧尸围城限时挑战\n§b活动时间: 10月1日 - 10月7日")
                        .onRespond(ActivityPVE241001::showPVEActivityForm)
        );
         */
        simple.addButton(
                new ResponsiveElementButton("§e§lBedFight挑战\n§r§8[10月28日 - 11月13日]")
                        .onRespond(ActivityBedFight20241028::showActivityForm)
        );
        simple.addButton(
                new ResponsiveElementButton("§a§l主城活动")
                        .onRespond(ActivityLobbyTask::showActivityForm)
        );
        simple.addButton(
                new ElementButton("丧尸围城限时挑战\n§r§8[已结束]")
        );
        simple.addButton(
                new ElementButton("§6§l10月测试服跑酷公开赛\n§r§8[已结束]")
        );
        simple.showToPlayer(player);
    }
}
