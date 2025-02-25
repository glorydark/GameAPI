package gameapi.test.newactivity;

import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import gameapi.manager.data.GameActivityManager;
import gameapi.manager.data.activity.ActivityData;
import gameapi.manager.data.activity.AwardData;

/**
 * @author glorydark
 */
public class ActivityWinterComing20241110 {
    public static void registerWinterComing20241110() {
        ActivityData activityData = new ActivityData(
                "activity_winter_coming_20241110",
                "初冬送礼",
                "无条件领取初冬称号奖励",
                "2024-11-10 00-00-00",
                "2024-11-20 00-00-00"
        );
        activityData.addAward(
                new AwardData(activityData, "初冬称号 <邂逅初冬> 14d")
                        .checkClaimStatus((activityData1, player) -> activityData1.getData(player.getName()).getBoolean("claimed", false))
                        .claim((activityData1, player) -> {
                            ConfigSection section = activityData1.getData(player.getName());
                            section.set("claimed", true);
                            activityData1.setData(player.getName(), section);
                        })
                        .command("prefix give " + AwardData.PLAYER_REPLACEMENT + " 邂逅初冬 1209600000")
                        .message(TextFormat.GREEN + "您已成功领取 <初冬福利>!")
        );

        GameActivityManager.registerActivity(activityData);
    }
}
