package gameapi.commands.defaults.manage;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.RoomManager;

/**
 * @author glorydark
 */
public class TeleportAllOutOfGameCommand extends EasySubCommand {

    public TeleportAllOutOfGameCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player target = commandSender.asPlayer();
        for (Player value : Server.getInstance().getOnlinePlayers().values()) {
            if (RoomManager.getRoom(value) == null) {
                value.teleport(target);
            }
        }
        commandSender.sendMessage(TextFormat.GREEN + "成功传送所有未游戏玩家到当前位置！");
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp() && commandSender.isPlayer();
    }
}
