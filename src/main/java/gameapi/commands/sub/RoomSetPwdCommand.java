package gameapi.commands.sub;

import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.RoomManager;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomSetPwdCommand extends EasySubCommand {

    public RoomSetPwdCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender.isOp() && args.length == 3) {
            Room room = RoomManager.getRoom(args[0], args[1]);
            if (room != null) {
                if (room.isAllowedToStart()) {
                    room.setJoinPassword(args[2]);
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.set_password", args[2]));
                }
            } else {
                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found"));
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
