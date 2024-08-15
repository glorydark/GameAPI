package gameapi.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import gameapi.manager.RoomManager;
import gameapi.room.Room;

/**
 * @author Glorydark
 * For in-game test
 */
public class HubCommand extends Command {

    public HubCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.isPlayer()) {
            Player player = (Player) commandSender;
            Room room = RoomManager.getRoom(player);
            if (room != null) {
                room.removePlayer(player);
            }
        }
        return false;
    }
}
