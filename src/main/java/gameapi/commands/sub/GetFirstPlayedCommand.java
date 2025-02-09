package gameapi.commands.sub;

import cn.nukkit.IPlayer;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.CalendarTools;

import java.util.Optional;

/**
 * @author glorydark
 */
public class GetFirstPlayedCommand extends EasySubCommand {

    public GetFirstPlayedCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        IPlayer player;
        if (args.length == 0) {
            player = commandSender.asPlayer();
        } else {
            player = Server.getInstance().getPlayer(args[0]);
            if (player == null) {
                player = Server.getInstance().getOfflinePlayer(args[0]);
            }
        }
        if (player == null) {
            commandSender.sendMessage(TextFormat.RED + "玩家不存在!");
            return false;
        }
        Long l = Optional.ofNullable(player.getFirstPlayed()).orElse(System.currentTimeMillis() / 1000) * 1000;
        commandSender.sendMessage(player.getName() + "第一次游玩的时间是: " + CalendarTools.getDateStringByDefault(l));
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
