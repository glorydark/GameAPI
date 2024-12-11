package gameapi.manager.data.activity.internal;

import cn.nukkit.utils.ConfigSection;
import gameapi.GameAPI;
import gameapi.manager.data.GameActivityManager;
import gameapi.manager.data.activity.ActivityData;
import gameapi.manager.data.activity.AwardData;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author glorydark
 */
public class ActivityRingParkourNewMap {
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
}
