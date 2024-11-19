package gameapi.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.RoomManager;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class TeleportIntoRoomCommand extends EasySubCommand {

    public TeleportIntoRoomCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length != 1) {
            return false;
        }
        Player player = commandSender.asPlayer();
        Room room = RoomManager.getRoom(player);
        if (room == null) {
            commandSender.sendMessage(TextFormat.RED + "您还不在房间中！");
            return false;
        }
        if (args[0].equals("@a")) {
            for (Player value : Server.getInstance().getOnlinePlayers().values()) {
                Room room1 = RoomManager.getRoom(value);
                if (room1 != null) {
                    room1.removePlayer(player);
                    room1.removeSpectator(player);
                }
                room.addPlayer(value);
            }
            commandSender.sendMessage(TextFormat.GREEN + "成功让全体玩家加入房间！ ");
        } else {
            Player player1 = Server.getInstance().getPlayer(args[0]);
            if (player1 == null) {
                commandSender.sendMessage(TextFormat.RED + "玩家不在线！");
                return false;
            }
            room.addPlayer(player1);
            commandSender.sendMessage(TextFormat.GREEN + "成功让玩家" + args[0] + "加入到您的房间！");
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp() && commandSender.isPlayer();
    }
}
