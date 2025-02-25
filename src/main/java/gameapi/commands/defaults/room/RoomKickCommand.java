package gameapi.commands.defaults.room;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.RoomManager;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomKickCommand extends EasySubCommand {

    public RoomKickCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Room room = RoomManager.getRoom((Player) commandSender);
        if (commandSender.isOp() || room.getRoomAdmins().contains(commandSender.getName())) {
            if (room != null) {
                Player player = Server.getInstance().getPlayer(args[0]);
                if (player != null) {
                    if (room.getPlayers().contains(player)) {
                        room.removePlayer(player);
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.kick.success"));
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.error.not_in_game.others", player.getName()));
                    }
                } else {
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.player_not_found", args[0]));
                }
            } else {
                GameAPI.getLanguage().getTranslation("command.error.not_in_game");
            }
        }
        return false;
    }
}
