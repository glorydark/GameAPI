package gameapi.commands.defaults.room;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.RoomManager;
import gameapi.room.Room;
import gameapi.room.utils.reason.QuitRoomReason;

/**
 * @author glorydark
 */
public class RoomQuitCommand extends EasySubCommand {

    public RoomQuitCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Room room = RoomManager.getRoom((Player) commandSender);
        if (room != null) {
            if (room.getPlayers().contains((Player) commandSender)) {
                room.removePlayer((Player) commandSender, QuitRoomReason.PLAYER_LEAVE);
            } else {
                room.removeSpectator((Player) commandSender);
            }
        } else {
            GameAPI.getLanguage().getTranslation("command.error.not_in_game");
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}
