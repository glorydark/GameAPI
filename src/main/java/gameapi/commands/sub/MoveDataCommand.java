package gameapi.commands.sub;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

/**
 * @author glorydark
 */
public class MoveDataCommand extends EasySubCommand {

    public MoveDataCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 1) {
            Optional<Player> seePlayer = Optional.ofNullable(Server.getInstance().getPlayer(args[0]));
            if (seePlayer.isPresent()) {
                new File(Server.getInstance().getDataPath() + "/players_move/").mkdirs();
                try {
                    Files.move(new File(Server.getInstance().getDataPath() + "/players/" + seePlayer.get().getUniqueId() + ".dat").toPath(), new File(Server.getInstance().getDataPath() + "/players_move/" + seePlayer.get().getUniqueId() + ".dat").toPath());
                    commandSender.sendMessage(TextFormat.GREEN + "移动成功！");
                } catch (IOException e) {
                    e.printStackTrace();;
                }
            } else {
                IPlayer offlinePlayer = Server.getInstance().getOfflinePlayer(args[0]);
                if (offlinePlayer != null) {
                    new File(Server.getInstance().getDataPath() + "/players_move/").mkdirs();
                    try {
                        Files.move(new File(Server.getInstance().getDataPath() + "/players/" + offlinePlayer.getUniqueId().toString() + ".dat").toPath(), new File(Server.getInstance().getDataPath() + "/players_move/" + offlinePlayer.getUniqueId() + ".dat").toPath());
                    } catch (IOException e) {
                        e.printStackTrace();;
                    }
                    commandSender.sendMessage(TextFormat.GREEN + "移动成功！");
                } else {
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.see_uuid.player_not_found", args[0]));
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
