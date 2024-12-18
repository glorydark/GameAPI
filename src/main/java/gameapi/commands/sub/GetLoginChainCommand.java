package gameapi.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class GetLoginChainCommand extends EasySubCommand {

    public GetLoginChainCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender.isOp() && args.length == 1) {
            String pn = args[0];
            Player player = Server.getInstance().getPlayerExact(pn);
            commandSender.sendMessage(player.getLoginChainData().getLanguageCode());
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
