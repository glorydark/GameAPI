package gameapi.manager.data.activity;

import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import gameapi.manager.data.GameActivityManager;
import gameapi.manager.data.PlayerGameDataManager;

/**
 * @author glorydark
 */
public class ActivityRegistry {

    public static void init() {
        registerClamber4NewMap();
        registerWinterComing20241110();
    }

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

    public static void registerClamber4NewMap() {
        ActivityData activityData = new ActivityData(
                "activity_parkour_c4_new_map_20241117",
                "Clamber 4 新图挑战",
                "完成任务有机会获得称号奖励哦！",
                "2024-11-17 00-00-00",
                "2024-12-1 00-00-00"
        );
        activityData.addAward(
                new AwardData(activityData, "§c§l在10分钟内通关")
                        .checkClaimStatus((activityData1, player) -> activityData1.getData(player.getName()).getBoolean("claimed_special", false))
                        .claim((activityData1, player) -> {
                            ConfigSection section = activityData1.getData(player.getName());
                            section.set("claimed_special", true);
                            activityData1.setData(player.getName(), section);
                        })
                        .checkFinish((activityData2, player) -> PlayerGameDataManager.getPlayerGameData("RecklessHero", "Clamber4_time", player, 600001) <= 600000)
                        .command("prefix give " + AwardData.PLAYER_REPLACEMENT + " 特级跑酷玩家 1209600000")
                        .message(TextFormat.GREEN + "您已成功领取 <特级跑酷玩家>!")
        );
        activityData.addAward(
                new AwardData(activityData, "§6§l在15分钟内通关")
                        .checkClaimStatus((activityData1, player) -> activityData1.getData(player.getName()).getBoolean("claimed_gold", false))
                        .claim((activityData1, player) -> {
                            ConfigSection section = activityData1.getData(player.getName());
                            section.set("claimed_gold", true);
                            activityData1.setData(player.getName(), section);
                        })
                        .checkFinish((activityData2, player) -> PlayerGameDataManager.getPlayerGameData("RecklessHero", "Clamber4_time", player, 900001) <= 900000)
                        .command("prefix give " + AwardData.PLAYER_REPLACEMENT + " 金牌跑酷玩家 1209600000")
                        .message(TextFormat.GREEN + "您已成功领取 <金牌跑酷玩家>!")
        );
        activityData.addAward(
                new AwardData(activityData, "§3§l在30分钟内通关")
                        .checkClaimStatus((activityData1, player) -> activityData1.getData(player.getName()).getBoolean("claimed_silver", false))
                        .claim((activityData1, player) -> {
                            ConfigSection section = activityData1.getData(player.getName());
                            section.set("claimed_silver", true);
                            activityData1.setData(player.getName(), section);
                        })
                        .checkFinish((activityData2, player) -> PlayerGameDataManager.getPlayerGameData("RecklessHero", "Clamber4_time", player, 1800001) <= 1800000)
                        .command("prefix give " + AwardData.PLAYER_REPLACEMENT + " 银牌跑酷玩家 1209600000")
                        .message(TextFormat.GREEN + "您已成功领取 <银牌跑酷玩家>!")
        );
        activityData.addAward(
                new AwardData(activityData, "§1§l完成比赛")
                        .checkClaimStatus((activityData1, player) -> activityData1.getData(player.getName()).getBoolean("claimed_bronze", false))
                        .claim((activityData1, player) -> {
                            ConfigSection section = activityData1.getData(player.getName());
                            section.set("claimed_bronze", true);
                            activityData1.setData(player.getName(), section);
                        })
                        .checkFinish((activityData2, player) -> PlayerGameDataManager.getPlayerGameData("RecklessHero", "Clamber4_win", player, 0) > 0)
                        .command("prefix give " + AwardData.PLAYER_REPLACEMENT + " 铜牌跑酷玩家 1209600000")
                        .message(TextFormat.GREEN + "您已成功领取 <铜牌跑酷玩家>!")
        );

        GameActivityManager.registerActivity(activityData);
    }
}
