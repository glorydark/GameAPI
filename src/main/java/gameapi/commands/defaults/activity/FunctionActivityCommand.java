package gameapi.commands.defaults.activity;

import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.data.GameActivityManager;

/**
 * @author glorydark
 */
public class FunctionActivityCommand extends EasySubCommand {

    public FunctionActivityCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 1) {
            GameActivityManager.showActivityForm(commandSender.asPlayer(), args[0]);
        } else {
            GameActivityManager.showAllActivityForm(commandSender.asPlayer());
            ;
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}
