package gameapi.commands.vanilla;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.ParticleTools;

public class ImprovedParticleCommand extends EasySubCommand {

    public ImprovedParticleCommand(String name) {
        super(name);
        this.getCommandParameters().clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("particle", CommandParamType.STRING),
        });
    }

    // xp player level exp
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length > 1) {
            Player player = Server.getInstance().getPlayer(args[0]);
            if (player != null) {
                ParticleTools.addParticleEffect(args[1], player.getLevel(), player.asVector3f());
            }
        }
        return true;
    }
}