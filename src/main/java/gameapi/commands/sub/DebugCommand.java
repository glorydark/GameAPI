package gameapi.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class DebugCommand extends EasySubCommand {

    public DebugCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender.isOp() && args.length != 1) {
            return false;
        }
        if (commandSender.isPlayer()) {
            switch (args[0]) {
                case "true":
                    GameAPI.getGameDebugManager().addPlayer((Player) commandSender);
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.debug.on"));
                    break;
                case "false":
                    GameAPI.getGameDebugManager().removePlayer((Player) commandSender);
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.debug.off"));
            }
        } else {
            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.use_in_game"));
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}
