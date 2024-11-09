package gameapi.commands.sub.test;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.ConfigSection;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.data.GameActivityManager;
import gameapi.manager.data.activity.ActivityData;
import gameapi.manager.data.activity.AwardData;

/**
 * @author glorydark
 */
public class FunctionNewActivityCommand extends EasySubCommand {

    public FunctionNewActivityCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        switch (args[0]) {
            case "1" :
                ActivityData activityData = new ActivityData("test_activity", "test", "test description", "2024-10-19 20-20-00", "2025-10-19 20-20-00");
                activityData.addAward(
                        new AwardData(activityData, "test_award")
                                .checkClaimStatus((activityData1, player) -> activityData1.getData(player.getName()).getBoolean("claimed", false))
                                .checkFinish((activityData1, player) -> activityData1.getData(player.getName()).getBoolean("finished", false))
                                .claim((activityData1, player) -> {
                                    ConfigSection section = activityData1.getData(player.getName());
                                    section.set("claimed", true);
                                    activityData1.setData(player.getName(), section);
                                })
                                .command("give " + AwardData.PLAYER_REPLACEMENT + " 233")
                                .message("Congratulations!")
                );
                GameActivityManager.registerActivity(activityData);
                GameActivityManager.showActivityForm(commandSender.asPlayer(), "test_activity");
                break;
            case "2":
                ActivityData activityData1 = GameActivityManager.getActivity("test_activity");
                ConfigSection configSection = activityData1.getData(commandSender.getName());
                configSection.set("finished", true);
                activityData1.setData(commandSender.getName(), configSection);
                break;
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
