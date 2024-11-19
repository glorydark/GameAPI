package gameapi.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class SudoMCommand extends EasySubCommand {

    public SudoMCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length != 2) {
            return false;
        }
        if (args[0].equals("@a")) {
            for (Player value : Server.getInstance().getOnlinePlayers().values()) {
                value.chat(args[1]);
            }
            commandSender.sendMessage(TextFormat.GREEN + "成功让全体玩家发送消息： " + args[1]);
        } else {
            Player player = Server.getInstance().getPlayer(args[0]);
            if (player == null) {
                commandSender.sendMessage(TextFormat.RED + "玩家不在线！");
                return false;
            }
            player.chat(args[1]);
            commandSender.sendMessage(TextFormat.GREEN + "成功让玩家" + args[0] + "发送消息： " + args[1]);
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
