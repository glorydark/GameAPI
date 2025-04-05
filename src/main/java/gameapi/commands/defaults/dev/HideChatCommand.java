package gameapi.commands.defaults.dev;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public class HideChatCommand extends EasySubCommand {

    public static List<Player> hideMessagePlayers = new ArrayList<>();

    public HideChatCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender.isPlayer()) {
            Player player = (Player) commandSender;
            if (hideMessagePlayers.contains(player)) {
                player.sendMessage(TextFormat.RED + "已关闭消息遮挡！");
                hideMessagePlayers.remove(player);
            } else {
                player.sendMessage(TextFormat.GREEN + "已开启消息遮挡！");
                hideMessagePlayers.add(player);
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
