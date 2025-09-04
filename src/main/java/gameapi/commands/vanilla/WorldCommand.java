package gameapi.commands.vanilla;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;

public class WorldCommand extends EasySubCommand {

    public WorldCommand(String name) {
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
            String levelName = args[0];
            String playerName = args.length == 2? args[1]: "";
            Level level = Server.getInstance().getLevelByName(levelName);
            if (level == null) {
                if (Server.getInstance().loadLevel(levelName)) {
                    sender.sendMessage(TextFormat.GREEN + "Loading level: " + levelName);
                } else {
                    return false;
                }
            }
            if (level == null) {
                level = Server.getInstance().getLevelByName(levelName);
                if (level == null) {
                    sender.sendMessage(TextFormat.RED + "Failed to load level: " + levelName);
                    return false;
                }
            }
            if (playerName.isEmpty()) {
                if (sender.isPlayer()) {
                    sender.asPlayer().teleport(level.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                } else {
                    sender.sendMessage("You should use it in game!");
                }
            } else {
                Player player = Server.getInstance().getPlayer(playerName);
                if (player == null) {
                    sender.sendMessage("Can not teleport player to world " + levelName + ". Player not found: " + playerName);
                } else {
                    player.teleport(level.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    sender.sendMessage("Teleporting player " + playerName + " to world " + levelName);
                }
            }
        }
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}