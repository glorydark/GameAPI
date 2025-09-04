package gameapi.commands.vanilla;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;

public class LoadWorldCommand extends EasySubCommand {

    public LoadWorldCommand(String name) {
        super(name);
        this.getCommandParameters().clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("level_name", false, CommandParamType.STRING)
        });
    }

    // xp player level exp
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length >= 1) {
            Level level = Server.getInstance().getLevelByName(args[0]);
            if (level != null) {
                sender.sendMessage(TextFormat.RED + "Level existed: " + args[0]);
                return false;
            }
            Server.getInstance().loadLevel(args[0]);
            sender.sendMessage(TextFormat.GREEN + "Loading level: " + args[0]);
        }
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}