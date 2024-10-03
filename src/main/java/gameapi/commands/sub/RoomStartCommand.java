package gameapi.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.RoomManager;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomStartCommand extends EasySubCommand {

    public RoomStartCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player p = (Player) commandSender;
        if (commandSender.isOp()) {
            if (args.length == 2) {
                Room room = RoomManager.getRoom(args[0], args[1]);
                if (room != null) {
                    room.setAllowedToStart(true);
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.start_pass.endowed", room.getRoomName()));
                } else {
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found", args[1]));
                }
                return false;
            }
        }
        Room room = RoomManager.getRoom(p);
        if (room != null) {
            if (room.getRoomAdmins().contains(commandSender.getName()) || commandSender.isOp()) {
                room.setAllowedToStart(true);
                commandSender.sendMessage(TextFormat.GREEN + "您已允许房间开始倒计时！");
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
