package gameapi.commands.defaults.achievement;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.achievement.AchievementUIFactory;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class AchievementUICommand extends EasySubCommand {

    public AchievementUICommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        AchievementUIFactory.showCategoryMenu((Player) commandSender);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}
