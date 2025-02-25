package gameapi.commands.defaults.room;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.RoomManager;
import gameapi.room.Room;
import gameapi.room.RoomStatus;

/**
 * @author glorydark
 */
public class RoomFastCommand extends EasySubCommand {

    public RoomFastCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Room room = RoomManager.getRoom((Player) commandSender);
        if (room != null) {
            if (room.getRoomStatus() == RoomStatus.ROOM_STATUS_PRESTART) {
                room.setTime(room.getWaitTime() - 1);
            }
        } else {
            GameAPI.getLanguage().getTranslation("command.error.not_in_game");
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
