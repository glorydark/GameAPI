package gameapi.commands.vanilla;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;

public class ImprovedXpCommand extends EasySubCommand {

    public ImprovedXpCommand(String name) {
        super(name);
        this.getCommandParameters().clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", false, CommandParamType.TARGET),
                CommandParameter.newType("level", false, CommandParamType.INT),
                CommandParameter.newType("exp", false, CommandParamType.INT)
        });
    }

    // xp player level exp
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 3) {
            String name = args[0];
            if (name.equals("@s")) {
                name = sender.getName();
            }
            Player player = Server.getInstance().getPlayer(name);
            if (player != null) {
                player.setExperience(Integer.parseInt(args[2]), Integer.parseInt(args[1]));
                sender.sendMessage(TextFormat.GREEN + "成功设置玩家的经验等级为 " + args[1] + "，经验：" + args[2]);
            } else {
                sender.sendMessage(TextFormat.RED + "玩家不在线！");
            }
        }
        return true;
    }
}