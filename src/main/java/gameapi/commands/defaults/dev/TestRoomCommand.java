package gameapi.commands.defaults.dev;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.room.Room;
import gameapi.room.RoomRule;

/**
 * @author glorydark
 */
public class TestRoomCommand extends EasySubCommand {

    public TestRoomCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        RoomRule roomRule = new RoomRule(0);
        roomRule.setAllowAttackEntityBeforeStart(true);
        roomRule.setAllowAttackPlayerBeforeStart(true);
        Room room = new Room("test", roomRule, 1);
        room.addPlayer(player);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
