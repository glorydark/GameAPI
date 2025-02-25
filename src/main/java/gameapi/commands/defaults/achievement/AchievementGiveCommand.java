package gameapi.commands.defaults.achievement;

import cn.nukkit.command.CommandSender;
import gameapi.achievement.AchievementManager;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class AchievementGiveCommand extends EasySubCommand {

    public AchievementGiveCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length != 4) {
            return false;
        }
        AchievementManager.endowAchievement(args[0], args[1], args[2], args[3]);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
