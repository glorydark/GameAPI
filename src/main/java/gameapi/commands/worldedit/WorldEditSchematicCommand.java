package gameapi.commands.worldedit;

import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.SchematicConverter;

/**
 * @author glorydark
 */
public class WorldEditSchematicCommand extends EasySubCommand {

    public WorldEditSchematicCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        SchematicConverter.createBuildFromSchematic(commandSender.asPlayer(), args[0]);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}