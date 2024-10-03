package gameapi.commands.sub.achievement;

import cn.nukkit.command.CommandSender;
import gameapi.achievement.AchievementManager;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class FunctionAchievementReloadCommand extends EasySubCommand {

    public FunctionAchievementReloadCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        AchievementManager.load();
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
