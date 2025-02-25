package gameapi.commands.defaults.room;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.RoomManager;
import gameapi.room.Room;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author glorydark
 */
public class RoomStatusCommand extends EasySubCommand {

    public RoomStatusCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        StringBuilder builder = new StringBuilder(GameAPI.getLanguage().getTranslation(commandSender, "command.status.getting") + "\n");
        if (RoomManager.getRoomCount() > 0) {
            for (Map.Entry<String, List<Room>> game : RoomManager.getLoadedRooms().entrySet()) {
                builder.append("\n")
                        .append(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.title", game.getKey()))
                        .append("\n");
                List<Room> rooms = game.getValue();
                if (!rooms.isEmpty()) {
                    for (Room room : rooms) {
                        if (!room.isAllowedToStart()) {
                            builder.append(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.tag.need_start_pass", room.getRoomName(), room.getRoomStatus().toString(), room.getPlayers().size(), room.getMaxPlayer(), room.getMinPlayer()))
                                    .append("\n");
                        } else {
                            builder.append(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.tag.common", room.getRoomName(), room.getRoomStatus().toString(), room.getPlayers().size(), room.getMaxPlayer(), room.getMinPlayer()))
                                    .append("\n");
                        }
                        Set<String> playerNameList = new HashSet<>();
                        for (Player player : room.getPlayers()) {
                            playerNameList.add(player.getName());
                        }
                        builder.append("Players: ")
                                .append(playerNameList.toString().replace("[", "").replace("]", ""))
                                .append("\n");
                        Set<String> spectatorNameList = new HashSet<>();
                        for (Player spectator : room.getSpectators()) {
                            spectatorNameList.add(spectator.getName());
                        }
                        builder.append("Spectators: ")
                                .append(spectatorNameList.toString().replace("[", "").replace("]", ""))
                                .append("\n");
                    }
                } else {
                    builder.append(GameAPI.getLanguage().getTranslation(commandSender, "command.status.no_room_loaded"))
                            .append("\n");
                }

            }
        } else {
            builder.append(GameAPI.getLanguage().getTranslation(commandSender, "command.status.no_game_loaded"))
                    .append("\n");
        }
        builder.append("\n");
        commandSender.sendMessage(builder.toString());
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
