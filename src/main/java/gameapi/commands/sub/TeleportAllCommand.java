package gameapi.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class TeleportAllCommand extends EasySubCommand {

    public TeleportAllCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player target = commandSender.asPlayer();
        for (Player value : Server.getInstance().getOnlinePlayers().values()) {
            value.teleport(target);
        }
        commandSender.sendMessage(TextFormat.GREEN + "成功传送所有玩家到当前位置！");
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp() && commandSender.isPlayer();
    }
}
