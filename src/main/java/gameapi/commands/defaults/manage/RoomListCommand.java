package gameapi.commands.defaults.manage;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;
import gameapi.form.AdvancedFormWindowModal;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import gameapi.manager.RoomManager;
import gameapi.room.Room;

/**
 * @author glorydark
 */
public class RoomListCommand extends EasySubCommand {

    public RoomListCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        this.showCategory(commandSender.asPlayer());
        return false;
    }

    public void showCategory(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("房间列表");
        for (String s : RoomManager.getGameNameList()) {
            simple.addButton(
                    new ResponsiveElementButton(s)
                            .onRespond(player1 -> {
                                showRoomList(player1, s);
                            })
            );
        }
        simple.showToPlayer(player);
    }

    public void showRoomList(Player player, String gameName) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("房间列表 - " + gameName);
        for (Room room : RoomManager.getRooms(gameName)) {
            simple.addButton(
                    new ResponsiveElementButton(
                            room.getRoomName() + "(" + room.getPlayers().size() + "/" + room.getMaxPlayer() + ")\n"
                                    + room.getRoomStatus())
                            .onRespond(player1 -> showRoomJoinChoice(player1, room))
            );
        }
        simple.showToPlayer(player);
    }

    public void showRoomJoinChoice(Player player, Room room) {
        AdvancedFormWindowModal modal = new AdvancedFormWindowModal("提示", "请选择加入方式", TextFormat.GOLD + "加入游戏", TextFormat.GREEN + "加入观察者");
        modal.onTrueButton(room::processPlayerJoin);
        modal.onFalseButton(room::processSpectatorJoin);
        modal.showToPlayer(player);
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp() && commandSender.isPlayer();
    }
}
