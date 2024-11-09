package gameapi.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class ResetSpeedCommand extends EasySubCommand {

    public ResetSpeedCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        player.setSprinting(false);
        player.setGliding(false);
        player.setCrawling(false);
        player.setSwimming(false);
        player.setSneaking(false);
        player.removeAllEffects();
        player.teleport(player.add(0, 1,0), null);
        player.setMovementSpeed(0.1f);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}
