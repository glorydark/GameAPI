package gameapi.commands.defaults.dev;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;

import java.util.Optional;
import java.util.UUID;

/**
 * @author glorydark
 */
public class SeeNameCommand extends EasySubCommand {

    public SeeNameCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 1) {
            UUID uuid = UUID.fromString(args[0]);
            Optional<Player> seePlayer = Server.getInstance().getPlayer(uuid);
            if (seePlayer.isPresent()) {
                GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("command.see_name.success", uuid, seePlayer.get().getName()));
            } else {
                IPlayer offlinePlayer = Server.getInstance().getOfflinePlayer(uuid);
                if (offlinePlayer != null) {
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.see_name.success", uuid, offlinePlayer.getName()));
                } else {
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.see_name.player_not_found", uuid));
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
