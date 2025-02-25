package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.WorldEditTools;

/**
 * @author glorydark
 */
public class WorldEditBWTestCommand extends EasySubCommand {

    public WorldEditBWTestCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        WorldEditTools.createMultiplePlatform(player, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Block.get(Block.GLASS));
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}