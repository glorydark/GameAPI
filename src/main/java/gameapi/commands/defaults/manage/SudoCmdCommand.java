package gameapi.commands.defaults.manage;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class SudoCmdCommand extends EasySubCommand {

    public SudoCmdCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length != 2) {
            return false;
        }
        if (args[0].equals("@a")) {
            for (Player value : Server.getInstance().getOnlinePlayers().values()) {
                Server.getInstance().dispatchCommand(value, args[1]);
            }
            commandSender.sendMessage(TextFormat.GREEN + "成功让全体玩家执行指令： /" + args[1]);
        } else {
            Player player = Server.getInstance().getPlayer(args[0]);
            if (player == null) {
                commandSender.sendMessage(TextFormat.RED + "玩家不在线！");
                return false;
            }
            Server.getInstance().dispatchCommand(player, args[1]);
            commandSender.sendMessage(TextFormat.GREEN + "成功让玩家" + args[0] + "执行指令： /" + args[1]);
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
