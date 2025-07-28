package gameapi.commands.defaults.manage;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class TransferCommand extends EasySubCommand {

    public TransferCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length != 3) {
            return false;
        }
        Player player = Server.getInstance().getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(TextFormat.RED + "找不到玩家！");
            return false;
        }
        String ip = args[1];
        String port = args[2];
        player.transfer(ip, Integer.parseInt(port));
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
