package gameapi.commands.defaults.dev;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class SeeSpeedCommand extends EasySubCommand {

    public SeeSpeedCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        commandSender.sendMessage("当前速度：" + player.getMovementSpeed());
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}
