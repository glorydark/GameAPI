package gameapi.commands.defaults.room;

import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.RoomManager;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomAddStartPassCommand extends EasySubCommand {

    public RoomAddStartPassCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender.isOp()) {
            if (args.length == 2) {
                Room room = RoomManager.getRoom(args[0], args[1]);
                if (room != null) {
                    room.setAllowedToStart(false);
                    commandSender.sendMessage("成功设置房间为需管理员开启！");
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
