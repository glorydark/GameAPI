package gameapi.manager.data.activity;

import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.manager.data.GameActivityManager;
import gameapi.manager.data.PlayerGameDataManager;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author glorydark
 */
public class ActivityRegistry {

    public static void init() {
        registerRingParkourNewMap();
        // registerClamber4NewMap();
        // registerWinterComing20241110();
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

    public static void registerRingParkourNewMap() {
        ActivityData activityData = new ActivityData(
                "activity_parkour_ring_pk_new_map_20241206",
                "12月跑酷月赛模拟赛奖励领取",
                "前三名抽奖界面！",
                "2024-12-6 00-00-00",
                "2024-12-8 00-00-00"
        );
        activityData.addAward(
                new AwardData(activityData, "§c§l冠军抽奖")
                        .checkClaimStatus((activityData1, player) -> activityData1.getData(player.getName()).getBoolean("claimed_first_finish", false))
                        .claim((activityData1, player) -> {
                            ConfigSection section = activityData1.getData(player.getName());
                            section.set("claimed_first_finish", true);
                            activityData1.setData(player.getName(), section);
                            switch (ThreadLocalRandom.current().nextInt(4)) {
                                case 0:
                                    player.sendMessage("§a恭喜你获得 §e8.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 niangaoqiqi 获得 §e8.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 1:
                                    player.sendMessage("§a恭喜你获得 §e6.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 niangaoqiqi 获得 §e6.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 2:
                                    player.sendMessage("§a恭喜你获得 §e3.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 niangaoqiqi 获得 §e3.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 3:
                                    player.sendMessage("§c很抱歉，您未能抽中任何奖品!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 niangaoqiqi 抽中了空气！");
                                    break;
                            }
                        })
                        .checkFinish((activityData2, player) -> player.getName().equals("niangaoqiqi"))
        );
        activityData.addAward(
                new AwardData(activityData, "§c§l亚军抽奖")
                        .checkClaimStatus((activityData1, player) -> activityData1.getData(player.getName()).getBoolean("claimed_second_finish", false))
                        .claim((activityData1, player) -> {
                            ConfigSection section = activityData1.getData(player.getName());
                            section.set("claimed_second_finish", true);
                            activityData1.setData(player.getName(), section);
                            switch (ThreadLocalRandom.current().nextInt(4)) {
                                case 0:
                                    player.sendMessage("§a恭喜你获得 §e3.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 pxx3694072 获得 §e3.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 1:
                                    player.sendMessage("§a恭喜你获得 §e1.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 pxx3694072 获得 §e1.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 2:
                                    player.sendMessage("§a恭喜你获得 §e0.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 pxx3694072 获得 §e0.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 3:
                                    player.sendMessage("§c很抱歉，您未能抽中任何奖品!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 pxx3694072 抽中了空气！");
                                    break;
                            }
                        })
                        .checkFinish((activityData2, player) -> player.getName().equals("pxx3694072"))
        );
        activityData.addAward(
                new AwardData(activityData, "§c§l季军抽奖")
                        .checkClaimStatus((activityData1, player) -> activityData1.getData(player.getName()).getBoolean("claimed_third_finish", false))
                        .claim((activityData1, player) -> {
                            ConfigSection section = activityData1.getData(player.getName());
                            section.set("claimed_third_finish", true);
                            activityData1.setData(player.getName(), section);
                            switch (ThreadLocalRandom.current().nextInt(4)) {
                                case 0:
                                    player.sendMessage("§a恭喜你获得 §e1.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 mcfatienr 获得 §e1.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 1:
                                    player.sendMessage("§a恭喜你获得 §e0.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 mcfatienr 获得 §e0.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 2:
                                    player.sendMessage("§a恭喜你获得 §e0.66 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 mcfatienr 获得 §e0.66 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 3:
                                    player.sendMessage("§c很抱歉，您未能抽中任何奖品!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 mcfatienr 抽中了空气！");
                                    break;
                            }
                        })
                        .checkFinish((activityData2, player) -> player.getName().equals("mcfatienr"))
        );
        GameActivityManager.registerActivity(activityData);
    }

    public static void registerClamber4NewMap() {
        ActivityData activityData = new ActivityData(
                "activity_parkour_c4_new_map_20241117",
                "Clamber 4 新图挑战",
                "完成任务有机会获得称号奖励哦！",
                "2024-11-17 00-00-00",
                "2024-12-9 00-00-00"
        );
        activityData.addAward(
                new AwardData(activityData, "§c§l首位sub10通关")
                        .checkClaimStatus((activityData1, player) -> activityData1.getData(player.getName()).getBoolean("claimed_first_finish", false))
                        .claim((activityData1, player) -> {
                            ConfigSection section = activityData1.getData(player.getName());
                            section.set("claimed_first_finish", true);
                            activityData1.setData(player.getName(), section);
                            switch (ThreadLocalRandom.current().nextInt(7)) {
                                case 0:
                                    player.sendMessage("§a恭喜你获得 §e8.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 niangaoqiqi 获得 §e8.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 1:
                                    player.sendMessage("§a恭喜你获得 §e专属限定成就§a");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 niangaoqiqi 获得 §e专属限定成就§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 2:
                                    player.sendMessage("§a恭喜你获得 §e6.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 niangaoqiqi 获得 §e6.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 3:
                                    player.sendMessage("§a恭喜你获得 §e1.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 niangaoqiqi 获得 §e1.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 4:
                                    player.sendMessage("§a恭喜你获得 §e4.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 niangaoqiqi 获得 §e4.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 5:
                                    player.sendMessage("§a恭喜你获得 §e18.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    GameAPI.getInstance().getLogger().info("§a恭喜 niangaoqiqi 获得 §e18.88 RMB§a，请尽快实名QQ支付，以便发放!");
                                    break;
                                case 6:
                                    player.sendMessage("§c很抱歉，您未能抽中任何奖品!");
                                    break;
                            }
                        })
                        .checkFinish((activityData2, player) -> player.getName().equals("niangaoqiqi") || player.getName().equals("BizarreDark"))
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
