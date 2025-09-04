package gameapi.commands.defaults.room;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.manager.RoomManager;
import gameapi.room.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Glorydark
 * For in-game test
 */
public class HubCommand extends Command {

    public final List<String> SURVIVAL_WORLDS = new ArrayList<>(){
        {
            this.add("sc1");
            this.add("nether");
            this.add("the_end");
        }
    };

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
                room.removeSpectator(player);
            } else {
                if (GameAPI.getInstance().isGlorydarkRelatedFeature()) {
                    if (SURVIVAL_WORLDS.contains(player.getLevelName())) {
                        commandSender.sendMessage(TextFormat.GREEN + "已返回主城！");
                        player.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                }
            }
        }
        return false;
    }
}
