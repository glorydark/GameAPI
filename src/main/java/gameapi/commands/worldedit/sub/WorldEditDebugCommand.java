package gameapi.commands.worldedit.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class WorldEditDebugCommand extends EasySubCommand {

    public WorldEditDebugCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        switch (args[0].toLowerCase()) {
            case "true":
                GameAPI.worldEditPlayers.add((Player) commandSender);
                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.world_edit.on"));
                break;
            case "false":
                GameAPI.worldEditPlayers.remove((Player) commandSender);
                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.world_edit.off"));
                break;
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp() && commandSender.isPlayer();
    }
}
